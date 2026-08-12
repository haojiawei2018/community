package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.entity.input.forum.CircleSaveRequest;
import org.hopeframework.biz.api.entity.output.forum.CircleResponse;
import org.hopeframework.biz.api.service.forum.ICommunityCircleAdminService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "社区后台圈子管理")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1/circles")
public class CommunityAdminCircleController {
    private final ICommunityCircleAdminService circleAdminService;

    public CommunityAdminCircleController(ICommunityCircleAdminService circleAdminService) {
        this.circleAdminService = circleAdminService;
    }

    @ApiOperation("查询全部圈子")
    @RequirePermission("circle.manage")
    @GetMapping
    public RespBody<List<CircleResponse>> list() {
        return ResultUtil.success(circleAdminService.listCircles());
    }

    @ApiOperation("新增圈子")
    @RequirePermission("circle.manage")
    @PostMapping
    public RespBody<CircleResponse> create(@RequestBody CircleSaveRequest request) {
        return ResultUtil.success(circleAdminService.createCircle(request));
    }

    @ApiOperation("修改圈子")
    @RequirePermission("circle.manage")
    @PutMapping("/{circleId}")
    public RespBody<CircleResponse> update(@PathVariable Long circleId,
                                           @RequestBody CircleSaveRequest request) {
        return ResultUtil.success(circleAdminService.updateCircle(circleId, request));
    }

    @ApiOperation("删除空圈子")
    @RequirePermission("circle.manage")
    @DeleteMapping("/{circleId}")
    public RespBody<Void> delete(@PathVariable Long circleId) {
        circleAdminService.deleteCircle(circleId);
        return ResultUtil.success();
    }
}
