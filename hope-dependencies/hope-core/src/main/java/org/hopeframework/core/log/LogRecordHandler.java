package org.hopeframework.core.log;

import javax.annotation.Resource;

import org.hopeframework.core.spring.UserOperateLogComponent;
import org.springframework.beans.factory.InitializingBean;

/**
 * 操作日志记录类
 *
 * @author 摇光 [NO.0146]
 */
public abstract class LogRecordHandler implements InitializingBean {

    /** 用户操作日志组件 */
    @Resource
    private UserOperateLogComponent userOperateLogComponent;

    /**
     * 记录操作日志
     *
     * @param userOperateLog 操作日志
     */
    public abstract void recordLog(UserOperateLog userOperateLog);

    @Override
    public void afterPropertiesSet() throws Exception {
        userOperateLogComponent.addLogRecordHandler(this);
    }

}
