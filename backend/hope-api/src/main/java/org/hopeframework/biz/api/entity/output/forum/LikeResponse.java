package org.hopeframework.biz.api.entity.output.forum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponse {
    private Boolean liked;
    private Long likeCount;
}
