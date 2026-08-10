package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.forum.CommentPageRequest;
import org.hopeframework.biz.api.entity.input.forum.CreateCommentRequest;
import org.hopeframework.biz.api.entity.input.forum.CreatePostRequest;
import org.hopeframework.biz.api.entity.input.forum.PostPageRequest;
import org.hopeframework.biz.api.entity.output.forum.CommentResponse;
import org.hopeframework.biz.api.entity.output.forum.LikeResponse;
import org.hopeframework.biz.api.entity.output.forum.PostResponse;
import org.hopeframework.biz.api.service.forum.IForumService;
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

@Api(tags = "用户端帖子与评论")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final IForumService forumService;

    public PostController(IForumService forumService) {
        this.forumService = forumService;
    }

    @ApiOperation("分页查询公开帖子信息流")
    @GetMapping
    public RespBody<PageResult<PostResponse>> page(PostPageRequest request) {
        return ResultUtil.success(forumService.pagePosts(request));
    }

    @ApiOperation("查询公开帖子详情")
    @GetMapping("/{postId}")
    public RespBody<PostResponse> detail(@PathVariable Long postId) {
        return ResultUtil.success(forumService.getPost(postId));
    }

    @UserLoginToken
    @ApiOperation("创建并发布帖子或保存草稿")
    @PostMapping
    public RespBody<PostResponse> create(@RequestBody CreatePostRequest request) {
        return ResultUtil.success(forumService.createPost(request));
    }

    @ApiOperation("分页查询帖子一级评论")
    @GetMapping("/{postId}/comments")
    public RespBody<PageResult<CommentResponse>> comments(@PathVariable Long postId, CommentPageRequest request) {
        return ResultUtil.success(forumService.pageComments(postId, request));
    }

    @UserLoginToken
    @ApiOperation("发表评论")
    @PostMapping("/{postId}/comments")
    public RespBody<CommentResponse> comment(@PathVariable Long postId,
                                             @RequestBody CreateCommentRequest request) {
        return ResultUtil.success(forumService.createComment(postId, request));
    }

    @UserLoginToken
    @ApiOperation("幂等点赞帖子")
    @PutMapping("/{postId}/like")
    public RespBody<LikeResponse> like(@PathVariable Long postId) {
        return ResultUtil.success(forumService.likePost(postId));
    }

    @UserLoginToken
    @ApiOperation("幂等取消帖子点赞")
    @DeleteMapping("/{postId}/like")
    public RespBody<LikeResponse> unlike(@PathVariable Long postId) {
        return ResultUtil.success(forumService.unlikePost(postId));
    }
}
