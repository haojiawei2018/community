package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.input.forum.CreateReportRequest;
import org.hopeframework.biz.api.service.forum.IUserSafetyService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.hopeframework.core.exception.HopeException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户举报与屏蔽")
@UserLoginToken
@RestController
@RequestMapping("/api/v1")
public class UserSafetyController {
    private final IUserSafetyService safetyService;

    public UserSafetyController(IUserSafetyService safetyService) {
        this.safetyService = safetyService;
    }

    @ApiOperation("举报帖子")
    @PostMapping("/posts/{postId}/reports")
    public RespBody<Void> reportPost(@PathVariable Long postId, @RequestBody CreateReportRequest request) {
        try {
            safetyService.reportPost(postId, request);
            return ResultUtil.success();
        } catch (HopeException ex) {
            return new RespBody<>(ex.getCode(), ex.getMessage());
        }
    }

    @ApiOperation("屏蔽社区成员")
    @PutMapping("/members/{memberId}/block")
    public RespBody<Void> blockMember(@PathVariable Long memberId) {
        try {
            safetyService.blockMember(memberId);
            return ResultUtil.success();
        } catch (HopeException ex) {
            return new RespBody<>(ex.getCode(), ex.getMessage());
        }
    }
}
