package org.hopeframework.biz.api.service.forum;

import org.hopeframework.biz.api.entity.input.forum.CreateReportRequest;

public interface IUserSafetyService {
    void reportPost(Long postId, CreateReportRequest request);
    void blockMember(Long memberId);
}
