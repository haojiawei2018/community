package org.hopeframework.core.spring;

import java.io.InputStreamReader;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hopeframework.core.annotation.AccessToken;
import org.hopeframework.core.constant.Constants;
import org.hopeframework.core.constant.RequestConst;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.enums.SignType;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.log.UserOperateLog;
import org.hopeframework.core.util.WebUtils;
import org.hopeframework.utils.StringLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Longs;

/**
 * 拦截器
 *
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class SecurityApiHandler extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityApiHandler.class);

	private String encoding = Constants.ENCODING;

	/**
	 * 用户操作日志组件
	 */
	@Resource
	private UserOperateLogComponent userOperateLogComponent;

	// 1.上传文件和非上传文件的请求体不同需要不同的处理
	// 2.accessId是否需要验证的判断逻辑
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;

		/*if(HandlerMethod.class.equals(handler.getClass())) {

			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			AccessToken annotation = method.getAnnotation(AccessToken.class);
			if (null != annotation) {
				SignType signType = annotation.sign();
				if (SignType.NORMAL.equals(signType)) {
					return true;
				}
			}
			String format = request.getHeader(RequestConst.FORMAT);
			//LOGGER.info("format:{}", format);
			request.setAttribute(RequestConst.FORMAT, format);

			// 记录操作日志
			UserOperateLog userOperateLog = userOperateLogComponent.currentUserOperateLog();

			String fileupload = request.getHeader(RequestConst.FILE_UPLOAD);
			if (!"1".equals(fileupload)) {
				// 获取请求体
				String content = CharStreams.toString(new InputStreamReader(request.getInputStream(), encoding));
				LOGGER.info("RequestContent：{}", content);
				// 记录请求内容
				userOperateLog.setContent(content);
				request.setAttribute(RequestConst.REQUEST_CONTENT_BODY, content);

			} else {
				String filename = WebUtils.getHeaderDecode(request, RequestConst.FILE_NAME, encoding);
				request.setAttribute(RequestConst.REQUEST_FILE_NAME, filename);

				// 记录操作内容
				userOperateLog.setContent("该次操作为文件上传，上传的文件名为：" + filename);

				// 是否允许空文件上传并校验长度
				//	checkFileUploadContentLength(request.getContentLength(), reqLength, "", "");

				String requestType = request.getHeader(RequestConst.REQ_TYPE);
				request.setAttribute(RequestConst.REQUEST_REQ_TYPE, requestType);
				// 获取accessId
				String accessId = WebUtils.getHeaderDecode(request, RequestConst.ACCESS_ID, encoding);
				request.setAttribute(RequestConst.REQUEST_ACCESS_ID, accessId);

				// 记录请求通行证编号
				userOperateLog.setAccessid(accessId);
			}
		}
		return true;*/
	}

	/**
	 * 文件上传内容长度校验
	 *
	 * @param contentLength
	 * @param reqLength
	 * @param fileAllowedEmpty
	 *            是否允许空文件上传
	 * @param fileAllowedMaxLength
	 *            允许上传的最大值
	 * @return void 返回类型
	 //* @throws YuluException
	 *             参数
	 */
	public static void checkFileUploadContentLength(long contentLength, String reqLength, String fileAllowedEmpty,
			String fileAllowedMaxLength) throws HopeException {
		// 允许空文件上传
		boolean nullflag = "1".equals(fileAllowedEmpty);
		if (contentLength == 0 && !nullflag) {
			throw new HopeException(ResponseConst.REQUEST_CONTENT_MUST_BE_NOT_NULL);
		}
		long maxLen = 0;
		if (Strings.isNullOrEmpty(fileAllowedEmpty)) {
			maxLen = Constants.CONTENTLENGTH_MAX;
		} else {
			maxLen = Long.valueOf(fileAllowedMaxLength);
		}
		if (contentLength > maxLen) {
			throw new HopeException(ResponseConst.REQUEST_ENTITY_TOO_LARGE);
		}
		if (Strings.isNullOrEmpty(reqLength)) {
			reqLength = "0";
		}
		long requestLen = Longs.tryParse(reqLength);
		if (contentLength != requestLen) {
			throw new HopeException(ResponseConst.REQUEST_LENGTH_NOT_MATCH);
		}
	}

}
