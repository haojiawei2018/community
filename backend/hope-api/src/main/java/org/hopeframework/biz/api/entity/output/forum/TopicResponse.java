package org.hopeframework.biz.api.entity.output.forum;

import lombok.Data;

import java.util.Date;

@Data
public class TopicResponse {
    private Long id;
    private Long circleId;
    private String circleName;
    private String topicName;
    private String description;
    private String coverUrl;
    private String status;
    private Date startAt;
    private Date endAt;
    private Integer sortOrder;
}
