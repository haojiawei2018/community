package org.hopeframework.biz.api.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.common.tenant.TenantTableRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class MybatisTenantConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setLimit(100);
        interceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        TenantSqlParser tenantSqlParser = new TenantSqlParser();
        tenantSqlParser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                return new LongValue(TenantContext.requireTenantId());
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return TenantTableRegistry.isGlobalTable(tableName);
            }
        });
        interceptor.setSqlParserList(Collections.singletonList(tenantSqlParser));
        return interceptor;
    }
}
