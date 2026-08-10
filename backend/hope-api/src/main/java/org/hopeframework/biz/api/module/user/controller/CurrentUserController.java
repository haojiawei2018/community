package org.hopeframework.biz.api.module.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.module.auth.dto.UserSessionResponse;
import org.hopeframework.biz.api.module.auth.service.AuthService;
import org.hopeframework.biz.api.module.user.dto.UpdateProfileRequest;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "当前用户")
@UserLoginToken
@RestController
@RequestMapping("/api/v1/users/me")
public class CurrentUserController {

    private final AuthService authService;

    public CurrentUserController(AuthService authService) {
        this.authService = authService;
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
}
