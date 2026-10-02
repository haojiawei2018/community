package org.hopeframework.biz.api.config.booking;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Data
@Component
@ConfigurationProperties(prefix = "booking.wechat-pay")
public class BookingWechatPayProperties {
    private String appId;
    private String merchantId;
    private String apiV2Key;
    private String notifyUrl;
    private String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public boolean isConfigured() {
        return StringUtils.hasText(appId)
                && StringUtils.hasText(merchantId)
                && StringUtils.hasText(apiV2Key)
                && StringUtils.hasText(notifyUrl);
    }
}
