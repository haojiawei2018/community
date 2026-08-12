package org.hopeframework.biz.api.controller.notification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.notification.NotificationPageRequest;
import org.hopeframework.biz.api.entity.output.notification.NotificationResponse;
import org.hopeframework.biz.api.entity.output.notification.NotificationSummaryResponse;
import org.hopeframework.biz.api.service.notification.INotificationService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户端通知")
@UserLoginToken
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ApiOperation("查询当前成员未读通知汇总")
    @GetMapping("/summary")
    public RespBody<NotificationSummaryResponse> summary() {
        return ResultUtil.success(notificationService.summary());
    }

    @ApiOperation("分页查询当前成员通知")
    @GetMapping
    public RespBody<PageResult<NotificationResponse>> page(NotificationPageRequest request) {
        return ResultUtil.success(notificationService.page(request));
    }

    @ApiOperation("将一条通知标记为已读")
    @PutMapping("/{notificationId}/read")
    public RespBody<NotificationResponse> markRead(@PathVariable Long notificationId) {
        return ResultUtil.success(notificationService.markRead(notificationId));
    }

    @ApiOperation("将当前分类或全部通知标记为已读")
    @PutMapping("/read-all")
    public RespBody<Integer> markAllRead(@RequestParam(required = false) String category) {
        return ResultUtil.success(notificationService.markAllRead(category));
    }
}
