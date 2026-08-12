package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

@Data
public class PostPageRequest {
    private Long circleId;
    private String keyword;
    private String sort;
    private Boolean following;
    private Integer page;
    private Integer pageSize;
}
