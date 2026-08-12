package org.hopeframework.biz.api.entity.output.user;

import lombok.Data;

@Data
public class UserCommunitySummaryResponse {
    private long postCount;
    private long receivedLikeCount;
    private long commentCount;
}
