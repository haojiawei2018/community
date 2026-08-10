package org.hopeframework.biz.api.common.tenant;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 开源版固定为单社区；SaaS 版会替换为私有租户解析实现。 */
public class SingleCommunityInterceptor implements HandlerInterceptor {
    private final SingleCommunityProperties properties;

    public SingleCommunityInterceptor(SingleCommunityProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        TenantContext.set(properties.getId(), properties.getCode());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
