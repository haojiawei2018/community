package org.hopeframework.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 保全中心请求体{"request"：{"common" : {}, "content" : {} }}格式指定
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Target({ TYPE, METHOD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface HopeReqBody {
}
