package org.hopeframework.biz.api.service.impl.booking;

import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.config.booking.BookingWechatPayProperties;
import org.hopeframework.biz.api.mapper.booking.BookingAppointmentMapper;
import org.hopeframework.biz.api.mapper.booking.BookingUserMapper;
import org.hopeframework.biz.api.model.booking.BookingAppointment;
import org.hopeframework.biz.api.model.booking.BookingProduct;
import org.hopeframework.biz.api.model.booking.BookingUser;
import org.hopeframework.biz.api.service.booking.IBookingWechatPayService;
import org.hopeframework.core.exception.HopeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

@Service
public class BookingWechatPayServiceImpl implements IBookingWechatPayService {

    private static final Logger log = LoggerFactory.getLogger(BookingWechatPayServiceImpl.class);
    private static final int PAYMENT_TIMEOUT_MINUTES = 5;

    private final BookingWechatPayProperties properties;
    private final BookingUserMapper bookingUserMapper;
    private final BookingAppointmentMapper appointmentMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public BookingWechatPayServiceImpl(BookingWechatPayProperties properties,
                                       BookingUserMapper bookingUserMapper,
                                       BookingAppointmentMapper appointmentMapper) {
        this.properties = properties;
        this.bookingUserMapper = bookingUserMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public BigDecimal resolvePaymentAmount(BigDecimal productDepositAmount) {
        return productDepositAmount;
    }

    @Override
    public BigDecimal resolvePaymentAmount(BigDecimal productDepositAmount, int peopleCount) {
        if (productDepositAmount == null) return null;
        return productDepositAmount.multiply(BigDecimal.valueOf(Math.max(peopleCount, 1)));
    }

    @Override
    public Map<String, String> createJsapiPayment(BookingAppointment appointment,
                                                  BookingProduct product,
                                                  Long bookingUserId,
                                                  String clientIp) {
        if (!properties.isConfigured()) return null;
        validateConfiguration();
        BookingUser bookingUser = bookingUserMapper.selectById(bookingUserId);
        if (bookingUser == null || !StringUtils.hasText(bookingUser.getOpenid())) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "当前预约用户缺少微信openid，请重新登录");
        }

        Map<String, String> request = new LinkedHashMap<>();
        request.put("appid", properties.getAppId());
        request.put("mch_id", properties.getMerchantId());
        request.put("nonce_str", nonce());
        request.put("sign_type", "MD5");
        request.put("body", truncateUtf8("预约定金-" + product.getProductName(), 120));
        request.put("out_trade_no", appointment.getAppointmentNo());
        request.put("total_fee", String.valueOf(WechatPayV2Utils.amountToCents(appointment.getDepositAmount())));
        request.put("spbill_create_ip", normalizeIp(clientIp));
        request.put("time_expire", formatWechatTime(appointment.getExpiresAt()));
        request.put("notify_url", properties.getNotifyUrl());
        request.put("trade_type", "JSAPI");
        request.put("openid", bookingUser.getOpenid());
        request.put("attach", String.valueOf(TenantContext.requireTenantId()));
        request.put("sign", WechatPayV2Utils.sign(request, properties.getApiV2Key()));

        Map<String, String> response = postUnifiedOrder(WechatPayV2Utils.toXml(request));
        if (!"SUCCESS".equals(response.get("return_code"))) {
            throw paymentGatewayError(response.get("return_msg"));
        }
        if (!WechatPayV2Utils.verifySign(response, properties.getApiV2Key())) {
            throw paymentGatewayError("微信统一下单响应验签失败");
        }
        if (!"SUCCESS".equals(response.get("result_code"))) {
            throw paymentGatewayError(firstText(response.get("err_code_des"), response.get("err_code")));
        }
        if (!properties.getAppId().equals(response.get("appid"))
                || !properties.getMerchantId().equals(response.get("mch_id"))) {
            throw paymentGatewayError("微信统一下单返回的商户信息不匹配");
        }
        String prepayId = response.get("prepay_id");
        if (!StringUtils.hasText(prepayId)) {
            throw paymentGatewayError("微信统一下单未返回prepay_id");
        }
        return buildMiniProgramPaymentParams(prepayId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handlePaymentNotify(String xml) {
        try {
            if (!properties.isConfigured()) return WechatPayV2Utils.failResponse("PAY_NOT_CONFIGURED");
            validateConfiguration();
            Map<String, String> notify = WechatPayV2Utils.parseXml(xml);
            if (!"SUCCESS".equals(notify.get("return_code"))
                    || !"SUCCESS".equals(notify.get("result_code"))) {
                return WechatPayV2Utils.failResponse("PAY_RESULT_NOT_SUCCESS");
            }
            if (!WechatPayV2Utils.verifySign(notify, properties.getApiV2Key())) {
                return WechatPayV2Utils.failResponse("SIGN_ERROR");
            }
            if (!properties.getAppId().equals(notify.get("appid"))
                    || !properties.getMerchantId().equals(notify.get("mch_id"))) {
                return WechatPayV2Utils.failResponse("MERCHANT_MISMATCH");
            }
            String appointmentNo = notify.get("out_trade_no");
            BookingAppointment appointment = appointmentMapper.lockByAppointmentNo(
                    TenantContext.requireTenantId(), appointmentNo);
            if (appointment == null) return WechatPayV2Utils.failResponse("ORDER_NOT_FOUND");
            int paidCents = parseCents(notify.get("total_fee"));
            if (paidCents != WechatPayV2Utils.amountToCents(appointment.getDepositAmount())) {
                return WechatPayV2Utils.failResponse("AMOUNT_MISMATCH");
            }
            String transactionId = notify.get("transaction_id");
            if (!StringUtils.hasText(transactionId)) {
                return WechatPayV2Utils.failResponse("TRANSACTION_ID_EMPTY");
            }
            if ("PAID".equals(appointment.getPaymentStatus())) {
                return transactionId.equals(appointment.getTransactionId())
                        ? WechatPayV2Utils.successResponse()
                        : WechatPayV2Utils.failResponse("TRANSACTION_MISMATCH");
            }
            if (!"PENDING_PAYMENT".equals(appointment.getStatus())
                    || !"UNPAID".equals(appointment.getPaymentStatus())) {
                return WechatPayV2Utils.failResponse("ORDER_STATUS_INVALID");
            }
            Date now = new Date();
            appointment.setStatus("CONFIRMED");
            appointment.setPaymentStatus("PAID");
            appointment.setTransactionId(transactionId);
            appointment.setPaidAt(now);
            appointment.setUpdatedAt(now);
            appointmentMapper.updateById(appointment);
            return WechatPayV2Utils.successResponse();
        } catch (RuntimeException exception) {
            log.warn("处理微信支付V2通知失败: {}", exception.getMessage());
            return WechatPayV2Utils.failResponse("PROCESS_ERROR");
        }
    }

    private Map<String, String> postUnifiedOrder(String requestXml) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/xml;charset=UTF-8"));
            HttpEntity<String> entity = new HttpEntity<>(requestXml, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getUnifiedOrderUrl(), HttpMethod.POST, entity, String.class);
            return WechatPayV2Utils.parseXml(response.getBody());
        } catch (HopeException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw paymentGatewayError("调用微信统一下单服务失败");
        }
    }

    private Map<String, String> buildMiniProgramPaymentParams(String prepayId) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("appId", properties.getAppId());
        values.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        values.put("nonceStr", nonce());
        values.put("package", "prepay_id=" + prepayId);
        values.put("signType", "MD5");
        values.put("paySign", WechatPayV2Utils.sign(values, properties.getApiV2Key()));
        values.remove("appId");
        return values;
    }

    private void validateConfiguration() {
        if (!properties.isConfigured()) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "微信支付尚未配置商户号、API V2密钥或回调地址");
        }
        if (properties.getApiV2Key().length() != 32) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "微信支付API V2密钥必须为32位");
        }
        if (!properties.getNotifyUrl().startsWith("https://")) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "微信支付回调地址必须使用公网HTTPS地址");
        }
    }

    private int parseCents(String value) {
        try {
            return Integer.parseInt(value);
        } catch (RuntimeException exception) {
            throw paymentGatewayError("微信支付通知金额格式错误");
        }
    }

    private String normalizeIp(String clientIp) {
        if (!StringUtils.hasText(clientIp)) return "127.0.0.1";
        String value = clientIp.split(",")[0].trim();
        return value.length() <= 64 ? value : value.substring(0, 64);
    }

    private String formatWechatTime(Date value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return format.format(value == null
                ? new Date(System.currentTimeMillis() + PAYMENT_TIMEOUT_MINUTES * 60 * 1000L)
                : value);
    }

    private String nonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String truncateUtf8(String value, int maxBytes) {
        StringBuilder result = new StringBuilder();
        int bytes = 0;
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            String item = new String(Character.toChars(codePoint));
            int itemBytes = item.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
            if (bytes + itemBytes > maxBytes) break;
            result.append(item);
            bytes += itemBytes;
            offset += Character.charCount(codePoint);
        }
        return result.toString();
    }

    private String firstText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) return preferred;
        return StringUtils.hasText(fallback) ? fallback : "微信统一下单失败";
    }

    private HopeException paymentGatewayError(String message) {
        return new HopeException(HttpStatus.BAD_GATEWAY.value(), firstText(message, "微信支付服务异常"));
    }
}
