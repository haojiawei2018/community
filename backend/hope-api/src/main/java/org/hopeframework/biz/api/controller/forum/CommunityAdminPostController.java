package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.service.forum.ICommunityPostAdminService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "社区后台帖子管理")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1/posts")
public class CommunityAdminPostController {
    private final ICommunityPostAdminService postAdminService;

    public CommunityAdminPostController(ICommunityPostAdminService postAdminService) {
        this.postAdminService = postAdminService;
    }

    @ApiOperation("删除帖子")
    @RequirePermission("content.review")
    @DeleteMapping("/{postId}")
    public RespBody<Void> delete(@PathVariable Long postId) {
        postAdminService.deletePost(postId);
        return ResultUtil.success();
    }
}
