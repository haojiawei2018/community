package org.hopeframework.biz.api.service.impl.forum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.forum.CreateReportRequest;
import org.hopeframework.biz.api.mapper.forum.ForumPostMapper;
import org.hopeframework.biz.api.mapper.forum.ForumReportMapper;
import org.hopeframework.biz.api.mapper.forum.UserBlockMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.model.forum.ForumReport;
import org.hopeframework.biz.api.model.forum.UserBlock;
import org.hopeframework.biz.api.model.user.TenantMember;
import org.hopeframework.biz.api.service.forum.IUserSafetyService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Service
public class UserSafetyServiceImpl implements IUserSafetyService {
    private final ForumPostMapper postMapper;
    private final ForumReportMapper reportMapper;
    private final UserBlockMapper blockMapper;
    private final TenantMemberMapper memberMapper;

    public UserSafetyServiceImpl(ForumPostMapper postMapper, ForumReportMapper reportMapper,
                                 UserBlockMapper blockMapper, TenantMemberMapper memberMapper) {
        this.postMapper = postMapper;
        this.reportMapper = reportMapper;
        this.blockMapper = blockMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportPost(Long postId, CreateReportRequest request) {
        AuthPrincipal principal = AuthContext.require();
        ForumPost post = postMapper.selectById(postId);
        if (post == null || !"PUBLISHED".equals(post.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "帖子不存在或不可举报");
        }
        if (Objects.equals(post.getAuthorMemberId(), principal.getMemberId())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不能举报自己发布的帖子");
        }
        String reasonCode = request == null ? null : request.getReasonCode();
        if (!StringUtils.hasText(reasonCode)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "请选择举报原因");
        }
        Integer pending = reportMapper.selectCount(new LambdaQueryWrapper<ForumReport>()
                .eq(ForumReport::getReporterMemberId, principal.getMemberId())
                .eq(ForumReport::getTargetType, "POST")
                .eq(ForumReport::getTargetId, postId)
                .in(ForumReport::getStatus, "PENDING", "PROCESSING"));
        if (pending != null && pending > 0) return;
        Date now = new Date();
        ForumReport report = new ForumReport();
        report.setTenantId(TenantContext.requireTenantId());
        report.setReporterMemberId(principal.getMemberId());
        report.setTargetType("POST");
        report.setTargetId(postId);
        report.setReasonCode(reasonCode.trim());
        report.setReasonText(request.getReasonText());
        report.setStatus("PENDING");
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        reportMapper.insert(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void blockMember(Long memberId) {
        AuthPrincipal principal = AuthContext.require();
        if (memberId == null) throw new HopeException(HttpStatus.BAD_REQUEST.value(), "成员 ID 不能为空");
        if (Objects.equals(memberId, principal.getMemberId())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不能屏蔽自己");
        }
        TenantMember member = memberMapper.selectById(memberId);
        if (member == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "社区成员不存在");
        Integer exists = blockMapper.selectCount(new LambdaQueryWrapper<UserBlock>()
                .eq(UserBlock::getMemberId, principal.getMemberId())
                .eq(UserBlock::getBlockedMemberId, memberId));
        if (exists != null && exists > 0) return;
        UserBlock block = new UserBlock();
        block.setTenantId(TenantContext.requireTenantId());
        block.setMemberId(principal.getMemberId());
        block.setBlockedMemberId(memberId);
        block.setCreatedAt(new Date());
        blockMapper.insert(block);
    }
}
