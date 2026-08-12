package org.hopeframework.biz.api.entity.output.forum;

import lombok.Data;

@Data
public class CircleResponse {
    private Long id;
    private String circleCode;
    private String circleName;
    private String iconUrl;
    private String coverUrl;
    private String description;
    private String joinMode;
    private String status;
    private Integer sortOrder;
    private Long memberCount;
    private Long postCount;
}
