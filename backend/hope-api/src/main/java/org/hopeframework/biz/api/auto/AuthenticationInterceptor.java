package org.hopeframework.biz.api.auto;

import com.alibaba.fastjson.JSON;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.MemberSecurityService;
import org.hopeframework.biz.api.common.security.PetSnackAccessTokenService;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.common.security.XiaosongTvAccessTokenService;
import org.hopeframework.biz.api.common.security.IronBoxAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.response.RespBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/** 支持类或方法注解、标准 Bearer Token，并建立当前用户上下文。 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AccessTokenService accessTokenService;
    private final PetSnackAccessTokenService petSnackAccessTokenService;
    private final XiaosongTvAccessTokenService xiaosongTvAccessTokenService;
    private final IronBoxAccessTokenService ironBoxAccessTokenService;
    private final MemberSecurityService memberSecurityService;

    public AuthenticationInterceptor(AccessTokenService accessTokenService,
                                     PetSnackAccessTokenService petSnackAccessTokenService,
                                     XiaosongTvAccessTokenService xiaosongTvAccessTokenService,
                                     IronBoxAccessTokenService ironBoxAccessTokenService,
                                     MemberSecurityService memberSecurityService) {
        this.accessTokenService = accessTokenService;
        this.petSnackAccessTokenService = petSnackAccessTokenService;
        this.xiaosongTvAccessTokenService = xiaosongTvAccessTokenService;
        this.ironBoxAccessTokenService = ironBoxAccessTokenService;
        this.memberSecurityService = memberSecurityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            return authenticate(request, handler);
        } catch (HopeException exception) {
            writeAuthenticationError(response, exception);
            return false;
        }
    }

    private boolean authenticate(HttpServletRequest request, Object handler) {
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        PassToken passToken = findAnnotation(method, handlerMethod, PassToken.class);
        if (passToken != null && passToken.required()) return true;

        UserLoginToken userLoginToken = findAnnotation(method, handlerMethod, UserLoginToken.class);
        if (userLoginToken != null && userLoginToken.required()) {
            AuthPrincipal principal = verifyToken(requireToken(request), handlerMethod);
            validateTenant(principal);
            validateApplicationScope(principal, handlerMethod);
            validatePrincipal(principal);
            AuthContext.set(principal);
            RequirePermission permission = findAnnotation(method, handlerMethod, RequirePermission.class);
            if (permission != null) {
                if (principal.isBookingUser() || principal.isFlashcardUser()
                        || principal.isPetSnackUser() || principal.isXiaosongTvUser() || principal.isIronBoxUser()) {
                    throw new HopeException(HttpStatus.FORBIDDEN.value(), "独立应用用户无权访问管理功能");
                }
                if (!principal.isBookingAdmin() && !principal.isFlashcardAdmin() && !principal.isPetSnackAdmin()) {
                    memberSecurityService.requirePermission(principal.getMemberId(), permission.value());
                }
            }
            return true;
        }

        String optionalToken = resolveToken(request);
        if (optionalToken != null && !optionalToken.trim().isEmpty()) {
            AuthPrincipal principal = verifyToken(optionalToken, handlerMethod);
            validateTenant(principal);
            validateApplicationScope(principal, handlerMethod);
            validatePrincipal(principal);
            AuthContext.set(principal);
        }
        return true;
    }

    private void writeAuthenticationError(HttpServletResponse response, HopeException exception) {
        int code = exception.getCode();
        String message = exception.getMessage();
        if (code == ResponseConst.NULL_TOKEN) {
            code = HttpStatus.UNAUTHORIZED.value();
            message = "请先登录";
        } else if (code == ResponseConst.ACCESS_TOKEN) {
            code = HttpStatus.UNAUTHORIZED.value();
            message = "登录已过期，请重新登录";
        }
        int httpStatus = code >= 400 && code <= 599 ? code : HttpStatus.UNAUTHORIZED.value();
        response.setStatus(httpStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write(JSON.toJSONString(new RespBody<Void>(code, message, null)));
        } catch (IOException ioException) {
            throw new IllegalStateException("写入鉴权失败响应异常", ioException);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void validateTenant(AuthPrincipal principal) {
        // 闪卡 APP 是独立用户体系，不参与社区租户归属校验。其业务表仍使用当前
        // 单社区上下文完成数据隔离，普通社区用户和管理端继续严格校验租户。
        if (principal.isIronBoxUser()) {
            if (!principal.getTenantId().equals(TenantContext.getTenantId())) {
                throw new HopeException(HttpStatus.FORBIDDEN.value(), "AI铁盒令牌不属于当前租户");
            }
            return;
        }
        if (principal.isFlashcardUser() || principal.isPetSnackUser() || principal.isXiaosongTvUser()) return;
        Long currentTenantId = TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(principal.getTenantId())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "访问令牌不属于当前租户");
        }
    }

    private void validatePrincipal(AuthPrincipal principal) {
        if (!principal.isBookingUser() && !principal.isBookingAdmin()
                && !principal.isFlashcardUser() && !principal.isFlashcardAdmin()
                && !principal.isPetSnackUser() && !principal.isPetSnackAdmin()
                && !principal.isXiaosongTvUser() && !principal.isIronBoxUser()) {
            memberSecurityService.validate(principal);
        }
    }

    private void validateApplicationScope(AuthPrincipal principal, HandlerMethod handlerMethod) {
        String controllerName = handlerMethod.getBeanType().getName();
        boolean petSnackController = controllerName.startsWith(
                "org.hopeframework.biz.api.controller.petsnack.");
        boolean xiaosongTvController = controllerName.startsWith(
                "org.hopeframework.biz.api.controller.xiaosongtv.");
        boolean ironBoxController = controllerName.startsWith(
                "org.hopeframework.biz.api.controller.ironbox.");
        boolean petSnackAdminController = controllerName.endsWith("PetSnackAdminController");
        if ((principal.isPetSnackUser() || principal.isPetSnackAdmin()) && !petSnackController) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "宠物商城用户无权访问其他业务接口");
        }
        if (petSnackAdminController && !principal.isPetSnackAdmin()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录身份无权访问宠物商城后台");
        }
        if (petSnackController && !petSnackAdminController && !principal.isPetSnackUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录身份无权访问宠物商城用户接口");
        }
        if (principal.isXiaosongTvUser() && !xiaosongTvController) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "小松的电视机用户无权访问其他业务接口");
        }
        if (xiaosongTvController && !principal.isXiaosongTvUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录身份无权访问小松的电视机接口");
        }
        if (principal.isIronBoxUser() && !ironBoxController) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "AI铁盒用户无权访问其他业务接口");
        }
        if (ironBoxController && !principal.isIronBoxUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录身份无权访问AI铁盒接口");
        }
    }

    private AuthPrincipal verifyToken(String token, HandlerMethod handlerMethod) {
        String controllerName = handlerMethod.getBeanType().getName();
        if (controllerName.startsWith("org.hopeframework.biz.api.controller.petsnack.")
                && !controllerName.endsWith("PetSnackAdminController")) {
            return petSnackAccessTokenService.verify(token);
        }
        if (controllerName.startsWith("org.hopeframework.biz.api.controller.xiaosongtv.")) {
            return xiaosongTvAccessTokenService.verify(token);
        }
        if (controllerName.startsWith("org.hopeframework.biz.api.controller.ironbox.")) {
            return ironBoxAccessTokenService.verify(token);
        }
        return accessTokenService.verify(token);
    }

    private String requireToken(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token == null || token.trim().isEmpty()) throw new HopeException(ResponseConst.NULL_TOKEN);
        return token;
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String bearerToken = authorization.substring(7).trim();
            if (!bearerToken.isEmpty()) return bearerToken;
        }
        return request.getHeader("token");
    }

    private <T extends java.lang.annotation.Annotation> T findAnnotation(
            Method method, HandlerMethod handlerMethod, Class<T> annotationType) {
        T annotation = method.getAnnotation(annotationType);
        return annotation != null ? annotation : handlerMethod.getBeanType().getAnnotation(annotationType);
    }
}
