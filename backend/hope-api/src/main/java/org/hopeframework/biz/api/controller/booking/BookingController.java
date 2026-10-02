package org.hopeframework.biz.api.controller.booking;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.input.booking.CreateBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.input.booking.RescheduleBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.input.booking.BookingWechatLoginRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAppointmentListResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAppointmentResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAvailabilityResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingBootstrapResponse;
import org.hopeframework.biz.api.service.booking.IBookingService;
import org.hopeframework.biz.api.service.booking.IBookingWechatAuthService;
import org.hopeframework.biz.api.service.booking.IBookingWechatPayService;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.hopeframework.core.util.WebUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "预约小程序")
@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final IBookingService bookingService;
    private final IBookingWechatAuthService wechatAuthService;
    private final IBookingWechatPayService wechatPayService;

    public BookingController(IBookingService bookingService,
                             IBookingWechatAuthService wechatAuthService,
                             IBookingWechatPayService wechatPayService) {
        this.bookingService = bookingService;
        this.wechatAuthService = wechatAuthService;
        this.wechatPayService = wechatPayService;
    }

    @PassToken
    @ApiOperation("预约小程序微信一键登录")
    @PostMapping("/auth/wechat-login")
    public RespBody<TokenResponse> wechatLogin(@RequestBody BookingWechatLoginRequest request,
                                               HttpServletRequest servletRequest) {
        try {
            return ResultUtil.success(wechatAuthService.login(request, WebUtils.getClientIP(servletRequest)));
        } catch (HopeException exception) {
            // 共用论坛项目的全局异常处理器不在当前扫描包下，预约登录接口按原有 RespBody 协议返回业务错误。
            return new RespBody<>(exception.getCode(), exception.getMessage(), null);
        }
    }

    @PassToken
    @ApiOperation("初始化首页、分类、商品详情和店铺数据")
    @GetMapping("/bootstrap")
    public RespBody<BookingBootstrapResponse> bootstrap(
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return ResultUtil.success(bookingService.bootstrap(storeId));
    }

    @PassToken
    @ApiOperation("查询指定日期的可预约档期")
    @GetMapping("/availability")
    public RespBody<BookingAvailabilityResponse> availability(
            @RequestParam("storeId") Long storeId,
            @RequestParam("productId") Long productId,
            @RequestParam("date") String date) {
        return ResultUtil.success(bookingService.availability(storeId, productId, date));
    }

    @UserLoginToken
    @ApiOperation("创建待支付预约单")
    @PostMapping("/appointments")
    public RespBody<BookingAppointmentResponse> createAppointment(
            @RequestBody CreateBookingAppointmentRequest request,
            HttpServletRequest servletRequest) {
        try {
            return ResultUtil.success(bookingService.createAppointment(
                    request, WebUtils.getClientIP(servletRequest)));
        } catch (HopeException exception) {
            return new RespBody<>(exception.getCode(), exception.getMessage(), null);
        }
    }

    @UserLoginToken
    @ApiOperation("待支付预约单继续支付")
    @PostMapping("/appointments/{appointmentId}/payment")
    public RespBody<BookingAppointmentResponse> continuePayment(
            @PathVariable Long appointmentId, HttpServletRequest servletRequest) {
        try {
            return ResultUtil.success(bookingService.continuePayment(
                    appointmentId, WebUtils.getClientIP(servletRequest)));
        } catch (HopeException exception) {
            return new RespBody<>(exception.getCode(), exception.getMessage(), null);
        }
    }

    @UserLoginToken
    @ApiOperation("取消待支付预约单")
    @PostMapping("/appointments/{appointmentId}/cancel")
    public RespBody<BookingAppointmentResponse> cancelPendingAppointment(@PathVariable Long appointmentId) {
        try {
            return ResultUtil.success(bookingService.cancelPendingAppointment(appointmentId));
        } catch (HopeException exception) {
            return new RespBody<>(exception.getCode(), exception.getMessage(), null);
        }
    }

    @PassToken
    @ApiOperation("微信支付V2结果通知")
    @PostMapping(value = "/payments/wechat/notify", produces = "application/xml;charset=UTF-8")
    public String wechatPaymentNotify(@RequestBody String xml) {
        return wechatPayService.handlePaymentNotify(xml);
    }

    @UserLoginToken
    @ApiOperation("查询我的预约及状态数量")
    @GetMapping("/appointments/me")
    public RespBody<BookingAppointmentListResponse> myAppointments() {
        return ResultUtil.success(bookingService.myAppointments());
    }

    @UserLoginToken
    @ApiOperation("修改已预约订单的到店档期")
    @PutMapping("/appointments/{appointmentId}/schedule")
    public RespBody<BookingAppointmentResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestBody RescheduleBookingAppointmentRequest request) {
        try {
            return ResultUtil.success(bookingService.rescheduleAppointment(appointmentId, request));
        } catch (HopeException exception) {
            return new RespBody<>(exception.getCode(), exception.getMessage(), null);
        }
    }
}
