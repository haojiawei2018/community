package org.hopeframework.biz.api;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.hopeframework.biz.api.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hope Framework - BIZ API 启动类
 */
@EnableScheduling
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class BizApiApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BizApiApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}