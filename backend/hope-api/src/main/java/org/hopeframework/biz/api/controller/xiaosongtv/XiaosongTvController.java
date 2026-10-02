package org.hopeframework.biz.api.controller.xiaosongtv;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvLoginRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvPhoneCodeRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvPhoneLoginRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvRegisterRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvSaveCdRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.xiaosongtv.XiaosongTvCdResponse;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvService;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvPhoneCodeService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 小松的电视机独立用户端接口，禁止复用其他项目的用户与业务表。 */
@Api(tags = "小松的电视机")
@RestController
@RequestMapping("/api/v1/xiaosong-tv")
public class XiaosongTvController {
    private final IXiaosongTvService xiaosongTvService;
    private final IXiaosongTvPhoneCodeService phoneCodeService;

    public XiaosongTvController(IXiaosongTvService xiaosongTvService,
                                IXiaosongTvPhoneCodeService phoneCodeService) {
        this.xiaosongTvService = xiaosongTvService;
        this.phoneCodeService = phoneCodeService;
    }

    @PassToken
    @ApiOperation("注册小松的电视机账号")
    @PostMapping("/auth/register")
    public RespBody<TokenResponse> register(@RequestBody XiaosongTvRegisterRequest request) {
        return ResultUtil.success(xiaosongTvService.register(request));
    }

    @PassToken
    @ApiOperation("登录小松的电视机账号")
    @PostMapping("/auth/login")
    public RespBody<TokenResponse> login(@RequestBody XiaosongTvLoginRequest request) {
        return ResultUtil.success(xiaosongTvService.login(request));
    }

    @PassToken
    @ApiOperation("发送小松的电视机手机验证码")
    @PostMapping("/auth/phone/send-code")
    public RespBody<Object> sendPhoneCode(@RequestBody XiaosongTvPhoneCodeRequest request) {
        phoneCodeService.send(request == null ? null : request.getPhone());
        return ResultUtil.success(null);
    }

    @PassToken
    @ApiOperation("手机号验证码注册或登录小松的电视机")
    @PostMapping("/auth/phone/login")
    public RespBody<TokenResponse> phoneLogin(@RequestBody XiaosongTvPhoneLoginRequest request) {
        return ResultUtil.success(xiaosongTvService.phoneLogin(request));
    }

    @UserLoginToken
    @ApiOperation("保存当前用户CD")
    @PostMapping("/cds")
    public RespBody<XiaosongTvCdResponse> saveCd(@RequestBody XiaosongTvSaveCdRequest request) {
        return ResultUtil.success(xiaosongTvService.saveCd(request));
    }

    @UserLoginToken
    @ApiOperation("查看当前用户CD列表")
    @GetMapping("/cds")
    public RespBody<List<XiaosongTvCdResponse>> listCds() {
        return ResultUtil.success(xiaosongTvService.listCds());
    }
}
