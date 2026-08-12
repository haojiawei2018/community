package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.entity.input.forum.ActivityCreateRequest;
import org.hopeframework.biz.api.entity.output.forum.TopicResponse;
import org.hopeframework.biz.api.service.forum.ICommunityActivityAdminService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "社区后台活动管理")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1/activities")
public class CommunityAdminActivityController {
    private final ICommunityActivityAdminService activityAdminService;

    public CommunityAdminActivityController(ICommunityActivityAdminService activityAdminService) {
        this.activityAdminService = activityAdminService;
    }

    @ApiOperation("查询活动列表")
    @RequirePermission("announcement.manage")
    @GetMapping
    public RespBody<List<TopicResponse>> list() {
        return ResultUtil.success(activityAdminService.listActivities());
    }

    @ApiOperation("新增活动")
    @RequirePermission("announcement.manage")
    @PostMapping
    public RespBody<TopicResponse> create(@RequestBody ActivityCreateRequest request) {
        return ResultUtil.success(activityAdminService.createActivity(request));
    }

    @ApiOperation("删除活动")
    @RequirePermission("announcement.manage")
    @DeleteMapping("/{activityId}")
    public RespBody<Void> delete(@PathVariable Long activityId) {
        activityAdminService.deleteActivity(activityId);
        return ResultUtil.success();
    }
}
