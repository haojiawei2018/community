package org.hopeframework.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.hopeframework.core.cache.AccessDao;
import org.hopeframework.core.cache.BasicDataDao;
import org.hopeframework.core.constant.Constants;
import org.hopeframework.core.exception.ExceptionHandler;
import org.hopeframework.core.spring.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * web配置
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Configuration
@EnableAsync
public class WebConfiguration implements WebMvcConfigurer {

	@Primary
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverter() {
		//定义一个转换消息的对象
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
		supportedMediaTypes.add(MediaType.APPLICATION_PDF);
		supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XML);
		supportedMediaTypes.add(MediaType.IMAGE_GIF);
		supportedMediaTypes.add(MediaType.IMAGE_JPEG);
		supportedMediaTypes.add(MediaType.IMAGE_PNG);
		supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
		supportedMediaTypes.add(MediaType.TEXT_HTML);
		supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		supportedMediaTypes.add(MediaType.TEXT_XML);
		fastConverter.setSupportedMediaTypes(supportedMediaTypes);
		//添加fastjson的配置信息 比如 ：是否要格式化返回的json数据
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.QuoteFieldNames,
				SerializerFeature.WriteEnumUsingToString,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.DisableCircularReferenceDetect);
		//在转换器中添加配置信息
		fastConverter.setFastJsonConfig(fastJsonConfig);
		return new HttpMessageConverters(fastConverter);
	}

	// 用于处理编码问题
	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding(Constants.ENCODING);
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}

	@Bean
	public SecurityApiHandler securityApiHandler() {
		return new SecurityApiHandler();
	}

	@Bean
	public LogHandlerInterceptor logHandlerInterceptor() {
		return new LogHandlerInterceptor();
	}

	/**
	 * 拦截器添加
	 *
	 * @param registry
	 *            拦截器注册
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logHandlerInterceptor());
		registry.addInterceptor(securityApiHandler());
	}

	/**
	 * controller 参数解析
	 *
	 * @param argumentResolvers
	 *            参数解析列表
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(hopeMethodArgumentResolver());
	}

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		return new ThreadPoolTaskExecutor();
	}

	@Bean
	HopeMethodArgumentResolver hopeMethodArgumentResolver() {
		return new HopeMethodArgumentResolver();
	}

	@Bean
	public ValidatorService voiceValidatorService() {
		return new ValidatorService();
	}

	@Bean
	public AccessDao accessDao() {
		return new AccessDao();
	}

	@Bean
	public BasicDataDao basicDataDao() {
		return new BasicDataDao();
	}

	@Bean
	public ExceptionHandler exceptionHandler() {
		return new ExceptionHandler();
	}

	/**
	 * 用户操作日志组件
	 *
	 * @return 用户操作日志组件
	 */
	@Bean
	public UserOperateLogComponent userOperateLogComponent() {
		return new UserOperateLogComponent();
	}

	@Bean
	@ConditionalOnBean({ UserOperateLogComponent.class })
	@Primary
	public UserOperateLogComponent.DefaultLogRecordHandler defaultLogRecordHandler() {
		return new UserOperateLogComponent.DefaultLogRecordHandler();
	}

	/**
	 * {@link org.springframework.web.bind.annotation.ResponseBody}的请求结果拦截器
	 *
	 * @return 请求结果拦截器
	 */
	@Bean
	public RecordLogAdvice recordLogAdvice() {
		return new RecordLogAdvice();
	}

	/**
	 * 跨域
	 * @return
	 */
	private static final String ALLOWED_HEADERS = "x-requested-with,authorization,Content-Type,Authorization,credential,X-XSRF-TOKEN,token,Content-Length,Content-Disposition,userId,key,platform,versionCode";

	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**")
				.allowedHeaders("*")
				.allowedOrigins("*")
				.allowedMethods("*")
				.allowCredentials(false)
				.maxAge(18000L);
	}

}
