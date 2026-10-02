package org.hopeframework.biz.api.common.web;

import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.response.RespBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 将业务异常统一转换为项目约定的 JSON 响应，避免拦截器中的登录异常被 Spring 包装成 HTTP 500。
 */
@RestControllerAdvice(basePackages = "org.hopeframework.biz.api")
public class ApiExceptionHandler {

    @ExceptionHandler(HopeException.class)
    public RespBody<Void> handleHopeException(HopeException exception) {
        int code = exception.getCode();
        String message = exception.getMessage();
        if (code == ResponseConst.NULL_TOKEN) {
            code = HttpStatus.UNAUTHORIZED.value();
            message = "请先登录";
        } else if (code == ResponseConst.ACCESS_TOKEN) {
            code = HttpStatus.UNAUTHORIZED.value();
            message = "登录已过期，请重新登录";
        }
        return new RespBody<>(code, message, null);
    }
}
