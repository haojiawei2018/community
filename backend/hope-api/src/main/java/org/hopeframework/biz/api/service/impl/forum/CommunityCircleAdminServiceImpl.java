package org.hopeframework.biz.api.service.impl.forum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.forum.CircleSaveRequest;
import org.hopeframework.biz.api.entity.output.forum.CircleResponse;
import org.hopeframework.biz.api.mapper.forum.ForumCircleMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMapper;
import org.hopeframework.biz.api.model.forum.ForumCircle;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.service.forum.ICommunityCircleAdminService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommunityCircleAdminServiceImpl implements ICommunityCircleAdminService {
    private static final Set<String> JOIN_MODES = new HashSet<>(Arrays.asList("OPEN", "APPROVAL", "PRIVATE"));
    private static final Set<String> STATUSES = new HashSet<>(Arrays.asList("ACTIVE", "INACTIVE"));

    private final ForumCircleMapper circleMapper;
    private final ForumPostMapper postMapper;

    public CommunityCircleAdminServiceImpl(ForumCircleMapper circleMapper, ForumPostMapper postMapper) {
        this.circleMapper = circleMapper;
        this.postMapper = postMapper;
    }

    @Override
    public List<CircleResponse> listCircles() {
        return circleMapper.selectList(new LambdaQueryWrapper<ForumCircle>()
                        .orderByAsc(ForumCircle::getSortOrder)
                        .orderByDesc(ForumCircle::getId))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CircleResponse createCircle(CircleSaveRequest request) {
        validate(request, null);
        AuthPrincipal principal = AuthContext.require();
        Date now = new Date();
        ForumCircle circle = new ForumCircle();
        circle.setTenantId(TenantContext.requireTenantId());
        apply(circle, request);
        circle.setMemberCount(0L);
        circle.setPostCount(0L);
        circle.setCreatedBy(principal.getMemberId());
        circle.setCreatedAt(now);
        circle.setUpdatedBy(principal.getMemberId());
        circle.setUpdatedAt(now);
        circle.setDeleted(0);
        circleMapper.insert(circle);
        return toResponse(circle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CircleResponse updateCircle(Long circleId, CircleSaveRequest request) {
        ForumCircle circle = requireCircle(circleId);
        validate(request, circleId);
        apply(circle, request);
        circle.setUpdatedBy(AuthContext.require().getMemberId());
        circle.setUpdatedAt(new Date());
        circleMapper.updateById(circle);
        return toResponse(circle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCircle(Long circleId) {
        ForumCircle circle = requireCircle(circleId);
        Integer postCount = postMapper.selectCount(new LambdaQueryWrapper<ForumPost>()
                .eq(ForumPost::getCircleId, circle.getId()));
        if (postCount != null && postCount > 0) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "圈子下已有帖子，不能删除，请先迁移或清理内容");
        }
        circleMapper.deleteById(circle.getId());
    }

    private void validate(CircleSaveRequest request, Long currentId) {
        if (request == null || !StringUtils.hasText(request.getCircleCode())
                || !StringUtils.hasText(request.getCircleName())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "圈子编码和名称不能为空");
        }
        String code = request.getCircleCode().trim().toLowerCase(Locale.ROOT);
        String name = request.getCircleName().trim();
        if (!code.matches("[a-z0-9][a-z0-9_-]{1,63}")) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "圈子编码需为 2-64 位小写字母、数字、横线或下划线");
        }
        if (name.length() > 64) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "圈子名称不能超过 64 个字符");
        }
        if (StringUtils.hasText(request.getDescription()) && request.getDescription().trim().length() > 500) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "圈子简介不能超过 500 个字符");
        }
        validateUrl(request.getIconUrl(), "圈子图标");
        validateUrl(request.getCoverUrl(), "圈子封面");
        String joinMode = normalize(request.getJoinMode(), "OPEN");
        String status = normalize(request.getStatus(), "ACTIVE");
        if (!JOIN_MODES.contains(joinMode)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不支持的加入方式");
        }
        if (!STATUSES.contains(status)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不支持的圈子状态");
        }
        ForumCircle duplicate = circleMapper.selectOne(new LambdaQueryWrapper<ForumCircle>()
                .eq(ForumCircle::getCircleCode, code)
                .ne(currentId != null, ForumCircle::getId, currentId)
                .last("LIMIT 1"));
        if (duplicate != null) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "圈子编码已存在");
        }
    }

    private void validateUrl(String url, String label) {
        if (StringUtils.hasText(url) && url.trim().length() > 512) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), label + "地址不能超过 512 个字符");
        }
    }

    private void apply(ForumCircle circle, CircleSaveRequest request) {
        circle.setCircleCode(request.getCircleCode().trim().toLowerCase(Locale.ROOT));
        circle.setCircleName(request.getCircleName().trim());
        circle.setIconUrl(trimToNull(request.getIconUrl()));
        circle.setCoverUrl(trimToNull(request.getCoverUrl()));
        circle.setDescription(trimToNull(request.getDescription()));
        circle.setJoinMode(normalize(request.getJoinMode(), "OPEN"));
        circle.setStatus(normalize(request.getStatus(), "ACTIVE"));
        circle.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private ForumCircle requireCircle(Long circleId) {
        if (circleId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "圈子 ID 不能为空");
        }
        ForumCircle circle = circleMapper.selectById(circleId);
        if (circle == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "圈子不存在");
        }
        return circle;
    }

    private CircleResponse toResponse(ForumCircle circle) {
        CircleResponse response = new CircleResponse();
        response.setId(circle.getId());
        response.setCircleCode(circle.getCircleCode());
        response.setCircleName(circle.getCircleName());
        response.setIconUrl(circle.getIconUrl());
        response.setCoverUrl(circle.getCoverUrl());
        response.setDescription(circle.getDescription());
        response.setJoinMode(circle.getJoinMode());
        response.setStatus(circle.getStatus());
        response.setSortOrder(circle.getSortOrder());
        response.setMemberCount(circle.getMemberCount() == null ? 0L : circle.getMemberCount());
        response.setPostCount(circle.getPostCount() == null ? 0L : circle.getPostCount());
        return response;
    }

    private String normalize(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase(Locale.ROOT) : fallback;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
