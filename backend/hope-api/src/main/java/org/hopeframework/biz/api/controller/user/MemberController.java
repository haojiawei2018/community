package org.hopeframework.biz.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.forum.PostPageRequest;
import org.hopeframework.biz.api.entity.output.forum.PostResponse;
import org.hopeframework.biz.api.entity.output.user.MemberProfileResponse;
import org.hopeframework.biz.api.service.forum.IForumService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "社区成员公开主页")
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final IForumService forumService;

    public MemberController(IForumService forumService) {
        this.forumService = forumService;
    }

    @ApiOperation("查询社区成员公开资料")
    @GetMapping("/{memberId}")
    public RespBody<MemberProfileResponse> profile(@PathVariable Long memberId) {
        return ResultUtil.success(forumService.getMemberProfile(memberId));
    }

    @ApiOperation("分页查询社区成员公开帖子")
    @GetMapping("/{memberId}/posts")
    public RespBody<PageResult<PostResponse>> posts(@PathVariable Long memberId, PostPageRequest request) {
        return ResultUtil.success(forumService.pageMemberPosts(memberId, request));
    }
}
