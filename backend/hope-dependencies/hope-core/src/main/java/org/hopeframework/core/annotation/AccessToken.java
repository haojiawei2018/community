package org.hopeframework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hopeframework.core.enums.AccessType;
import org.hopeframework.core.enums.SignType;

/**
 * 访问令牌
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessToken {

	SignType sign() default SignType.HMACSHA;

	AccessType[] access();

	/**
	 * 是否需要检查ACCESSID,KEY。。 动态ACCESSID,KEY的时候登录是不需要检查的
	 * 
	 * @return
	 */
	boolean checkAccess() default false;

	/**
	 * ip白名单的验证
	 * 
	 * @return
	 */
	boolean ipLimit() default false;
}
