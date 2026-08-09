/*
package org.hopeframework.core.log;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

*/
/**
 * 将操作日志记录到数据库
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 *//*

public class DbLogRecordHandler extends LogRecordHandler {

	*/
/** 数据库插入模板 *//*

	private static final String INSERT_LOG_TEMPLATE = "INSERT INTO user_operate_log\n"
			+ "\t(USER_NO, ACTION_CODE, CONTENT, `STATUS`, CODE, REQUEST_TIME, COMPLETE_TIME, "
			+ "REQUEST_DURATION, SERVER_TIME, DB_TIME, REQUESET_IP, SERVICE_TYPE, REQUEST_URL, " + "ACCESSID, REMARK)\n"
			+ "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?)";

	*/
/** 数据库操作类 *//*

	@Resource
	private JdbcTemplate jdbcTemplate;

	*/
/** 事务 *//*

	@Resource
	protected PlatformTransactionManager transactionManager;

	*/
/**
	 * 记录操作日志
	 *
	 * @param userOperateLog
	 *            操作日志
	 *//*

	@Override
	public void recordLog(UserOperateLog userOperateLog) {

		// 用户套餐信息事务
		DefaultTransactionDefinition tfUserOperateLog = new DefaultTransactionDefinition();
		tfUserOperateLog.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus statusUci = transactionManager.getTransaction(tfUserOperateLog);

		// 新增日志
		jdbcTemplate.update(INSERT_LOG_TEMPLATE,
				new Object[] { userOperateLog.getUserNo(), userOperateLog.getActionCode(), userOperateLog.getContent(),
						userOperateLog.getStatus(), userOperateLog.getCode(), userOperateLog.getRequestTime(),
						userOperateLog.getCompleteTime(), userOperateLog.getRequestDuration(),
						userOperateLog.getServerTime(), userOperateLog.getRequesetIp(), userOperateLog.getServiceType(),
						userOperateLog.getRequestUrl(), userOperateLog.getAccessid(), userOperateLog.getRemark() });

		transactionManager.commit(statusUci);
	}
}
*/
