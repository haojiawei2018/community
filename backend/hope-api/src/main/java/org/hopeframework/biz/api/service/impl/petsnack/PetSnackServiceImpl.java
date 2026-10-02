package org.hopeframework.biz.api.service.impl.petsnack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hopeframework.biz.api.common.security.PetSnackAccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.petsnack.PetSnackRequests;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper;
import org.hopeframework.biz.api.service.file.IImageStorageService;
import org.hopeframework.biz.api.service.petsnack.IPetSnackService;
import org.hopeframework.biz.api.service.petsnack.IPetSnackWechatPayService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class PetSnackServiceImpl implements IPetSnackService {

    private static final int ORDER_EXPIRE_MINUTES = 15;
    private static final int MAX_ORDER_ITEM_TYPES = 50;
    private static final int MAX_ITEM_QUANTITY = 99;
    private static final SimpleDateFormat ORDER_NO_TIME = new SimpleDateFormat("yyyyMMddHHmmss");

    private final PetSnackMapper mapper;
    private final PetSnackAccessTokenService accessTokenService;
    private final IImageStorageService imageStorageService;
    private final IPetSnackWechatPayService wechatPayService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String appId;
    private final String appSecret;

    public PetSnackServiceImpl(PetSnackMapper mapper,
                               PetSnackAccessTokenService accessTokenService,
                               IImageStorageService imageStorageService,
                               IPetSnackWechatPayService wechatPayService,
                               @Value("${pet-snack.wechat.app-id:}") String appId,
                               @Value("${pet-snack.wechat.app-secret:}") String appSecret) {
        this.mapper = mapper;
        this.accessTokenService = accessTokenService;
        this.imageStorageService = imageStorageService;
        this.wechatPayService = wechatPayService;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse wechatLogin(PetSnackRequests.WechatLoginRequest request, String clientIp) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            throw badRequest("微信登录code不能为空");
        }
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(appSecret)) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "宠物商城微信登录尚未配置AppID或AppSecret");
        }
        JSONObject session = code2Session(request.getCode().trim());
        String openid = session.getString("openid");
        if (!StringUtils.hasText(openid)) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "微信登录未返回openid");
        }

        Map<String, Object> user = mapper.selectUserByWechat(appId, openid);
        if (user == null) {
            user = new HashMap<>();
            user.put("appId", appId);
            user.put("openid", openid);
            user.put("unionid", session.getString("unionid"));
            user.put("nickname", "微信用户" + openid.substring(Math.max(0, openid.length() - 6)));
            user.put("clientIp", clientIp);
            mapper.insertUser(user);
            user = mapper.selectUserByWechat(appId, openid);
        } else {
            ensureActiveUser(user);
            mapper.updateUserLogin(idOf(user, "id"), session.getString("unionid"), clientIp);
            user = mapper.selectUserByWechat(appId, openid);
        }
        ensureActiveUser(user);

        Long userId = idOf(user, "id");
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.create(userId));
        response.setExpiresIn(0L);
        response.setUser(toSession(user));
        return response;
    }

    @Override
    public Map<String, Object> bootstrap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("store", store());
        result.put("banners", normalizeBanners(mapper.selectBanners()));
        result.put("categories", categories());
        result.put("hotProducts", normalizeProductList(mapper.selectProducts(null, null, "HOT", 0, 20)));
        result.put("newProducts", normalizeProductList(mapper.selectProducts(null, null, "NEW", 0, 20)));
        return result;
    }

    @Override
    public Map<String, Object> store() {
        Map<String, Object> store = mapper.selectStore();
        if (store == null) throw notFound("暂无可用店铺");
        Object tags = store.remove("serviceTagsJson");
        store.put("serviceTags", jsonStringList(tags));
        return store;
    }

    @Override
    public List<Map<String, Object>> categories() {
        return mapper.selectCategories();
    }

    @Override
    public Map<String, Object> products(Long categoryId, String keyword, String tag, int pageNo, int pageSize) {
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.min(50, Math.max(1, pageSize));
        String safeKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String safeTag = StringUtils.hasText(tag) ? tag.trim().toUpperCase(Locale.ROOT) : null;
        if (safeTag != null && !"HOT".equals(safeTag) && !"NEW".equals(safeTag)) {
            throw badRequest("tag只支持HOT或NEW");
        }
        int offset = (safePageNo - 1) * safePageSize;
        List<Map<String, Object>> list = mapper.selectProducts(categoryId, safeKeyword, safeTag, offset, safePageSize);
        Map<String, Object> result = pageResult(safePageNo, safePageSize,
                mapper.countProducts(categoryId, safeKeyword, safeTag), normalizeProductList(list));
        return result;
    }

    @Override
    public Map<String, Object> productDetail(Long productId) {
        if (productId == null) throw badRequest("商品ID不能为空");
        Map<String, Object> product = mapper.selectProduct(productId);
        if (product == null) throw notFound("商品不存在或已下架");
        normalizeMoney(product, "salePriceYuan", "salePrice");
        normalizeMoney(product, "marketPriceYuan", "marketPrice");
        Object gallery = product.remove("galleryUrlsJson");
        product.put("galleryUrls", jsonStringList(gallery));
        List<Map<String, Object>> skus = mapper.selectProductSkus(productId);
        for (Map<String, Object> sku : skus) {
            normalizeMoney(sku, "salePriceYuan", "salePrice");
            normalizeMoney(sku, "marketPriceYuan", "marketPrice");
            sku.put("soldOut", intValue(sku.get("stock")) <= 0);
        }
        product.put("skus", skus);
        return product;
    }

    @Override
    public Map<String, Object> currentUser() {
        Map<String, Object> user = requireUser();
        user.put("userId", user.remove("id"));
        user.put("profileCompleted", StringUtils.hasText(stringValue(user.get("avatarUrl")))
                && StringUtils.hasText(stringValue(user.get("nickname"))));
        return user;
    }

    @Override
    public Map<String, Object> updateProfile(PetSnackRequests.ProfileRequest request) {
        if (request == null) throw badRequest("用户资料不能为空");
        String nickname = text(request.getNickname(), "昵称", 1, 40);
        String avatarUrl = text(request.getAvatarUrl(), "头像", 1, 500);
        Long userId = currentUserId();
        if (mapper.updateUserProfile(userId, nickname, avatarUrl) != 1) {
            throw notFound("用户不存在或已禁用");
        }
        return currentUser();
    }

    @Override
    public ImageUploadResponse uploadAvatar(MultipartFile file) {
        Long userId = currentUserId();
        ImageUploadResponse response = imageStorageService.upload(file);
        if (mapper.updateUserAvatar(userId, response.getUrl()) != 1) {
            throw notFound("用户不存在或已禁用");
        }
        return response;
    }

    @Override
    public List<Map<String, Object>> addresses() {
        List<Map<String, Object>> list = mapper.selectAddresses(currentUserId());
        for (Map<String, Object> address : list) addFullAddress(address);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createAddress(PetSnackRequests.AddressRequest request) {
        Long userId = currentUserId();
        Map<String, Object> address = addressValues(request, userId, null);
        boolean firstAddress = mapper.countAddresses(userId) == 0;
        boolean makeDefault = firstAddress || Boolean.TRUE.equals(request.getIsDefault());
        if (makeDefault) mapper.clearDefaultAddresses(userId);
        address.put("isDefault", makeDefault ? 1 : 0);
        mapper.insertAddress(address);
        return requireAddress(idOf(address, "id"), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateAddress(Long addressId, PetSnackRequests.AddressRequest request) {
        Long userId = currentUserId();
        Map<String, Object> existing = requireAddress(addressId, userId);
        Map<String, Object> address = addressValues(request, userId, addressId);
        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault()) || intValue(existing.get("isDefault")) == 1;
        if (Boolean.TRUE.equals(request.getIsDefault())) mapper.clearDefaultAddresses(userId);
        address.put("isDefault", makeDefault ? 1 : 0);
        if (mapper.updateAddress(address) != 1) throw notFound("收货地址不存在");
        return requireAddress(addressId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long addressId) {
        Long userId = currentUserId();
        Map<String, Object> existing = requireAddress(addressId, userId);
        if (mapper.deleteAddress(addressId, userId) != 1) throw notFound("收货地址不存在");
        if (intValue(existing.get("isDefault")) == 1) {
            Long latestAddressId = mapper.selectLatestAddressId(userId);
            if (latestAddressId != null) mapper.setDefaultAddress(latestAddressId, userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long addressId) {
        Long userId = currentUserId();
        requireAddress(addressId, userId);
        mapper.clearDefaultAddresses(userId);
        if (mapper.setDefaultAddress(addressId, userId) != 1) throw notFound("收货地址不存在");
    }

    @Override
    public Map<String, Object> coupons(String status, int pageNo, int pageSize) {
        Long userId = currentUserId();
        String safeStatus = StringUtils.hasText(status) ? status.trim().toUpperCase(Locale.ROOT) : "AVAILABLE";
        if (!"AVAILABLE".equals(safeStatus) && !"USED".equals(safeStatus) && !"EXPIRED".equals(safeStatus)) {
            throw badRequest("优惠券状态无效");
        }
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.min(50, Math.max(1, pageSize));
        List<Map<String, Object>> list = mapper.selectUserCoupons(userId, safeStatus,
                (safePageNo - 1) * safePageSize, safePageSize);
        for (Map<String, Object> coupon : list) normalizeCoupon(coupon);
        return pageResult(safePageNo, safePageSize, mapper.countUserCoupons(userId, safeStatus), list);
    }

    @Override
    public Map<String, Object> previewOrder(PetSnackRequests.OrderPreviewRequest request) {
        Map<String, Object> preview = calculateOrder(request, currentUserId());
        preview.remove("_rawItems");
        preview.remove("_address");
        preview.remove("_coupon");
        return preview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createOrder(PetSnackRequests.CreateOrderRequest request) {
        Long userId = currentUserId();
        if (request == null) throw badRequest("订单参数不能为空");
        String clientRequestNo = text(request.getClientRequestNo(), "幂等请求号", 8, 64);
        Map<String, Object> existing = mapper.selectOrderByClientRequest(userId, clientRequestNo);
        if (existing != null) return normalizeOrder(existing);

        Map<String, Object> preview = calculateOrder(request, userId);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) preview.get("_rawItems");
        @SuppressWarnings("unchecked")
        Map<String, Object> address = (Map<String, Object>) preview.get("_address");
        @SuppressWarnings("unchecked")
        Map<String, Object> coupon = (Map<String, Object>) preview.get("_coupon");

        Map<String, Object> order = new HashMap<>();
        order.put("orderNo", nextOrderNo());
        order.put("clientRequestNo", clientRequestNo);
        order.put("userId", userId);
        order.put("addressId", idOf(address, "id"));
        order.put("userCouponId", coupon == null ? null : idOf(coupon, "userCouponId"));
        order.put("receiverName", address.get("receiverName"));
        order.put("receiverPhone", address.get("receiverPhone"));
        order.put("receiverAddress", fullAddress(address));
        order.put("remark", trimToNull(request.getRemark(), 500));
        order.put("productAmountYuan", preview.get("_productAmountYuan"));
        order.put("freightAmountYuan", preview.get("_freightAmountYuan"));
        order.put("discountAmountYuan", preview.get("_discountAmountYuan"));
        order.put("payAmountYuan", preview.get("_payAmountYuan"));
        order.put("totalQuantity", preview.get("totalQuantity"));
        order.put("expiresAt", new Date(System.currentTimeMillis() + ORDER_EXPIRE_MINUTES * 60_000L));
        mapper.insertOrder(order);
        Long orderId = idOf(order, "id");

        for (Map<String, Object> item : rawItems) {
            Long skuId = idOf(item, "skuId");
            int quantity = intValue(item.get("quantity"));
            if (mapper.decreaseSkuStock(skuId, quantity) != 1) {
                throw conflict("商品库存不足，请刷新后重试");
            }
            item.put("orderId", orderId);
            mapper.insertOrderItem(item);
        }
        if (coupon != null && mapper.lockUserCoupon(idOf(coupon, "userCouponId"), userId, orderId) != 1) {
            throw conflict("优惠券已被使用或已失效");
        }
        return normalizeOrder(mapper.selectOrder(orderId, userId));
    }

    @Override
    public Map<String, Object> myOrders(String status, int pageNo, int pageSize) {
        Long userId = currentUserId();
        String safeStatus = normalizeOrderFilter(status);
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.min(50, Math.max(1, pageSize));
        List<Map<String, Object>> list = mapper.selectOrders(userId, safeStatus,
                (safePageNo - 1) * safePageSize, safePageSize);
        for (Map<String, Object> order : list) {
            normalizeOrder(order);
            order.put("items", normalizeOrderItems(mapper.selectOrderItems(idOf(order, "orderId"))));
        }
        Map<String, Object> result = pageResult(safePageNo, safePageSize,
                mapper.countOrders(userId, safeStatus), list);
        result.put("counts", buildOrderCounts(mapper.countOrdersByStatus(userId)));
        return result;
    }

    @Override
    public Map<String, Object> orderDetail(Long orderId) {
        Long userId = currentUserId();
        Map<String, Object> order = requireOrder(orderId, userId);
        normalizeOrder(order);
        order.put("items", normalizeOrderItems(mapper.selectOrderItems(orderId)));
        return order;
    }

    @Override
    public Map<String, Object> continuePayment(Long orderId, String clientIp) {
        Map<String, Object> order = requireOrder(orderId, currentUserId());
        if (!"PENDING_PAYMENT".equals(stringValue(order.get("status")))) {
            throw conflict("当前订单状态不能支付");
        }
        return wechatPayService.createJsapiPayment(order, currentUserId(), clientIp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelOrder(Long orderId) {
        Long userId = currentUserId();
        Map<String, Object> order = requireOrder(orderId, userId);
        if (mapper.updateOrderStatus(orderId, userId, "PENDING_PAYMENT", "CANCELLED") != 1) {
            throw conflict("只有待支付订单可以取消");
        }
        for (Map<String, Object> item : mapper.selectOrderItems(orderId)) {
            mapper.restoreSkuStock(idOf(item, "skuId"), intValue(item.get("quantity")));
        }
        if (order.get("userCouponId") != null) {
            mapper.releaseUserCoupon(idOf(order, "userCouponId"), userId, orderId);
        }
        return orderDetail(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> confirmReceipt(Long orderId) {
        Long userId = currentUserId();
        requireOrder(orderId, userId);
        if (mapper.updateOrderStatus(orderId, userId, "SHIPPED", "COMPLETED") != 1) {
            throw conflict("只有已发货订单可以确认收货");
        }
        return orderDetail(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> applyRefund(Long orderId, PetSnackRequests.RefundRequest request) {
        Long userId = currentUserId();
        Map<String, Object> order = requireOrder(orderId, userId);
        String reason = text(request == null ? null : request.getReason(), "退款原因", 2, 500);
        if (mapper.updateOrderRefundStatus(orderId, userId) != 1) {
            throw conflict("当前订单状态不能申请退款");
        }
        Map<String, Object> refund = new HashMap<>();
        refund.put("refundNo", "PR" + nextOrderNo());
        refund.put("orderId", orderId);
        refund.put("userId", userId);
        refund.put("reason", reason);
        refund.put("refundAmountYuan", order.get("payAmountYuan"));
        mapper.insertRefund(refund);
        Map<String, Object> result = orderDetail(orderId);
        result.put("refundId", refund.get("id"));
        return result;
    }

    @Override
    public List<Map<String, Object>> helpArticles() {
        return mapper.selectHelpArticles();
    }

    @Override
    public Map<String, Object> helpArticle(Long articleId) {
        if (articleId == null) throw badRequest("文章ID不能为空");
        Map<String, Object> article = mapper.selectHelpArticle(articleId);
        if (article == null) throw notFound("帮助文章不存在");
        return article;
    }

    @Override
    public Map<String, Object> createFeedback(PetSnackRequests.FeedbackRequest request) {
        if (request == null) throw badRequest("反馈内容不能为空");
        Long userId = currentUserId();
        String type = StringUtils.hasText(request.getType())
                ? request.getType().trim().toUpperCase(Locale.ROOT) : "OTHER";
        if (!java.util.Arrays.asList("ORDER", "PRODUCT", "DELIVERY", "FUNCTION", "OTHER").contains(type)) {
            throw badRequest("反馈类型无效");
        }
        if (request.getOrderId() != null) requireOrder(request.getOrderId(), userId);
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("userId", userId);
        feedback.put("orderId", request.getOrderId());
        feedback.put("type", type);
        feedback.put("content", text(request.getContent(), "反馈内容", 2, 1000));
        List<String> images = request.getImageUrls() == null ? Collections.emptyList() : request.getImageUrls();
        if (images.size() > 6) throw badRequest("反馈图片最多6张");
        feedback.put("imageUrlsJson", JSON.toJSONString(images));
        feedback.put("contact", trimToNull(request.getContact(), 100));
        mapper.insertFeedback(feedback);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("feedbackId", feedback.get("id"));
        result.put("status", "PENDING");
        return result;
    }

    private Map<String, Object> calculateOrder(PetSnackRequests.OrderPreviewRequest request, Long userId) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw badRequest("订单商品不能为空");
        }
        if (request.getItems().size() > MAX_ORDER_ITEM_TYPES) throw badRequest("单次下单商品种类过多");
        Map<String, Object> address = requireAddress(request.getAddressId(), userId);

        LinkedHashMap<Long, Integer> quantities = new LinkedHashMap<>();
        for (PetSnackRequests.OrderItemRequest item : request.getItems()) {
            if (item == null || item.getSkuId() == null || item.getQuantity() == null
                    || item.getQuantity() < 1 || item.getQuantity() > MAX_ITEM_QUANTITY) {
                throw badRequest("商品规格或数量无效");
            }
            int merged = quantities.containsKey(item.getSkuId()) ? quantities.get(item.getSkuId()) : 0;
            merged += item.getQuantity();
            if (merged > MAX_ITEM_QUANTITY) throw badRequest("单个商品数量不能超过" + MAX_ITEM_QUANTITY);
            quantities.put(item.getSkuId(), merged);
        }

        BigDecimal productAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        List<Map<String, Object>> rawItems = new ArrayList<>();
        List<Map<String, Object>> displayItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Map<String, Object> sku = mapper.selectSkuForOrder(entry.getKey());
            if (sku == null) throw notFound("商品规格不存在或已下架");
            int stock = intValue(sku.get("stock"));
            if (stock < entry.getValue()) throw conflict(stringValue(sku.get("productName")) + "库存不足");
            BigDecimal unitPrice = money(sku.get("salePriceYuan"));
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(entry.getValue()));
            productAmount = productAmount.add(subtotal);
            totalQuantity += entry.getValue();

            Map<String, Object> raw = new HashMap<>();
            raw.put("productId", sku.get("productId"));
            raw.put("skuId", sku.get("skuId"));
            raw.put("productName", sku.get("productName"));
            raw.put("skuName", sku.get("skuName"));
            raw.put("coverUrl", sku.get("coverUrl"));
            raw.put("unitPriceYuan", unitPrice);
            raw.put("quantity", entry.getValue());
            raw.put("subtotalAmountYuan", subtotal);
            rawItems.add(raw);

            Map<String, Object> display = new LinkedHashMap<>();
            display.put("productId", sku.get("productId"));
            display.put("skuId", sku.get("skuId"));
            display.put("productName", sku.get("productName"));
            display.put("skuName", sku.get("skuName"));
            display.put("coverUrl", sku.get("coverUrl"));
            display.put("unitPrice", cents(unitPrice));
            display.put("quantity", entry.getValue());
            display.put("subtotalAmount", cents(subtotal));
            displayItems.add(display);
        }

        BigDecimal freightAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        Map<String, Object> coupon = null;
        if (request.getUserCouponId() != null) {
            coupon = mapper.selectUserCoupon(request.getUserCouponId(), userId);
            validateCoupon(coupon, productAmount);
            discountAmount = money(coupon.get("discountAmountYuan"));
        }
        BigDecimal payAmount = productAmount.add(freightAmount).subtract(discountAmount).max(BigDecimal.ZERO);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", displayItems);
        result.put("totalQuantity", totalQuantity);
        result.put("productAmount", cents(productAmount));
        result.put("freightAmount", cents(freightAmount));
        result.put("discountAmount", cents(discountAmount));
        result.put("payAmount", cents(payAmount));
        addFullAddress(address);
        result.put("address", address);
        if (coupon != null) {
            Map<String, Object> displayCoupon = new LinkedHashMap<>(coupon);
            normalizeCoupon(displayCoupon);
            result.put("coupon", displayCoupon);
        } else {
            result.put("coupon", null);
        }
        result.put("stockValid", true);
        result.put("invalidReason", null);
        result.put("_rawItems", rawItems);
        result.put("_address", address);
        result.put("_coupon", coupon);
        result.put("_productAmountYuan", productAmount);
        result.put("_freightAmountYuan", freightAmount);
        result.put("_discountAmountYuan", discountAmount);
        result.put("_payAmountYuan", payAmount);
        return result;
    }

    private void validateCoupon(Map<String, Object> coupon, BigDecimal productAmount) {
        if (coupon == null) throw notFound("优惠券不存在");
        if (!"AVAILABLE".equals(stringValue(coupon.get("status")))) throw conflict("优惠券不可使用");
        Date now = new Date();
        Date validFrom = dateValue(coupon.get("validFrom"));
        Date validTo = dateValue(coupon.get("validTo"));
        if (validFrom == null || validTo == null || now.before(validFrom) || now.after(validTo)) {
            throw conflict("优惠券不在有效期内");
        }
        if (productAmount.compareTo(money(coupon.get("thresholdAmountYuan"))) < 0) {
            throw conflict("订单金额未达到优惠券使用门槛");
        }
    }

    private JSONObject code2Session(String code) {
        try {
            String result = restTemplate.getForObject(
                    "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code",
                    String.class, appId, appSecret, code);
            JSONObject body = JSON.parseObject(result);
            Integer errorCode = body.getInteger("errcode");
            if (errorCode != null && errorCode != 0) {
                throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "微信登录失败：" + body.getString("errmsg"));
            }
            return body;
        } catch (HopeException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new HopeException(HttpStatus.BAD_GATEWAY.value(), "调用微信登录服务失败");
        }
    }

    private UserSessionResponse toSession(Map<String, Object> user) {
        UserSessionResponse response = new UserSessionResponse();
        Long userId = idOf(user, "id");
        response.setUserId(userId);
        response.setMemberId(userId);
        response.setTenantId(TenantContext.requireTenantId());
        response.setUsername("pet_snack_user_" + userId);
        response.setNickname(stringValue(user.get("nickname")));
        response.setDisplayName(stringValue(user.get("nickname")));
        response.setAvatarUrl(stringValue(user.get("avatarUrl")));
        response.setMemberStatus(stringValue(user.get("status")));
        return response;
    }

    private Long currentUserId() {
        AuthPrincipal principal = AuthContext.require();
        if (!principal.isPetSnackUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录身份不是宠物商城用户");
        }
        return principal.getUserId();
    }

    private Map<String, Object> requireUser() {
        Map<String, Object> user = mapper.selectUserById(currentUserId());
        ensureActiveUser(user);
        return user;
    }

    private void ensureActiveUser(Map<String, Object> user) {
        if (user == null) throw notFound("宠物商城用户不存在");
        if (!"ACTIVE".equals(stringValue(user.get("status")))) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "宠物商城用户已被禁用");
        }
    }

    private Map<String, Object> requireAddress(Long addressId, Long userId) {
        if (addressId == null) throw badRequest("收货地址不能为空");
        Map<String, Object> address = mapper.selectAddress(addressId, userId);
        if (address == null) throw notFound("收货地址不存在");
        addFullAddress(address);
        return address;
    }

    private Map<String, Object> requireOrder(Long orderId, Long userId) {
        if (orderId == null) throw badRequest("订单ID不能为空");
        Map<String, Object> order = mapper.selectOrder(orderId, userId);
        if (order == null) throw notFound("订单不存在");
        return order;
    }

    private Map<String, Object> addressValues(PetSnackRequests.AddressRequest request, Long userId, Long addressId) {
        if (request == null) throw badRequest("收货地址不能为空");
        Map<String, Object> address = new HashMap<>();
        address.put("id", addressId);
        address.put("userId", userId);
        address.put("receiverName", text(request.getReceiverName(), "收货人", 1, 50));
        String phone = text(request.getReceiverPhone(), "手机号", 6, 30);
        if (!phone.matches("^[0-9+\\- ]{6,30}$")) throw badRequest("手机号格式不正确");
        address.put("receiverPhone", phone);
        address.put("province", text(request.getProvince(), "省份", 1, 50));
        address.put("city", text(request.getCity(), "城市", 1, 50));
        address.put("district", text(request.getDistrict(), "区县", 1, 50));
        address.put("detailAddress", text(request.getDetailAddress(), "详细地址", 2, 255));
        address.put("postalCode", trimToNull(request.getPostalCode(), 20));
        return address;
    }

    private List<Map<String, Object>> normalizeBanners(List<Map<String, Object>> banners) {
        return banners == null ? Collections.emptyList() : banners;
    }

    private List<Map<String, Object>> normalizeProductList(List<Map<String, Object>> products) {
        for (Map<String, Object> product : products) {
            normalizeMoney(product, "salePriceYuan", "salePrice");
            normalizeMoney(product, "marketPriceYuan", "marketPrice");
            product.put("hot", intValue(product.get("hot")) == 1);
            product.put("newProduct", intValue(product.get("newProduct")) == 1);
            product.put("soldOut", intValue(product.get("stock")) <= 0);
        }
        return products;
    }

    private List<Map<String, Object>> normalizeOrderItems(List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            normalizeMoney(item, "unitPriceYuan", "unitPrice");
            normalizeMoney(item, "subtotalAmountYuan", "subtotalAmount");
        }
        return items;
    }

    private Map<String, Object> normalizeOrder(Map<String, Object> order) {
        if (order == null) return null;
        normalizeMoney(order, "productAmountYuan", "productAmount");
        normalizeMoney(order, "freightAmountYuan", "freightAmount");
        normalizeMoney(order, "discountAmountYuan", "discountAmount");
        normalizeMoney(order, "payAmountYuan", "payAmount");
        order.put("statusText", orderStatusText(stringValue(order.get("status"))));
        return order;
    }

    private void normalizeCoupon(Map<String, Object> coupon) {
        normalizeMoney(coupon, "thresholdAmountYuan", "thresholdAmount");
        normalizeMoney(coupon, "discountAmountYuan", "discountAmount");
    }

    private Map<String, Object> buildOrderCounts(List<Map<String, Object>> rows) {
        long all = 0L, pendingPayment = 0L, pendingReceipt = 0L, completed = 0L, afterSale = 0L;
        for (Map<String, Object> row : rows) {
            String status = stringValue(row.get("status"));
            long count = longValue(row.get("count"));
            all += count;
            if ("PENDING_PAYMENT".equals(status)) pendingPayment += count;
            if ("PAID".equals(status) || "PROCESSING".equals(status) || "SHIPPED".equals(status)) pendingReceipt += count;
            if ("COMPLETED".equals(status)) completed += count;
            if ("REFUND_PENDING".equals(status) || "REFUNDED".equals(status) || "REFUND_REJECTED".equals(status)) afterSale += count;
        }
        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("all", all);
        counts.put("pendingPayment", pendingPayment);
        counts.put("pendingReceipt", pendingReceipt);
        counts.put("completed", completed);
        counts.put("afterSale", afterSale);
        return counts;
    }

    private String normalizeOrderFilter(String status) {
        String value = StringUtils.hasText(status) ? status.trim().toUpperCase(Locale.ROOT) : "ALL";
        if (!java.util.Arrays.asList("ALL", "PENDING_PAYMENT", "PENDING_RECEIPT", "COMPLETED", "AFTER_SALE").contains(value)) {
            throw badRequest("订单筛选状态无效");
        }
        return value;
    }

    private String orderStatusText(String status) {
        if ("PENDING_PAYMENT".equals(status)) return "待支付";
        if ("PAID".equals(status)) return "已支付";
        if ("PROCESSING".equals(status)) return "备货中";
        if ("SHIPPED".equals(status)) return "待收货";
        if ("COMPLETED".equals(status)) return "已完成";
        if ("CANCELLED".equals(status)) return "已取消";
        if ("REFUND_PENDING".equals(status)) return "退款处理中";
        if ("REFUNDED".equals(status)) return "已退款";
        if ("REFUND_REJECTED".equals(status)) return "退款已拒绝";
        return status;
    }

    private Map<String, Object> pageResult(int pageNo, int pageSize, long total, List<?> list) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    private void normalizeMoney(Map<String, Object> source, String yuanKey, String centsKey) {
        Object value = source.remove(yuanKey);
        source.put(centsKey, cents(value));
    }

    private long cents(Object yuan) {
        return money(yuan).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private BigDecimal money(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(String.valueOf(value));
    }

    private void addFullAddress(Map<String, Object> address) {
        address.put("fullAddress", fullAddress(address));
    }

    private String fullAddress(Map<String, Object> address) {
        return stringValue(address.get("province")) + stringValue(address.get("city"))
                + stringValue(address.get("district")) + stringValue(address.get("detailAddress"));
    }

    private List<String> jsonStringList(Object value) {
        if (!StringUtils.hasText(stringValue(value))) return Collections.emptyList();
        try {
            JSONArray array = JSON.parseArray(String.valueOf(value));
            List<String> result = new ArrayList<>();
            for (Object item : array) result.add(String.valueOf(item));
            return result;
        } catch (RuntimeException exception) {
            return Collections.emptyList();
        }
    }

    private String text(String value, String label, int min, int max) {
        if (!StringUtils.hasText(value)) throw badRequest(label + "不能为空");
        String text = value.trim();
        if (text.length() < min || text.length() > max) throw badRequest(label + "长度应为" + min + "-" + max + "个字符");
        return text;
    }

    private String trimToNull(String value, int max) {
        if (!StringUtils.hasText(value)) return null;
        String text = value.trim();
        if (text.length() > max) throw badRequest("内容长度不能超过" + max + "个字符");
        return text;
    }

    private Long idOf(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        if (value == null) throw new IllegalStateException("Missing generated id: " + key);
        return ((Number) value).longValue();
    }

    private int intValue(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private long longValue(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private Date dateValue(Object value) {
        return value instanceof Date ? (Date) value : null;
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String nextOrderNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        synchronized (ORDER_NO_TIME) {
            return "PS" + ORDER_NO_TIME.format(new Date()) + suffix;
        }
    }

    private HopeException badRequest(String message) {
        return new HopeException(HttpStatus.BAD_REQUEST.value(), message);
    }

    private HopeException notFound(String message) {
        return new HopeException(HttpStatus.NOT_FOUND.value(), message);
    }

    private HopeException conflict(String message) {
        return new HopeException(HttpStatus.CONFLICT.value(), message);
    }
}
