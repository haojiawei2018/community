package org.hopeframework.biz.api.entity.output.forum;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    private Long id;
    private Long postId;
    private Long userId;
    private Long authorMemberId;
    private String username;
    private String avatar;
    private String content;
    private Long likeCount;
    private Date createTime;
}
