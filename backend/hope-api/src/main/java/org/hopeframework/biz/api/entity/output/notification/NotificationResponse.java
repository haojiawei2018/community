package org.hopeframework.biz.api.entity.output.notification;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationResponse {
    private Long id;
    private String type;
    private String category;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private Boolean read;
    private Date createdAt;
    private Long senderMemberId;
    private String senderName;
    private String senderAvatar;
}
