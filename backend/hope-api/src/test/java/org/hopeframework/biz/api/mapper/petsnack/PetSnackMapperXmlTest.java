package org.hopeframework.biz.api.mapper.petsnack;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PetSnackMapperXmlTest {

    @Test
    public void parsesDedicatedPetSnackMapperXml() throws Exception {
        Configuration configuration = configuration();

        assertTrue(configuration.hasStatement(
                "org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper.selectProducts"));
        assertTrue(configuration.hasStatement(
                "org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper.insertOrder"));
        assertTrue(configuration.hasStatement(
                "org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper.decreaseSkuStock"));
    }

    @Test
    public void buildsCriticalDynamicSqlBranches() throws Exception {
        Configuration configuration = configuration();
        Map<String, Object> productParams = new HashMap<>();
        productParams.put("categoryId", 2L);
        productParams.put("keyword", "鸡肉");
        productParams.put("tag", "HOT");
        productParams.put("offset", 0);
        productParams.put("limit", 20);
        assertSqlParses(configuration, "selectProducts", productParams);

        Map<String, Object> orderParams = new HashMap<>();
        orderParams.put("userId", 1L);
        orderParams.put("status", "AFTER_SALE");
        orderParams.put("offset", 0);
        orderParams.put("limit", 10);
        assertSqlParses(configuration, "selectOrders", orderParams);
    }

    private Configuration configuration() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(PetSnackMapper.class);
        try (InputStream input = Resources.getResourceAsStream("xml/PetSnackMapper.xml")) {
            XMLMapperBuilder builder = new XMLMapperBuilder(input, configuration,
                    "xml/PetSnackMapper.xml", configuration.getSqlFragments());
            builder.parse();
        }
        return configuration;
    }

    private void assertSqlParses(Configuration configuration, String statement, Map<String, Object> params)
            throws Exception {
        String namespace = "org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper.";
        BoundSql boundSql = configuration.getMappedStatement(namespace + statement).getBoundSql(params);
        CCJSqlParserUtil.parse(boundSql.getSql());
    }
}
