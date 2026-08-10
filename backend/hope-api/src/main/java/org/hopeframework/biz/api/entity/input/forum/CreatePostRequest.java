package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

@Data
public class CreatePostRequest {
    private Long circleId;
    private String postType;
    private String title;
    private String content;
    private String visibility;
    private Boolean saveAsDraft;
}
