package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

@Data
public class CommentPageRequest {
    private Integer page;
    private Integer pageSize;
}
