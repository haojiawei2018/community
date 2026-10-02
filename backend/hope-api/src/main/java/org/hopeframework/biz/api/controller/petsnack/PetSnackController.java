package org.hopeframework.biz.api.controller.petsnack;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.input.petsnack.PetSnackRequests;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.hopeframework.biz.api.service.petsnack.IPetSnackService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.hopeframework.core.util.WebUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "宠物零食小程序")
@RestController
@RequestMapping("/api/v1/pet-snack")
public class PetSnackController {

    private final IPetSnackService petSnackService;

    public PetSnackController(IPetSnackService petSnackService) {
        this.petSnackService = petSnackService;
    }

    @PassToken
    @ApiOperation("首页、商城初始化")
    @GetMapping("/bootstrap")
    public RespBody<Map<String, Object>> bootstrap() {
        return ResultUtil.success(petSnackService.bootstrap());
    }

    @PassToken
    @ApiOperation("店铺介绍")
    @GetMapping("/store")
    public RespBody<Map<String, Object>> store() {
        return ResultUtil.success(petSnackService.store());
    }

    @PassToken
    @ApiOperation("商品分类")
    @GetMapping("/categories")
    public RespBody<List<Map<String, Object>>> categories() {
        return ResultUtil.success(petSnackService.categories());
    }

    @PassToken
    @ApiOperation("商品列表、搜索和筛选")
    @GetMapping("/products")
    public RespBody<Map<String, Object>> products(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        Long parsedCategoryId = null;
        if (categoryId != null && !categoryId.trim().isEmpty() && !"null".equalsIgnoreCase(categoryId.trim())) {
            try {
                parsedCategoryId = Long.valueOf(categoryId.trim());
            } catch (NumberFormatException exception) {
                throw new org.hopeframework.core.exception.HopeException(400, "商品分类参数不正确");
            }
        }
        return ResultUtil.success(petSnackService.products(parsedCategoryId, keyword, tag, pageNo, pageSize));
    }

    @PassToken
    @ApiOperation("商品详情")
    @GetMapping("/products/{productId}")
    public RespBody<Map<String, Object>> productDetail(@PathVariable Long productId) {
        return ResultUtil.success(petSnackService.productDetail(productId));
    }

    @PassToken
    @ApiOperation("微信小程序登录")
    @PostMapping("/auth/wechat-login")
    public RespBody<TokenResponse> wechatLogin(@RequestBody PetSnackRequests.WechatLoginRequest request,
                                               HttpServletRequest servletRequest) {
        return ResultUtil.success(petSnackService.wechatLogin(request, WebUtils.getClientIP(servletRequest)));
    }

    @UserLoginToken
    @ApiOperation("当前宠物商城用户")
    @GetMapping("/me")
    public RespBody<Map<String, Object>> currentUser() {
        return ResultUtil.success(petSnackService.currentUser());
    }

    @UserLoginToken
    @ApiOperation("更新头像昵称")
    @PutMapping("/me/profile")
    public RespBody<Map<String, Object>> updateProfile(@RequestBody PetSnackRequests.ProfileRequest request) {
        return ResultUtil.success(petSnackService.updateProfile(request));
    }

    @UserLoginToken
    @ApiOperation("上传微信头像")
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RespBody<ImageUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResultUtil.success(petSnackService.uploadAvatar(file));
    }

    @UserLoginToken
    @ApiOperation("收货地址列表")
    @GetMapping("/addresses")
    public RespBody<List<Map<String, Object>>> addresses() {
        return ResultUtil.success(petSnackService.addresses());
    }

    @UserLoginToken
    @ApiOperation("新增收货地址")
    @PostMapping("/addresses")
    public RespBody<Map<String, Object>> createAddress(@RequestBody PetSnackRequests.AddressRequest request) {
        return ResultUtil.success(petSnackService.createAddress(request));
    }

    @UserLoginToken
    @ApiOperation("修改收货地址")
    @PutMapping("/addresses/{addressId}")
    public RespBody<Map<String, Object>> updateAddress(@PathVariable Long addressId,
                                                       @RequestBody PetSnackRequests.AddressRequest request) {
        return ResultUtil.success(petSnackService.updateAddress(addressId, request));
    }

    @UserLoginToken
    @ApiOperation("删除收货地址")
    @DeleteMapping("/addresses/{addressId}")
    public RespBody<Void> deleteAddress(@PathVariable Long addressId) {
        petSnackService.deleteAddress(addressId);
        return ResultUtil.success();
    }

    @UserLoginToken
    @ApiOperation("设置默认收货地址")
    @PutMapping("/addresses/{addressId}/default")
    public RespBody<Void> setDefaultAddress(@PathVariable Long addressId) {
        petSnackService.setDefaultAddress(addressId);
        return ResultUtil.success();
    }

    @UserLoginToken
    @ApiOperation("我的优惠券")
    @GetMapping("/coupons/me")
    public RespBody<Map<String, Object>> coupons(
            @RequestParam(value = "status", defaultValue = "AVAILABLE") String status,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return ResultUtil.success(petSnackService.coupons(status, pageNo, pageSize));
    }

    @UserLoginToken
    @ApiOperation("订单预览")
    @PostMapping("/orders/preview")
    public RespBody<Map<String, Object>> previewOrder(@RequestBody PetSnackRequests.OrderPreviewRequest request) {
        return ResultUtil.success(petSnackService.previewOrder(request));
    }

    @UserLoginToken
    @ApiOperation("创建待支付订单")
    @PostMapping("/orders")
    public RespBody<Map<String, Object>> createOrder(@RequestBody PetSnackRequests.CreateOrderRequest request) {
        return ResultUtil.success(petSnackService.createOrder(request));
    }

    @UserLoginToken
    @ApiOperation("我的订单和状态数量")
    @GetMapping("/orders/me")
    public RespBody<Map<String, Object>> myOrders(
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ResultUtil.success(petSnackService.myOrders(status, pageNo, pageSize));
    }

    @UserLoginToken
    @ApiOperation("订单详情")
    @GetMapping("/orders/{orderId}")
    public RespBody<Map<String, Object>> orderDetail(@PathVariable Long orderId) {
        return ResultUtil.success(petSnackService.orderDetail(orderId));
    }

    @UserLoginToken
    @ApiOperation("继续支付")
    @PostMapping("/orders/{orderId}/payment")
    public RespBody<Map<String, Object>> continuePayment(@PathVariable Long orderId,
                                                         HttpServletRequest servletRequest) {
        return ResultUtil.success(petSnackService.continuePayment(orderId, WebUtils.getClientIP(servletRequest)));
    }

    @UserLoginToken
    @ApiOperation("取消待支付订单")
    @PostMapping("/orders/{orderId}/cancel")
    public RespBody<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        return ResultUtil.success(petSnackService.cancelOrder(orderId));
    }

    @UserLoginToken
    @ApiOperation("确认收货")
    @PostMapping("/orders/{orderId}/confirm-receipt")
    public RespBody<Map<String, Object>> confirmReceipt(@PathVariable Long orderId) {
        return ResultUtil.success(petSnackService.confirmReceipt(orderId));
    }

    @UserLoginToken
    @ApiOperation("申请退款售后")
    @PostMapping("/orders/{orderId}/refunds")
    public RespBody<Map<String, Object>> applyRefund(@PathVariable Long orderId,
                                                     @RequestBody PetSnackRequests.RefundRequest request) {
        return ResultUtil.success(petSnackService.applyRefund(orderId, request));
    }

    @PassToken
    @ApiOperation("帮助中心列表")
    @GetMapping("/help/articles")
    public RespBody<List<Map<String, Object>>> helpArticles() {
        return ResultUtil.success(petSnackService.helpArticles());
    }

    @PassToken
    @ApiOperation("帮助文章详情")
    @GetMapping("/help/articles/{articleId}")
    public RespBody<Map<String, Object>> helpArticle(@PathVariable Long articleId) {
        return ResultUtil.success(petSnackService.helpArticle(articleId));
    }

    @UserLoginToken
    @ApiOperation("提交问题反馈")
    @PostMapping("/feedback")
    public RespBody<Map<String, Object>> createFeedback(@RequestBody PetSnackRequests.FeedbackRequest request) {
        return ResultUtil.success(petSnackService.createFeedback(request));
    }

    @PassToken
    @ApiOperation("微信支付通知预留接口")
    @PostMapping(value = "/payments/wechat/notify", produces = "application/xml;charset=UTF-8")
    public String wechatPaymentNotify() {
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[宠物商城微信支付尚未配置]]></return_msg></xml>";
    }
}
