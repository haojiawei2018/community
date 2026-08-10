package org.hopeframework.biz.api.common.tenant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "community")
public class SingleCommunityProperties {
    private Long id = 1L;
    private String code = "default";
    private String name = "开源游戏社区";
    private String logoUrl;
}
