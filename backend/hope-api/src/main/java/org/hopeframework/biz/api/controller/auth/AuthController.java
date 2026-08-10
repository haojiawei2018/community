package org.hopeframework.biz.api.controller.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.input.auth.LoginRequest;
import org.hopeframework.biz.api.entity.input.auth.LogoutRequest;
import org.hopeframework.biz.api.entity.input.auth.RefreshRequest;
import org.hopeframework.biz.api.entity.input.auth.RegisterRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.service.auth.AuthService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.hopeframework.core.util.WebUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "认证与账号")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PassToken
    @ApiOperation("注册全局账号并加入当前租户")
    @PostMapping("/register")
    public RespBody<TokenResponse> register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        return ResultUtil.success(authService.register(request, WebUtils.getClientIP(servletRequest)));
    }

    @PassToken
    @ApiOperation("账号密码登录")
    @PostMapping("/login")
    public RespBody<TokenResponse> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return ResultUtil.success(authService.login(request, WebUtils.getClientIP(servletRequest)));
    }

    @PassToken
    @ApiOperation("轮换刷新令牌")
    @PostMapping("/refresh")
    public RespBody<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        return ResultUtil.success(authService.refresh(request));
    }

    @UserLoginToken
    @ApiOperation("退出并吊销刷新令牌")
    @PostMapping("/logout")
    public RespBody<Void> logout(@RequestBody(required = false) LogoutRequest request) {
        authService.logout(request);
        return ResultUtil.success();
    }
}
