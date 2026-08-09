package org.hopeframework.core.spring;

import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.log.UserOperateLog;
import org.hopeframework.core.response.RespBody;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

/**
 * 返回体拦截器
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@ControllerAdvice
public class RecordLogAdvice implements ResponseBodyAdvice<RespBody> {

	/** 用户操作日志组件 */
	@Resource
	private UserOperateLogComponent userOperateLogComponent;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

		// 拦截所有返回体为ResBody的请求
		if (RespBody.class.equals(returnType.getParameterType())) {
			return true;
		}

		return false;
	}

	@Override
	public RespBody beforeBodyWrite(RespBody body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {

		// 记录操作日志
		UserOperateLog userOperateLog = userOperateLogComponent.currentUserOperateLog();

		// 返回结果Code
		int code = body.getCode();

		// 请求状态
		if (ResponseConst.SUCCESS == code) {
			// 成功
			userOperateLog.setStatus(0);
		} else {
			// 失败
			userOperateLog.setStatus(1);
		}

		// 请求状态代码
		userOperateLog.setCode(String.valueOf(code));

		// 记录结果信息
		userOperateLog.setRespBody(body);

		return body;
	}

}
