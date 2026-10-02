package org.hopeframework.biz.api.controller.booking;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.booking.admin.BookingAdminLoginRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingProductRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingCategoryRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingStoreRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingStoreSlotOverrideRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.RescheduleBookingAdminAppointmentRequest;
import org.hopeframework.biz.api.entity.output.booking.admin.BookingAdminDataResponse;
import org.hopeframework.biz.api.entity.output.booking.admin.BookingStoreSlotOverrideResponse;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.service.booking.IBookingAdminService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@Api(tags = "预约小程序后台管理")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1/booking")
public class BookingAdminController {

    private final IBookingAdminService bookingAdminService;
    private final AccessTokenService accessTokenService;
    @Value("${booking.admin.username:admin}")
    private String adminUsername;
    @Value("${booking.admin.password}")
    private String adminPassword;

    public BookingAdminController(IBookingAdminService bookingAdminService,
                                  AccessTokenService accessTokenService) {
        this.bookingAdminService = bookingAdminService;
        this.accessTokenService = accessTokenService;
    }

    @PassToken
    @ApiOperation("预约后台固定账号登录")
    @org.springframework.web.bind.annotation.PostMapping("/login")
    public RespBody<TokenResponse> login(@RequestBody BookingAdminLoginRequest request) {
        if (request == null || !adminUsername.equals(request.getUsername()) || !adminPassword.equals(request.getPassword())) {
            throw new org.hopeframework.core.exception.HopeException(401, "账号或密码错误");
        }
        Long tenantId = TenantContext.requireTenantId();
        AuthPrincipal principal = new AuthPrincipal(0L, 0L, tenantId, "BOOKING_ADMIN");
        UserSessionResponse user = new UserSessionResponse();
        user.setUserId(0L);
        user.setMemberId(0L);
        user.setTenantId(tenantId);
        user.setUsername("admin");
        user.setNickname("预约管理员");
        user.setDisplayName("预约管理员");
        user.setMemberStatus("ACTIVE");
        user.setRoles(Collections.singletonList("BOOKING_ADMIN"));
        user.setPermissions(Arrays.asList("tenant.config.read", "tenant.config.write"));
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.createPermanent(principal));
        response.setExpiresIn(0L);
        response.setUser(user);
        return ResultUtil.success(response);
    }

    @ApiOperation("查询预约后台全部管理数据")
    @RequirePermission("tenant.config.read")
    @GetMapping("/data")
    public RespBody<BookingAdminDataResponse> data() {
        return ResultUtil.success(bookingAdminService.data());
    }

    @ApiOperation("修改预约店铺")
    @RequirePermission("tenant.config.write")
    @PutMapping("/stores/{storeId}")
    public RespBody<BookingAdminDataResponse.StoreItem> updateStore(
            @PathVariable Long storeId, @RequestBody UpdateBookingStoreRequest request) {
        return ResultUtil.success(bookingAdminService.updateStore(storeId, request));
    }

    @ApiOperation("查询指定日期预约档期")
    @RequirePermission("tenant.config.read")
    @GetMapping("/stores/{storeId}/slot-overrides")
    public RespBody<BookingStoreSlotOverrideResponse> getSlotOverride(
            @PathVariable Long storeId, @RequestParam("date") String date) {
        return ResultUtil.success(bookingAdminService.getSlotOverride(storeId, date));
    }

    @ApiOperation("保存指定日期预约档期")
    @RequirePermission("tenant.config.write")
    @PutMapping("/stores/{storeId}/slot-overrides")
    public RespBody<BookingStoreSlotOverrideResponse> updateSlotOverride(
            @PathVariable Long storeId,
            @RequestParam("date") String date,
            @RequestBody UpdateBookingStoreSlotOverrideRequest request) {
        return ResultUtil.success(bookingAdminService.updateSlotOverride(storeId, date, request));
    }

    @ApiOperation("恢复指定日期默认预约档期")
    @RequirePermission("tenant.config.write")
    @DeleteMapping("/stores/{storeId}/slot-overrides")
    public RespBody<BookingStoreSlotOverrideResponse> deleteSlotOverride(
            @PathVariable Long storeId, @RequestParam("date") String date) {
        return ResultUtil.success(bookingAdminService.deleteSlotOverride(storeId, date));
    }

    @ApiOperation("修改预约分类图标")
    @RequirePermission("tenant.config.write")
    @PutMapping("/categories/{categoryId}")
    public RespBody<BookingAdminDataResponse.CategoryItem> updateCategory(
            @PathVariable Long categoryId, @RequestBody UpdateBookingCategoryRequest request) {
        return ResultUtil.success(bookingAdminService.updateCategory(categoryId, request));
    }

    @ApiOperation("新增预约分类")
    @RequirePermission("tenant.config.write")
    @PostMapping("/categories")
    public RespBody<BookingAdminDataResponse.CategoryItem> createCategory(
            @RequestBody UpdateBookingCategoryRequest request) {
        return ResultUtil.success(bookingAdminService.createCategory(request));
    }

    @ApiOperation("修改预约商品")
    @RequirePermission("tenant.config.write")
    @PutMapping("/products/{productId}")
    public RespBody<BookingAdminDataResponse.ProductItem> updateProduct(
            @PathVariable Long productId, @RequestBody UpdateBookingProductRequest request) {
        return ResultUtil.success(bookingAdminService.updateProduct(productId, request));
    }

    @ApiOperation("新增预约商品")
    @RequirePermission("tenant.config.write")
    @PostMapping("/products")
    public RespBody<BookingAdminDataResponse.ProductItem> createProduct(
            @RequestBody UpdateBookingProductRequest request) {
        return ResultUtil.success(bookingAdminService.createProduct(request));
    }

    @ApiOperation("后台取消已预约订单")
    @RequirePermission("tenant.config.write")
    @PostMapping("/appointments/{appointmentId}/cancel")
    public RespBody<BookingAdminDataResponse.AppointmentItem> cancelConfirmedAppointment(
            @PathVariable Long appointmentId) {
        return ResultUtil.success(bookingAdminService.cancelConfirmedAppointment(appointmentId));
    }

    @ApiOperation("后台修改已预约订单档期")
    @RequirePermission("tenant.config.write")
    @PutMapping("/appointments/{appointmentId}/schedule")
    public RespBody<BookingAdminDataResponse.AppointmentItem> rescheduleConfirmedAppointment(
            @PathVariable Long appointmentId,
            @RequestBody RescheduleBookingAdminAppointmentRequest request) {
        return ResultUtil.success(bookingAdminService.rescheduleConfirmedAppointment(appointmentId, request));
    }
}


