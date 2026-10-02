package org.hopeframework.biz.api.config.petsnack;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Data
@Component
@ConfigurationProperties(prefix = "pet-snack.cos")
public class PetSnackCosProperties {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String publicDomain;
    private String objectPrefix = "pet-snack";
    private DataSize maxImageSize = DataSize.ofMegabytes(10);
}
