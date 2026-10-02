package org.hopeframework.biz.api.config.cos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Data
@Component
@ConfigurationProperties(prefix = "tencent.cos")
public class TencentCosProperties {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String publicDomain;
    private String objectPrefix = "images";
    private DataSize maxImageSize = DataSize.ofMegabytes(10);
}
