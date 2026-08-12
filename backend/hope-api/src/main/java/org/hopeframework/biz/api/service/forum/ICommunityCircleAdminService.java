package org.hopeframework.biz.api.service.forum;

import org.hopeframework.biz.api.entity.input.forum.CircleSaveRequest;
import org.hopeframework.biz.api.entity.output.forum.CircleResponse;

import java.util.List;

public interface ICommunityCircleAdminService {
    List<CircleResponse> listCircles();
    CircleResponse createCircle(CircleSaveRequest request);
    CircleResponse updateCircle(Long circleId, CircleSaveRequest request);
    void deleteCircle(Long circleId);
}
