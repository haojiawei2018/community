package org.hopeframework.biz.api.auto;

import java.lang.annotation.*;

/**
 * 需要登录的接口，加上该注解后会被 AuthenticationInterceptor 校验 token
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLoginToken {

    boolean required() default true;
}