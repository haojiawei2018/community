package org.hopeframework.biz.api.config;

import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TenantSqlParserCompatibilityTest {

    @Test
    public void shouldParseTenantUpdateWithCompatibleJsqlParser() throws Exception {
        Statement statement = CCJSqlParserUtil.parse(
                "UPDATE tenant_member SET last_active_at = NOW() WHERE id = 1");
        Update update = (Update) statement;
        TenantSqlParser parser = new TenantSqlParser();
        parser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                return new LongValue(1L);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return false;
            }
        });

        parser.processUpdate(update);

        assertTrue(update.toString().toLowerCase().contains("tenant_id = 1"));
    }
}
