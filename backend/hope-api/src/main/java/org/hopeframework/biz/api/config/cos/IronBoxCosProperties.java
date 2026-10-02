package org.hopeframework.biz.api.config.cos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** AI 铁盒独立的 COS 存储桶，不能复用其他业务的密钥。 */
@Data
@Component
@ConfigurationProperties(prefix = "ironbox.cos")
public class IronBoxCosProperties {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String publicDomain;
}
