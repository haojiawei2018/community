package org.hopeframework.biz.api.service.impl.petsnack;

import org.hopeframework.biz.api.config.petsnack.PetSnackWechatPayProperties;
import org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper;
import org.hopeframework.biz.api.service.petsnack.IPetSnackWechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.hopeframework.core.exception.HopeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class PetSnackWechatPayServiceImpl implements IPetSnackWechatPayService {
    private static final Logger log = LoggerFactory.getLogger(PetSnackWechatPayServiceImpl.class);
    private final PetSnackWechatPayProperties properties;
    private final PetSnackMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public PetSnackWechatPayServiceImpl(PetSnackWechatPayProperties properties, PetSnackMapper mapper) {
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    public Map<String, Object> createJsapiPayment(Map<String, Object> order, Long userId, String clientIp) {
        validateConfiguration();
        Map<String, Object> user = mapper.selectUserById(userId);
        String openid = user == null ? null : String.valueOf(user.get("openid"));
        if (!StringUtils.hasText(openid) || "null".equals(openid)) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "当前用户缺少微信openid，请重新登录");
        }

        Map<String, String> request = new LinkedHashMap<>();
        request.put("appid", properties.getAppId());
        request.put("mch_id", properties.getMerchantId());
        request.put("nonce_str", nonce());
        request.put("sign_type", "MD5");
        request.put("body", "宠物零食订单-" + order.get("orderNo"));
        request.put("out_trade_no", String.valueOf(order.get("orderNo")));
        BigDecimal amount = (BigDecimal) order.get("payAmountYuan");
        request.put("total_fee", String.valueOf(amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.UNNECESSARY).intValueExact()));
        request.put("spbill_create_ip", normalizeIp(clientIp));
        Object expiresAt = order.get("expiresAt");
        if (expiresAt instanceof Date) request.put("time_expire", formatWechatTime((Date) expiresAt));
        request.put("notify_url", properties.getNotifyUrl());
        request.put("trade_type", "JSAPI");
        request.put("openid", openid);
        request.put("sign", sign(request, properties.getApiV2Key()));

        Map<String, String> response = postUnifiedOrder(toXml(request));
        if (!"SUCCESS".equals(response.get("return_code"))) throw gatewayError(response.get("return_msg"));
        if (!verifySign(response, properties.getApiV2Key())) throw gatewayError("微信统一下单响应验签失败");
        if (!"SUCCESS".equals(response.get("result_code"))) {
            String message = StringUtils.hasText(response.get("err_code_des"))
                    ? response.get("err_code_des") : response.get("err_code");
            throw gatewayError(message);
        }
        if (!properties.getAppId().equals(response.get("appid"))
                || !properties.getMerchantId().equals(response.get("mch_id"))) {
            throw gatewayError("微信统一下单返回的商户信息不匹配");
        }
        String prepayId = response.get("prepay_id");
        if (!StringUtils.hasText(prepayId)) throw gatewayError("微信统一下单未返回prepay_id");

        Map<String, String> payment = new LinkedHashMap<>();
        payment.put("appId", properties.getAppId());
        payment.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        payment.put("nonceStr", nonce());
        payment.put("package", "prepay_id=" + prepayId);
        payment.put("signType", "MD5");
        payment.put("paySign", sign(payment, properties.getApiV2Key()));
        payment.remove("appId");
        return new LinkedHashMap<String, Object>(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handlePaymentNotify(String xml) {
        try {
            if (!properties.isConfigured() || properties.getApiV2Key().length() != 32) {
                return fail("PAY_NOT_CONFIGURED");
            }
            Map<String, String> notify = parseXml(xml);
            if (!"SUCCESS".equals(notify.get("return_code"))
                    || !"SUCCESS".equals(notify.get("result_code"))) return fail("PAY_RESULT_NOT_SUCCESS");
            if (!verifySign(notify, properties.getApiV2Key())) return fail("SIGN_ERROR");
            if (!properties.getAppId().equals(notify.get("appid"))
                    || !properties.getMerchantId().equals(notify.get("mch_id"))) return fail("MERCHANT_MISMATCH");

            Map<String, Object> order = mapper.lockOrderByOrderNo(notify.get("out_trade_no"));
            if (order == null) return fail("ORDER_NOT_FOUND");
            int paidCents = Integer.parseInt(notify.get("total_fee"));
            BigDecimal amount = (BigDecimal) order.get("payAmountYuan");
            int expectedCents = amount.multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.UNNECESSARY).intValueExact();
            if (paidCents != expectedCents) return fail("AMOUNT_MISMATCH");
            String transactionId = notify.get("transaction_id");
            if (!StringUtils.hasText(transactionId)) return fail("TRANSACTION_ID_EMPTY");

            if ("PAID".equals(order.get("paymentStatus"))) {
                return transactionId.equals(order.get("transactionId")) ? success() : fail("TRANSACTION_MISMATCH");
            }
            if (!"PENDING_PAYMENT".equals(order.get("status"))
                    || !"UNPAID".equals(order.get("paymentStatus"))) return fail("ORDER_STATUS_INVALID");
            int updated = mapper.markOrderPaid(((Number) order.get("orderId")).longValue(), transactionId);
            return updated == 1 ? success() : fail("ORDER_STATUS_CHANGED");
        } catch (RuntimeException exception) {
            log.warn("处理宠物零食微信支付V2通知失败: {}", exception.getMessage());
            return fail("PROCESS_ERROR");
        }
    }

    private Map<String, String> parseXml(String xml) {
        if (!StringUtils.hasText(xml)) throw new IllegalArgumentException("empty xml");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            Map<String, String> result = new LinkedHashMap<>();
            NodeList nodes = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) result.put(node.getNodeName(), node.getTextContent());
            }
            return result;
        } catch (Exception exception) {
            throw new IllegalArgumentException("invalid xml", exception);
        }
    }

    private Map<String, String> postUnifiedOrder(String xml) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/xml;charset=UTF-8"));
            ResponseEntity<String> response = restTemplate.exchange(properties.getUnifiedOrderUrl(),
                    HttpMethod.POST, new HttpEntity<>(xml, headers), String.class);
            return parseXml(response.getBody());
        } catch (HopeException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw gatewayError("调用微信统一下单服务失败");
        }
    }

    private String toXml(Map<String, String> values) {
        StringBuilder xml = new StringBuilder("<xml>");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getValue() == null) continue;
            xml.append('<').append(entry.getKey()).append('>')
                    .append(escapeXml(entry.getValue()))
                    .append("</").append(entry.getKey()).append('>');
        }
        return xml.append("</xml>").toString();
    }

    private String escapeXml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private void validateConfiguration() {
        if (!properties.isConfigured()) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "宠物商城微信支付尚未配置AppID、商户号、V2密钥或回调地址");
        }
        if (properties.getApiV2Key().length() != 32) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "微信支付API V2密钥必须为32位");
        }
        if (!properties.getNotifyUrl().startsWith("https://")) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "微信支付回调地址必须使用公网HTTPS地址");
        }
    }

    private String nonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String normalizeIp(String clientIp) {
        if (!StringUtils.hasText(clientIp)) return "127.0.0.1";
        String value = clientIp.split(",")[0].trim();
        return value.length() <= 64 ? value : value.substring(0, 64);
    }

    private String formatWechatTime(Date value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return format.format(value);
    }

    private HopeException gatewayError(String message) {
        return new HopeException(HttpStatus.BAD_GATEWAY.value(),
                StringUtils.hasText(message) ? message : "微信支付服务异常");
    }

    private boolean verifySign(Map<String, String> values, String key) {
        String received = values.get("sign");
        if (!StringUtils.hasText(received)) return false;
        return MessageDigest.isEqual(sign(values, key).getBytes(StandardCharsets.UTF_8),
                received.trim().toUpperCase().getBytes(StandardCharsets.UTF_8));
    }

    private String sign(Map<String, String> values, String key) {
        TreeMap<String, String> sorted = new TreeMap<>(values);
        StringBuilder source = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if ("sign".equals(entry.getKey()) || !StringUtils.hasText(entry.getValue())) continue;
            if (source.length() > 0) source.append('&');
            source.append(entry.getKey()).append('=').append(entry.getValue());
        }
        source.append("&key=").append(key);
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(source.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte item : digest) result.append(String.format("%02x", item & 0xff));
            return result.toString().toUpperCase();
        } catch (Exception exception) {
            throw new IllegalStateException("签名失败", exception);
        }
    }

    private String success() {
        return "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>";
    }

    private String fail(String message) {
        return "<xml><return_code>FAIL</return_code><return_msg>" + message + "</return_msg></xml>";
    }
}
