package org.hopeframework.biz.api.service.impl.booking;

import org.hopeframework.core.exception.HopeException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WechatPayV2UtilsTest {

    @Test
    public void createsOfficialMd5SignatureVector() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("appid", "wxd930ea5d5a258f4f");
        values.put("mch_id", "10000100");
        values.put("device_info", "1000");
        values.put("body", "test");
        values.put("nonce_str", "ibuaiVcKdpRxkhJA");

        assertEquals("9A0A8659F005D6984697E2CA0A9CF3B7",
                WechatPayV2Utils.sign(values, "192006250b4c09247ec02edce69f6a2d"));
    }

    @Test
    public void convertsXmlAndAmountWithoutLosingValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("return_code", "SUCCESS");
        values.put("body", "预约定金<&>");
        Map<String, String> parsed = WechatPayV2Utils.parseXml(WechatPayV2Utils.toXml(values));

        assertEquals("预约定金<&>", parsed.get("body"));
        assertEquals(5000, WechatPayV2Utils.amountToCents(new BigDecimal("50.00")));
    }

    @Test
    public void rejectsXmlWithDoctype() {
        try {
            WechatPayV2Utils.parseXml("<!DOCTYPE xml [<!ENTITY xxe SYSTEM 'file:///tmp/x'>]><xml><a>&xxe;</a></xml>");
            fail("Expected HopeException");
        } catch (HopeException exception) {
            assertEquals(502, exception.getCode());
            assertTrue(exception.getMessage().contains("XML解析失败"));
        }
    }
}
