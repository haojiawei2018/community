package org.hopeframework.biz.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.output.user.CheckInSummaryResponse;
import org.hopeframework.biz.api.service.user.ICheckInService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户端每日签到")
@UserLoginToken
@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInController {
    private final ICheckInService checkInService;

    public CheckInController(ICheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @ApiOperation("查询当前成员签到汇总")
    @GetMapping("/me")
    public RespBody<CheckInSummaryResponse> summary() {
        return ResultUtil.success(checkInService.summary());
    }

    @ApiOperation("完成今日签到")
    @PostMapping
    public RespBody<CheckInSummaryResponse> checkIn() {
        return ResultUtil.success(checkInService.checkIn());
    }
}
