package org.hopeframework.core.spring;

import java.util.List;

import javax.annotation.Resource;

import org.hopeframework.core.log.LogRecordHandler;
import org.hopeframework.core.log.UserOperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * 用户操作日志组件
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class UserOperateLogComponent {

	/** 日志记录类 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserOperateLogComponent.class);

	/** 日志记录 */
	private final ThreadLocal<UserOperateLog> threadLocalLog = new ThreadLocal<UserOperateLog>();

	/** 日志记录类列表 */
	private final List<LogRecordHandler> logHandlers = Lists.newArrayList();

	/** 转换成json */
	@Resource
	private MappingJackson2HttpMessageConverter messageConverter;

	/**
	 * 取得当前用户操作日志
	 *
	 * @return 当前用户操作日志
	 */
	public UserOperateLog currentUserOperateLog() {

		UserOperateLog log = threadLocalLog.get();

		if (log == null) {
			log = new UserOperateLog();
			threadLocalLog.set(log);
		}

		return log;
	}

	/**
	 * 添加一个日志记录类
	 *
	 * @param logRecordHandler
	 *            日志记录类
	 */
	public void addLogRecordHandler(LogRecordHandler logRecordHandler) {
		logHandlers.add(logRecordHandler);
	}

	/**
	 * 记录日志
	 */
	public void recordLog() {
		// 取得用户操作日志
		UserOperateLog userOperateLog = threadLocalLog.get();
		recordLog(userOperateLog);
		removeCurrentUserOperateLog();
	}

	/**
	 * 使用日志记录类列表记录日志
	 *
	 * @param userOperateLog
	 *            用户操作日志
	 */
	@Async
	public void recordLog(UserOperateLog userOperateLog) {

		try {
			// 如果是RespBody返回体
			if (userOperateLog.getRespBody() != null) {

				// 将完整返回信息当作备注
				String remark = messageConverter.getObjectMapper().writeValueAsString(userOperateLog.getRespBody());
				userOperateLog.setRemark(remark);

			}
		} catch (JsonProcessingException e) {
			LOGGER.error("用户操作日志记录时转换请求体成json时出错了。", e);
		}

		// 如果UserNo为空，则为0
		if (Strings.isNullOrEmpty(userOperateLog.getUserNo())) {
			userOperateLog.setUserNo("<n/a>");
		}

		// 使用日志记录类列表分别记录日志
		for (LogRecordHandler handler : logHandlers) {
			// handler.recordLog(userOperateLog);
		}
	}

	/**
	 * 删除当前用户操作日志
	 */
	private void removeCurrentUserOperateLog() {
		threadLocalLog.remove();
	}

	/** 默认日志记录类 */
	public static class DefaultLogRecordHandler extends LogRecordHandler {

		@Override
		public void recordLog(UserOperateLog userOperateLog) {
			//LOGGER.info("请求结束：{}", userOperateLog);
		}
	}

}
