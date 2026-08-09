package org.hopeframework.core.spring;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import org.codehaus.jackson.map.ext.JodaDeserializers;
import org.hopeframework.core.annotation.HopeReqBody;
import org.hopeframework.core.cache.BasicDataDao;
import org.hopeframework.core.cache.SystemBasicData;
import org.hopeframework.core.constant.Constants;
import org.hopeframework.core.constant.RequestConst;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.enums.AccessType;
import org.hopeframework.core.error.Error;
import org.hopeframework.core.error.Errors;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.exception.ParamterValidException;
import org.hopeframework.core.log.UserOperateLog;
import org.hopeframework.core.request.BasePojo;
import org.hopeframework.core.request.ReqBody;
import org.hopeframework.core.request.ReqCommon;
import org.hopeframework.core.util.WebUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * controller拦截器
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Service
public class HopeMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/** 日志 */
	private static final Logger log = LoggerFactory.getLogger(HopeMethodArgumentResolver.class);

	/**
	 * 错误列表
	 */
	private Errors errors;

	@Autowired
	private ValidatorService validatorService;

	@Autowired
	private BasicDataDao basicDataDao;

	/** 用户操作日志组件 */
	@Resource
	private UserOperateLogComponent userOperateLogComponent;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// 取得参数索引
		int parameterIndex = parameter.getParameterIndex();

		// 获取AnCunReqBody注解
		// 从该方法所属类中取得注解
		HopeReqBody annotation = parameter.getDeclaringClass().getAnnotation(HopeReqBody.class);
		if (parameterIndex == 0 && annotation != null) {
			return true;
		}
		// 从该方法中取得注解
		annotation = parameter.getMethodAnnotation(HopeReqBody.class);
		if (parameterIndex == 0 && annotation != null) {
			return true;
		}
		// 从该方法的参数中取得注解
		annotation = parameter.getParameterAnnotation(HopeReqBody.class);
		if (annotation != null) {
			return true;
		}

		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		// 如果不是第一个参数并且是Errors类型
		if (parameter.getParameterIndex() >= 1 && parameter.getParameterType() == Errors.class) {
			return this.errors;
		}

		// 如果不是第一个参数并且不是Errors类型
		else if (parameter.getParameterIndex() >= 1 && parameter.getParameterType() != Errors.class) {
			return null;
		}

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		// 记录操作日志
		UserOperateLog userOperateLog = userOperateLogComponent.currentUserOperateLog();
		Object req = null;
		try {

			// 如果不是泛型时则选择指定为AnCunReqBody注解标注的参数类,
			// 如果没有AnCunReqBody注解标注参数类，则取得第一个
			Class<?> clazz = parameter.getParameterType();
			HopeReqBody anCunReqBody = parameter.getParameterAnnotation(HopeReqBody.class);
			if (anCunReqBody != null) {
				clazz = parameter.getParameterType();
			}
			log.debug("ReqBody Actual Type Arguments clazzName:" + clazz.getName());

			// 获取请求header中的format
			String format = (String) request.getAttribute(RequestConst.FORMAT);
			// 获取请求体
			String content = (String) request.getAttribute(RequestConst.REQUEST_CONTENT_BODY);
			// 如果request header中未包含format参数， 通过请求url获取format
			if (StringUtils.isEmpty(format)) {

				format =  Constants.FORMATE_JSON;
			}
			request.setAttribute(RequestConst.FORMAT, format);

			if (StringUtils.isEmpty(format)) {
				log.error("请求的format为空，使用默认format : {}", RequestConst.DEFAULT_FORMAT);
				request.setAttribute(RequestConst.FORMAT, RequestConst.DEFAULT_FORMAT);
				format = RequestConst.DEFAULT_FORMAT;
			}
			log.debug("visist format is {}", format);

			// 将请求体转换为ReqBody对象
			req = convertToInput(content, clazz, format);

			// 取得请求ip
			final String ip = WebUtils.getClientIP(request);

			// 记录请求相关信息, 该pojo又可能不继承BasePojo，特追加此判断
			// 记录客户端ip
			String ipSb = Joiner.on("").skipNulls().join("直接来源Ip: ", ip);

			//System.out.println("客户端ip:"+ipSb);
			if (req instanceof BasePojo) {
				BasePojo pojo = (BasePojo) req;
				// 当前操作人
				userOperateLog.setUserNo(pojo.getUserNo());
				// 记录accessId
				userOperateLog.setAccessid(pojo.getAccessId());
				// 记录服务类别
				/*if (pojo.getRequestType() != null) {
					userOperateLog.setServiceType(
							AccessType.accessTypeByIndex(Integer.valueOf(pojo.getRequestType())).name());
				}
*/
				ipSb = Joiner.on("").skipNulls().join(ipSb, " / 间接来源Ip: ", pojo.getIp());
			}
			userOperateLog.setRequesetIp(ipSb);

			// 获取被请求方法的AccessToken注解
			/*AccessToken annotation = parameter.getMethodAnnotation(AccessToken.class);
			if (annotation == null) {
				throw new HopeException(ResponseConst.INTERFACE_UNSET_ACCESSTOKEN);
			}

			// 获取注解中指定的签名加密方式
			SignType signType = annotation.sign();
			log.debug("请求加密方式：" + signType.name());
			// 获取注解中指定的访问类型
			AccessType[] accessTyps = annotation.access();
			// 是否需要检查，校验ACCESSID,ACCESSKEY
			boolean checkAccess = annotation.checkAccess();
			// 是否IP鉴权
			boolean ipLimit = annotation.ipLimit();

			ValidatorBean validatorBean = new ValidatorBean();
			validatorBean.setRequest(request);
			validatorBean.setAccessTypes(accessTyps);
			validatorBean.setSignType(signType);
			validatorBean.setContent(content);
			validatorBean.setEncoding(Constants.ENCODING);
			validatorBean.setCheckAccess(checkAccess);
			// 加入requestType、sign、accessId检查
			validatorService.doRequestTypeValid(req, validatorBean);

			// IP白名单检查
			if (ipLimit) {
				// 校验ip白名单
				validatorBean.setIp(ip);
				// validatorService.doIpLimitValid(validatorBean);
			}*/

			// 校验参数
			final WebDataBinder binder = binderFactory.createBinder(webRequest, req, clazz.getName());
			if(req != null) binder.validate(new Object[] { req });
			BindingResult result = binder.getBindingResult();
			// 将错误信息转换成error
			List<Error> errorList = FluentIterable.from(result.getAllErrors())
					.transform(new Function<ObjectError, Error>() {
						@Override
						public Error apply(ObjectError input) {
							SystemBasicData data = basicDataDao.getBusiness(input.getDefaultMessage());
							if (input instanceof FieldError) {
								FieldError fe = (FieldError) input;
								if (null != data && !StringUtils.isEmpty(data.getModule())) {
									return new Error(fe.getField(), fe.getDefaultMessage(), data.getConfValue());
								} else {
									return new Error(fe.getField(), fe.getDefaultMessage());
								}
							} else {
								if (null != data && !StringUtils.isEmpty(data.getModule())) {
									return new Error(input.getDefaultMessage(), data.getConfValue());
								} else {
									return new Error(input.getDefaultMessage());
								}
							}
						}
					}).toList();

			// 如果有错误信息
			if (errorList != null && errorList.size() > 0) {
				this.errors = new Errors().addErrors(errorList);
				log.debug("校验结果: {}", errors);
				throw new ParamterValidException(this.errors);
			}
			// 返回序列化后参数
			return req;
		} catch (HopeException e) {
			log.error("entrance validate errors:{}", this.errors);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new HopeException(ResponseConst.SYSTEM_EXCEPTION);
		}
	}

	/**
	 * 将接收的请求体，解析成controller的对象
	 *
	 * @param requestJson
	 *            请求体内容
	 * @param clazz
	 *            具体controller的参数类型
	 * @param format
	 *            解析类型
	 * @param <T>
	 *            具体controller的参数类型泛型
	 * @return 反序列化后实体
	 */
	private <T> T convertToInput(String requestJson, Class<T> clazz, String format) {

		ObjectReader objectReader = null;

		// 将请求体转换为ReqBody对象
		if (Constants.FORMATE_JSON.equalsIgnoreCase(format)) {
			objectReader = Jackson2ObjectMapperBuilder.json()
					.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
					.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.build().reader().withRootName(RequestConst.REQUEST_ROOT_NAME);
		} else if (Constants.FORMATE_XML.equalsIgnoreCase(format)) {
			objectReader = Jackson2ObjectMapperBuilder.xml()
					.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
					.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.build().reader();
		} else {
			log.error("format must be json or xml,not allow others type!!");
			throw new HopeException(ResponseConst.FORMAT_NOT_ALLOWED);
		}

		// 构建转换泛型
		JavaType secondJavaType = objectReader.getTypeFactory().constructType(clazz);
		JavaType javaType = objectReader.getTypeFactory().constructSimpleType(ReqBody.class,new JavaType[] { secondJavaType });

		// 将字符串反序列成对象
		ReqBody<T> t = null;
		try {
			t = objectReader.forType(javaType).readValue(requestJson);
		} catch (IOException e) {
			log.error("request body 转换出错", e);
			throw new HopeException(ResponseConst.ENTRYACE_VALIDATE_EXCEPTION);
		}

		// 记录操作日志
		UserOperateLog userOperateLog = userOperateLogComponent.currentUserOperateLog();
		ReqCommon common = t.getCommon();
		if (common.getAction().indexOf("uploadCallback") > -1) {
			userOperateLog.setRequestTime(new Date());
		} else {
			// 设置请求时间
			userOperateLog.setRequestTime(
					DateTime.parse(common.getReqtime(), Constants.REQUEST_TIME_FORMAT_FOR_JODA_TIME).toDate());
		}

		// 设置请求接口
		userOperateLog.setActionCode(common.getAction());

		return t.getContent();
	}

}
