package org.hopeframework.biz.api.entity.output.forum;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private Long circleId;
    private String circleName;
    private Long userId;
    private Long authorMemberId;
    private String username;
    private String avatar;
    private String postType;
    private String title;
    private String summary;
    private String content;
    private String status;
    private String visibility;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLiked;
    private Date createTime;
    private List<String> images = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
}
