package org.hopeframework.biz.api.auto;

import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.MemberSecurityService;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/** 支持类或方法注解、标准 Bearer Token，并建立当前用户上下文。 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AccessTokenService accessTokenService;
    private final MemberSecurityService memberSecurityService;

    public AuthenticationInterceptor(AccessTokenService accessTokenService, MemberSecurityService memberSecurityService) {
        this.accessTokenService = accessTokenService;
        this.memberSecurityService = memberSecurityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        PassToken passToken = findAnnotation(method, handlerMethod, PassToken.class);
        if (passToken != null && passToken.required()) {
            return true;
        }

        UserLoginToken userLoginToken = findAnnotation(method, handlerMethod, UserLoginToken.class);
        if (userLoginToken != null && userLoginToken.required()) {
            String token = requireToken(request);
            AuthPrincipal principal = accessTokenService.verify(token);
            Long currentTenantId = TenantContext.getTenantId();
            if (currentTenantId == null || !currentTenantId.equals(principal.getTenantId())) {
                throw new HopeException(HttpStatus.FORBIDDEN.value(), "访问令牌不属于当前租户");
            }
            memberSecurityService.validate(principal);
            AuthContext.set(principal);
            RequirePermission permission = findAnnotation(method, handlerMethod, RequirePermission.class);
            if (permission != null) {
                memberSecurityService.requirePermission(principal.getMemberId(), permission.value());
            }
            return true;
        }

        String optionalToken = resolveToken(request);
        if (optionalToken != null && !optionalToken.trim().isEmpty()) {
            AuthPrincipal principal = accessTokenService.verify(optionalToken);
            Long currentTenantId = TenantContext.getTenantId();
            if (currentTenantId == null || !currentTenantId.equals(principal.getTenantId())) {
                throw new HopeException(HttpStatus.FORBIDDEN.value(), "访问令牌不属于当前租户");
            }
            memberSecurityService.validate(principal);
            AuthContext.set(principal);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private String requireToken(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token == null || token.trim().isEmpty()) {
            throw new HopeException(ResponseConst.NULL_TOKEN);
        }
        return token;
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String bearerToken = authorization.substring(7).trim();
            if (!bearerToken.isEmpty()) {
                return bearerToken;
            }
        }
        return request.getHeader("token");
    }

    private <T extends java.lang.annotation.Annotation> T findAnnotation(
            Method method, HandlerMethod handlerMethod, Class<T> annotationType) {
        T annotation = method.getAnnotation(annotationType);
        return annotation != null ? annotation : handlerMethod.getBeanType().getAnnotation(annotationType);
    }
}
