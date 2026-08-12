package org.hopeframework.biz.api.service.forum;

import org.hopeframework.biz.api.entity.input.forum.ActivityCreateRequest;
import org.hopeframework.biz.api.entity.output.forum.TopicResponse;

import java.util.List;

public interface ICommunityActivityAdminService {
    List<TopicResponse> listActivities();
    TopicResponse createActivity(ActivityCreateRequest request);
    void deleteActivity(Long activityId);
}
