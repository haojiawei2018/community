package org.hopeframework.biz.api.service.petsnack;

import java.util.Map;

public interface IPetSnackWechatPayService {
    Map<String, Object> createJsapiPayment(Map<String, Object> order, Long userId, String clientIp);
    String handlePaymentNotify(String xml);
}
