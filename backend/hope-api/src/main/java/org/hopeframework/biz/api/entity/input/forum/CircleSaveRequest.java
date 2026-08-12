package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

@Data
public class CircleSaveRequest {
    private String circleCode;
    private String circleName;
    private String iconUrl;
    private String coverUrl;
    private String description;
    private String joinMode;
    private String status;
    private Integer sortOrder;
}
