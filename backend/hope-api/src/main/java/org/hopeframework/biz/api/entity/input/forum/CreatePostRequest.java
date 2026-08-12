package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePostRequest {
    private Long circleId;
    private String postType;
    private String title;
    private String content;
    private String visibility;
    private Boolean saveAsDraft;
    private List<String> images = new ArrayList<>();
}
