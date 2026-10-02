package org.hopeframework.biz.api.controller.petsnack;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.service.petsnack.IPetSnackWechatPayService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 宠物零食微信支付专用回调入口。
 * 外部地址按现有网关规则为 /api/api/v1/booking/payments/wechat/petnotify，
 * 与预约小程序原有 notify 回调相互隔离。
 */
@Api(tags = "宠物零食微信支付回调")
@RestController
@RequestMapping("/api/v1/booking/payments/wechat")
public class PetSnackPaymentNotifyController {
    private final IPetSnackWechatPayService wechatPayService;

    public PetSnackPaymentNotifyController(IPetSnackWechatPayService wechatPayService) {
        this.wechatPayService = wechatPayService;
    }

    @PassToken
    @ApiOperation("宠物零食微信支付V2结果通知")
    @PostMapping(value = "/petnotify", produces = "application/xml;charset=UTF-8")
    public String petSnackPaymentNotify(@RequestBody String xml) {
        return wechatPayService.handlePaymentNotify(xml);
    }
}
