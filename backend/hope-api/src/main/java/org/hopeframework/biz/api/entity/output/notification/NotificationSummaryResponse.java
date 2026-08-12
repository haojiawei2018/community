package org.hopeframework.biz.api.entity.output.notification;

import lombok.Data;

@Data
public class NotificationSummaryResponse {
    private long totalUnread;
    private long interactionUnread;
    private long likeUnread;
    private long activityUnread;
    private long systemUnread;
}
