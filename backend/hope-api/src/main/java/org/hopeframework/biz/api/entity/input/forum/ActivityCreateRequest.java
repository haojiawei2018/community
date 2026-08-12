package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

import java.util.Date;

@Data
public class ActivityCreateRequest {
    private String topicName;
    private Long circleId;
    private String description;
    private String coverUrl;
    private String status;
    private Date startAt;
    private Date endAt;
    private Integer sortOrder;
}
