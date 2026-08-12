package org.hopeframework.biz.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.service.auth.IAuthService;
import org.hopeframework.biz.api.entity.input.user.UpdateProfileRequest;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.forum.PostPageRequest;
import org.hopeframework.biz.api.entity.output.forum.PostResponse;
import org.hopeframework.biz.api.entity.output.user.UserCommunitySummaryResponse;
import org.hopeframework.biz.api.service.forum.IForumService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "当前用户")
@UserLoginToken
@RestController
@RequestMapping("/api/v1/users/me")
public class CurrentUserController {

    private final IAuthService authService;
    private final IForumService forumService;

    public CurrentUserController(IAuthService authService, IForumService forumService) {
        this.authService = authService;
        this.forumService = forumService;
    }

    @ApiOperation("当前全局账号和租户成员信息")
    @GetMapping
    public RespBody<UserSessionResponse> currentUser() {
        return ResultUtil.success(authService.currentUser());
    }

    @ApiOperation("修改当前租户成员资料")
    @PutMapping
    public RespBody<UserSessionResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResultUtil.success(authService.updateProfile(request));
    }

    @ApiOperation("当前成员社区数据汇总")
    @GetMapping("/summary")
    public RespBody<UserCommunitySummaryResponse> summary() {
        return ResultUtil.success(forumService.currentUserSummary());
    }

    @ApiOperation("分页查询当前成员发布的帖子")
    @GetMapping("/posts")
    public RespBody<PageResult<PostResponse>> posts(PostPageRequest request) {
        return ResultUtil.success(forumService.pageCurrentUserPosts(request));
    }

    @ApiOperation("删除当前成员自己发布的帖子")
    @DeleteMapping("/posts/{postId}")
    public RespBody<Void> deletePost(@PathVariable Long postId) {
        forumService.deleteCurrentUserPost(postId);
        return ResultUtil.success();
    }
}
