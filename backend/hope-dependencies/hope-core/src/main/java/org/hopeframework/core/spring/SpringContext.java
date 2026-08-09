package org.hopeframework.core.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 工程spring 上下文
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class SpringContext implements ApplicationContextAware {

	private static Logger log = LoggerFactory.getLogger(SpringContext.class);

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
		log.debug("Spring context init successfully -->!!");
	}

	/**
	 * 直接获取bean
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return context.getBean(requiredType);
	}
}
