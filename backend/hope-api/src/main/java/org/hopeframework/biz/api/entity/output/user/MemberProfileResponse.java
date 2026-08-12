package org.hopeframework.biz.api.entity.output.user;

import lombok.Data;

import java.util.Date;

@Data
public class MemberProfileResponse {
    private Long memberId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private Date joinedAt;
    private long postCount;
    private long receivedLikeCount;
}
