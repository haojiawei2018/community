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
    private String name = "开源社区";
    private String logoUrl;
    private String defaultAvatarUrl = "https://itzxiu.oss-cn-chengdu.aliyuncs.com/images/2026/08/12/3abaa404cc3a4adaab49318f0645d146.png";
}
