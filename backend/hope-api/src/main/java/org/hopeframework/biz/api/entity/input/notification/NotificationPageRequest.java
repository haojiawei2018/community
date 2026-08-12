package org.hopeframework.biz.api.entity.input.notification;

import lombok.Data;

@Data
public class NotificationPageRequest {
    private String category;
    private Integer page;
    private Integer pageSize;
}
