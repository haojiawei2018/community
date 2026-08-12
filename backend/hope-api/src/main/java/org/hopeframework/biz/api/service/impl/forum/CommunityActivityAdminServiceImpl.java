package org.hopeframework.biz.api.service.impl.forum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.forum.ActivityCreateRequest;
import org.hopeframework.biz.api.entity.output.forum.TopicResponse;
import org.hopeframework.biz.api.mapper.forum.ForumCircleMapper;
import org.hopeframework.biz.api.mapper.forum.ForumTopicMapper;
import org.hopeframework.biz.api.model.forum.ForumCircle;
import org.hopeframework.biz.api.model.forum.ForumTopic;
import org.hopeframework.biz.api.service.forum.ICommunityActivityAdminService;
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
public class CommunityActivityAdminServiceImpl implements ICommunityActivityAdminService {
    private static final Set<String> STATUSES = new HashSet<>(Arrays.asList("ACTIVE", "INACTIVE"));

    private final ForumTopicMapper topicMapper;
    private final ForumCircleMapper circleMapper;

    public CommunityActivityAdminServiceImpl(ForumTopicMapper topicMapper, ForumCircleMapper circleMapper) {
        this.topicMapper = topicMapper;
        this.circleMapper = circleMapper;
    }

    @Override
    public List<TopicResponse> listActivities() {
        return topicMapper.selectList(new LambdaQueryWrapper<ForumTopic>()
                        .orderByAsc(ForumTopic::getSortOrder)
                        .orderByDesc(ForumTopic::getId))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TopicResponse createActivity(ActivityCreateRequest request) {
        validate(request);
        AuthPrincipal principal = AuthContext.require();
        Date now = new Date();
        ForumTopic topic = new ForumTopic();
        topic.setTenantId(TenantContext.requireTenantId());
        topic.setCircleId(request.getCircleId());
        topic.setTopicName(request.getTopicName().trim());
        topic.setDescription(trimToNull(request.getDescription()));
        topic.setCoverUrl(trimToNull(request.getCoverUrl()));
        topic.setStatus(normalize(request.getStatus(), "ACTIVE"));
        topic.setStartAt(request.getStartAt());
        topic.setEndAt(request.getEndAt());
        topic.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        topic.setCreatedBy(principal.getMemberId());
        topic.setCreatedAt(now);
        topic.setUpdatedBy(principal.getMemberId());
        topic.setUpdatedAt(now);
        topic.setDeleted(0);
        topicMapper.insert(topic);
        return toResponse(topic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long activityId) {
        if (activityId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "活动 ID 不能为空");
        }
        ForumTopic topic = topicMapper.selectById(activityId);
        if (topic == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "活动不存在或已删除");
        }
        topicMapper.deleteById(topic.getId());
    }

    private void validate(ActivityCreateRequest request) {
        if (request == null || !StringUtils.hasText(request.getTopicName())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "活动名称不能为空");
        }
        if (request.getTopicName().trim().length() > 128) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "活动名称不能超过 128 个字符");
        }
        if (StringUtils.hasText(request.getDescription()) && request.getDescription().trim().length() > 512) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "活动简介不能超过 512 个字符");
        }
        if (StringUtils.hasText(request.getCoverUrl()) && request.getCoverUrl().trim().length() > 512) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "活动封面地址不能超过 512 个字符");
        }
        String status = normalize(request.getStatus(), "ACTIVE");
        if (!STATUSES.contains(status)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不支持的活动状态");
        }
        if (request.getStartAt() != null && request.getEndAt() != null
                && !request.getEndAt().after(request.getStartAt())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "活动结束时间必须晚于开始时间");
        }
        if (request.getCircleId() != null) {
            ForumCircle circle = circleMapper.selectById(request.getCircleId());
            if (circle == null || !"ACTIVE".equals(circle.getStatus())) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "关联圈子不存在或已停用");
            }
        }
    }

    private TopicResponse toResponse(ForumTopic topic) {
        TopicResponse response = new TopicResponse();
        response.setId(topic.getId());
        response.setCircleId(topic.getCircleId());
        ForumCircle circle = topic.getCircleId() == null ? null : circleMapper.selectById(topic.getCircleId());
        response.setCircleName(circle == null ? null : circle.getCircleName());
        response.setTopicName(topic.getTopicName());
        response.setDescription(topic.getDescription());
        response.setCoverUrl(topic.getCoverUrl());
        response.setStatus(topic.getStatus());
        response.setStartAt(topic.getStartAt());
        response.setEndAt(topic.getEndAt());
        response.setSortOrder(topic.getSortOrder());
        return response;
    }

    private String normalize(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase(Locale.ROOT) : fallback;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
