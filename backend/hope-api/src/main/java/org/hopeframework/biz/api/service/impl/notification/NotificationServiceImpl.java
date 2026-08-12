package org.hopeframework.biz.api.service.impl.notification;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.notification.NotificationPageRequest;
import org.hopeframework.biz.api.entity.output.notification.NotificationResponse;
import org.hopeframework.biz.api.entity.output.notification.NotificationSummaryResponse;
import org.hopeframework.biz.api.mapper.notification.NotificationMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.model.notification.CommunityNotification;
import org.hopeframework.biz.api.model.user.TenantMember;
import org.hopeframework.biz.api.service.notification.INotificationService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements INotificationService {

    private final NotificationMapper notificationMapper;
    private final TenantMemberMapper memberMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper, TenantMemberMapper memberMapper) {
        this.notificationMapper = notificationMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    public NotificationSummaryResponse summary() {
        Long memberId = AuthContext.require().getMemberId();
        List<CommunityNotification> unread = notificationMapper.selectList(
                new LambdaQueryWrapper<CommunityNotification>()
                        .eq(CommunityNotification::getReceiverMemberId, memberId)
                        .eq(CommunityNotification::getReadStatus, 0));
        NotificationSummaryResponse response = new NotificationSummaryResponse();
        for (CommunityNotification item : unread) {
            response.setTotalUnread(response.getTotalUnread() + 1);
            String category = categoryOf(item.getNotificationType());
            if ("LIKE".equals(category)) response.setLikeUnread(response.getLikeUnread() + 1);
            else if ("ACTIVITY".equals(category)) response.setActivityUnread(response.getActivityUnread() + 1);
            else if ("SYSTEM".equals(category)) response.setSystemUnread(response.getSystemUnread() + 1);
            else response.setInteractionUnread(response.getInteractionUnread() + 1);
        }
        return response;
    }

    @Override
    public PageResult<NotificationResponse> page(NotificationPageRequest request) {
        NotificationPageRequest query = request == null ? new NotificationPageRequest() : request;
        int page = query.getPage() == null ? 1 : Math.max(query.getPage(), 1);
        int pageSize = query.getPageSize() == null ? 20 : Math.min(Math.max(query.getPageSize(), 1), 50);
        List<String> types = typesOf(query.getCategory());
        LambdaQueryWrapper<CommunityNotification> wrapper = new LambdaQueryWrapper<CommunityNotification>()
                .eq(CommunityNotification::getReceiverMemberId, AuthContext.require().getMemberId())
                .in(!types.isEmpty(), CommunityNotification::getNotificationType, types)
                .orderByDesc(CommunityNotification::getCreatedAt)
                .orderByDesc(CommunityNotification::getId);
        Page<CommunityNotification> result = notificationMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<NotificationResponse> records = result.getRecords().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationResponse markRead(Long notificationId) {
        if (notificationId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "通知 ID 不能为空");
        }
        Long memberId = AuthContext.require().getMemberId();
        CommunityNotification notification = notificationMapper.selectOne(
                new LambdaQueryWrapper<CommunityNotification>()
                        .eq(CommunityNotification::getId, notificationId)
                        .eq(CommunityNotification::getReceiverMemberId, memberId));
        if (notification == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "通知不存在");
        }
        if (!Integer.valueOf(1).equals(notification.getReadStatus())) {
            notification.setReadStatus(1);
            notification.setReadAt(new Date());
            notificationMapper.updateById(notification);
        }
        return toResponse(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markAllRead(String category) {
        List<String> types = typesOf(category);
        LambdaUpdateWrapper<CommunityNotification> wrapper = new LambdaUpdateWrapper<CommunityNotification>()
                .eq(CommunityNotification::getReceiverMemberId, AuthContext.require().getMemberId())
                .eq(CommunityNotification::getReadStatus, 0)
                .in(!types.isEmpty(), CommunityNotification::getNotificationType, types)
                .set(CommunityNotification::getReadStatus, 1)
                .set(CommunityNotification::getReadAt, new Date());
        return notificationMapper.update(null, wrapper);
    }

    @Override
    public void notifyPostAuthor(ForumPost post, Long senderMemberId, String type, String content) {
        if (post == null || post.getAuthorMemberId() == null || post.getAuthorMemberId().equals(senderMemberId)) return;
        TenantMember sender = memberMapper.selectById(senderMemberId);
        String senderName = sender == null || !StringUtils.hasText(sender.getDisplayName())
                ? "社区成员" : sender.getDisplayName();
        CommunityNotification notification = new CommunityNotification();
        notification.setTenantId(TenantContext.requireTenantId());
        notification.setReceiverMemberId(post.getAuthorMemberId());
        notification.setSenderMemberId(senderMemberId);
        notification.setNotificationType(type);
        notification.setTitle(senderName + ("LIKE".equals(type) ? " 赞了你的帖子" : " 评论了你的帖子"));
        notification.setContent(content);
        notification.setBizType("POST");
        notification.setBizId(post.getId());
        notification.setReadStatus(0);
        notification.setCreatedAt(new Date());
        notification.setDeleted(0);
        notificationMapper.insert(notification);
    }

    private NotificationResponse toResponse(CommunityNotification item) {
        NotificationResponse response = new NotificationResponse();
        response.setId(item.getId());
        response.setType(item.getNotificationType());
        response.setCategory(categoryOf(item.getNotificationType()));
        response.setTitle(item.getTitle());
        response.setContent(item.getContent());
        response.setBizType(item.getBizType());
        response.setBizId(item.getBizId());
        response.setRead(Integer.valueOf(1).equals(item.getReadStatus()));
        response.setCreatedAt(item.getCreatedAt());
        response.setSenderMemberId(item.getSenderMemberId());
        if (item.getSenderMemberId() != null) {
            TenantMember sender = memberMapper.selectById(item.getSenderMemberId());
            if (sender != null) {
                response.setSenderName(sender.getDisplayName());
                response.setSenderAvatar(sender.getAvatarUrl());
            }
        }
        return response;
    }

    private List<String> typesOf(String category) {
        if (!StringUtils.hasText(category)) return Collections.emptyList();
        String value = category.trim().toUpperCase(Locale.ROOT);
        if ("INTERACTION".equals(value)) return Arrays.asList("COMMENT", "REPLY", "FOLLOW");
        if ("LIKE".equals(value)) return Collections.singletonList("LIKE");
        if ("ACTIVITY".equals(value)) return Collections.singletonList("REVIEW");
        if ("SYSTEM".equals(value)) return Collections.singletonList("SYSTEM");
        throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不支持的通知分类");
    }

    private String categoryOf(String type) {
        if ("LIKE".equals(type)) return "LIKE";
        if ("REVIEW".equals(type)) return "ACTIVITY";
        if ("SYSTEM".equals(type)) return "SYSTEM";
        return "INTERACTION";
    }
}
