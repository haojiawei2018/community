package org.hopeframework.core.exception;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hopeframework.core.cache.BasicDataDao;
import org.hopeframework.core.cache.SystemBasicData;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.error.Errors;
import org.hopeframework.core.response.RespBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理拦截类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@ControllerAdvice
public class ExceptionHandler extends Throwable {

	/** 日志 */
	private static Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

	@Autowired
	private BasicDataDao basicDataDao;

	/**
	 * 处理参数校验异常
	 *
	 * @param request
	 *            请求体
	 * @param response
	 *            回复体
	 * @param handler
	 *            发生异常的类
	 * @param ex
	 *            具体异常
	 * @return 处理结果
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(value = ParamterValidException.class)
	public RespBody<Errors> resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			ParamterValidException ex) {

		RespBody<Errors> result = new RespBody<Errors>(ex.getErrors());
		int code = ex.getCode();
		SystemBasicData data = basicDataDao.getBusiness(String.valueOf(code));
		String msg = "";
		if (null != data && !StringUtils.isEmpty(data.getModule())) {
			msg = data.getConfValue();
		}
		result.setCode(code);
		result.setMessage(msg);
		log.error("参数校验异常:result ={},", result.getData().toString());
		return result;
	}

	/**
	 * 业务处理异常
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @param ex
	 * @return
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(value = HopeException.class)
	public RespBody<String> resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 请求响应的格式（xml/json）
		RespBody<String> out = new RespBody<String>(null);
		int code = ResponseConst.SYSTEM_EXCEPTION;
		Object[] params = {};

		String msg = "";

		if (null != ex && ex instanceof HopeException) {
			code = ((HopeException) ex).getCode();
			msg = ((HopeException) ex).getMessage();
			params = ((HopeException) ex).getParams();
			SystemBasicData data = basicDataDao.getBusiness(String.valueOf(code));
			if (null != data && !StringUtils.isEmpty(data.getModule())) {
				msg = MessageFormat.format(data.getConfValue(), params);
			}
			log.error("业务异常:code={},msg={}", code, msg);

		} else {
			SystemBasicData data = basicDataDao.getBusiness(code + "");
			msg = MessageFormat.format(data.getConfValue(), params);
			log.error("系统异常");
		}
		response.setHeader("code", String.valueOf(code));
		out.setCode(code);
		out.setMessage(msg);
		log.error("异常信息:", ex);
		return out;
	}

}
