package org.hopeframework.biz.api.auto;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.hopeframework.biz.api.util.JwtConfig;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.util.WebUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * token 校验
 */

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
        String key = httpServletRequest.getHeader("key");// 从 http 请求头中取出 key
        String platform = httpServletRequest.getHeader("platform");// 从 http 请求头中取出 平台
        String versionCode = httpServletRequest.getHeader("versionCode");// 从 http 请求头中取出 版本号
        String url=httpServletRequest.getRequestURI();
        log.info("请求url:"+url);

        final String ip2 = WebUtils.getClientIP(httpServletRequest);
        String ipSb2 = Joiner.on("").skipNulls().join("客户端请求Ip: ", ip2);
        log.info(ipSb2);


        //拦截数据库
       /* DynamicDataSourceAspect dynamicDataSourceAspect=new DynamicDataSourceAspect();
        dynamicDataSourceAspect.switchDataSource();*/

        //log.info("token：" + token + ";版本号：" + versionCode + ";平台：" + platform+"url:"+url);
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                final String ip = WebUtils.getClientIP(httpServletRequest);
                String ipSb = Joiner.on("").skipNulls().join("客户端请求Ip: ", ip);
                log.info(ipSb);
                // 执行认证
                if (token == null) {
                    throw new HopeException(ResponseConst.NULL_TOKEN);
                }
                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new HopeException(ResponseConst.ACCESS_TOKEN);
                }
                Map map = new HashMap<>();
                map.put("userId", userId);
                if (platform != null && !platform.equals("")) {
                    map.put("platform", platform);
                }
                JwtConfig jwtConfig = new JwtConfig();
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jwtConfig.getSecret())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    throw new HopeException(ResponseConst.ACCESS_TOKEN);
                }
                return true;
            }
        }

        //检查有没有需要管理员后台用户权限的注解
        if (method.isAnnotationPresent(AdminLoginToken.class)) {
            AdminLoginToken adminLoginToken = method.getAnnotation(AdminLoginToken.class);
            if (adminLoginToken.required()) {
                System.out.println("开始认证");
                // 取得请求ip
                //HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                final String ip = WebUtils.getClientIP(httpServletRequest);
                String ipSb = Joiner.on("").skipNulls().join("客户端请求Ip: ", ip);
                log.info(ipSb);
                // 执行认证
                if (token == null) {
                    throw new HopeException(ResponseConst.NULL_TOKEN);
                }
                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new HopeException(ResponseConst.ACCESS_TOKEN);
                }
                Map map = new HashMap<>();
                map.put("userId", userId);
                if (platform != null && !platform.equals("")) {
                    map.put("platform", platform);
                }

                JwtConfig jwtConfig = new JwtConfig();
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jwtConfig.getAdminSecret())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    throw new HopeException(ResponseConst.ACCESS_TOKEN);
                }
                return true;
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}


