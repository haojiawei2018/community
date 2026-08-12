package org.hopeframework.biz.api.service.notification;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.notification.NotificationPageRequest;
import org.hopeframework.biz.api.entity.output.notification.NotificationResponse;
import org.hopeframework.biz.api.entity.output.notification.NotificationSummaryResponse;
import org.hopeframework.biz.api.model.forum.ForumPost;

public interface INotificationService {
    NotificationSummaryResponse summary();
    PageResult<NotificationResponse> page(NotificationPageRequest request);
    NotificationResponse markRead(Long notificationId);
    int markAllRead(String category);
    void notifyPostAuthor(ForumPost post, Long senderMemberId, String type, String content);
}
