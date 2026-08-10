package org.hopeframework.biz.api.common.tenant;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SingleCommunityMvcConfiguration implements WebMvcConfigurer {
    private final SingleCommunityProperties properties;

    public SingleCommunityMvcConfiguration(SingleCommunityProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SingleCommunityInterceptor(properties))
                .addPathPatterns("/api/**")
                .order(0);
    }
}
