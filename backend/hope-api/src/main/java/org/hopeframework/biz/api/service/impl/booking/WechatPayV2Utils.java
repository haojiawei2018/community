package org.hopeframework.biz.api.service.impl.booking;

import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

final class WechatPayV2Utils {

    private WechatPayV2Utils() {
    }

    static String sign(Map<String, String> source, String apiV2Key) {
        TreeMap<String, String> sorted = new TreeMap<>(source);
        StringBuilder value = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if ("sign".equals(entry.getKey()) || !StringUtils.hasText(entry.getValue())) {
                continue;
            }
            if (value.length() > 0) value.append('&');
            value.append(entry.getKey()).append('=').append(entry.getValue());
        }
        if (value.length() > 0) value.append('&');
        value.append("key=").append(apiV2Key);
        return md5(value.toString()).toUpperCase();
    }

    static boolean verifySign(Map<String, String> source, String apiV2Key) {
        String received = source.get("sign");
        if (!StringUtils.hasText(received)) return false;
        return MessageDigest.isEqual(
                sign(source, apiV2Key).getBytes(StandardCharsets.UTF_8),
                received.trim().toUpperCase().getBytes(StandardCharsets.UTF_8));
    }

    static String toXml(Map<String, String> values) {
        StringBuilder xml = new StringBuilder("<xml>");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getValue() == null) continue;
            xml.append('<').append(entry.getKey()).append('>')
                    .append(escapeXml(entry.getValue()))
                    .append("</").append(entry.getKey()).append('>');
        }
        return xml.append("</xml>").toString();
    }

    static Map<String, String> parseXml(String xml) {
        if (!StringUtils.hasText(xml)) {
            throw badGateway("微信支付XML为空");
        }
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
            Element root = document.getDocumentElement();
            if (root == null || !"xml".equals(root.getNodeName())) {
                throw badGateway("微信支付XML根节点无效");
            }
            Map<String, String> values = new LinkedHashMap<>();
            NodeList nodes = root.getChildNodes();
            for (int index = 0; index < nodes.getLength(); index++) {
                Node node = nodes.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    values.put(node.getNodeName(), node.getTextContent());
                }
            }
            return values;
        } catch (HopeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw badGateway("微信支付XML解析失败");
        }
    }

    static int amountToCents(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "微信支付金额必须大于0元");
        }
        try {
            return amount.multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.UNNECESSARY).intValueExact();
        } catch (ArithmeticException exception) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "微信支付金额最多保留两位小数");
        }
    }

    static String successResponse() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("return_code", "SUCCESS");
        values.put("return_msg", "OK");
        return toXml(values);
    }

    static String failResponse(String message) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("return_code", "FAIL");
        values.put("return_msg", StringUtils.hasText(message) ? message : "FAIL");
        return toXml(values);
    }

    private static String md5(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte item : digest) result.append(String.format("%02x", item & 0xff));
            return result.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("无法生成微信支付签名", exception);
        }
    }

    private static String escapeXml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static HopeException badGateway(String message) {
        return new HopeException(HttpStatus.BAD_GATEWAY.value(), message);
    }
}
