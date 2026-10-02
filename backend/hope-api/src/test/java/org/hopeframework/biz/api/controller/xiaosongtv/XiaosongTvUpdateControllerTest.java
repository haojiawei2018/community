package org.hopeframework.biz.api.controller.xiaosongtv;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XiaosongTvUpdateControllerTest {
    @Test
    public void updateRemainsDisabledUntilVersionAndDigestAreConfigured() {
        XiaosongTvUpdateController controller = new XiaosongTvUpdateController();
        ReflectionTestUtils.setField(controller, "wgtVersion", "");
        ReflectionTestUtils.setField(controller, "wgtSha1", "");
        ReflectionTestUtils.setField(controller, "wgtUrl", "http://161.189.5.95/xiaosong.wgt");

        Map<String, Object> result = controller.android().getData();
        assertFalse((Boolean) result.get("enabled"));
        assertFalse(result.containsKey("url"));
    }

    @Test
    public void publishedVersionContainsCompatibilityAndDigest() {
        XiaosongTvUpdateController controller = new XiaosongTvUpdateController();
        ReflectionTestUtils.setField(controller, "wgtVersion", "1.0.5");
        ReflectionTestUtils.setField(controller, "wgtSha1", "0123456789abcdef0123456789abcdef01234567");
        ReflectionTestUtils.setField(controller, "wgtUrl", "http://161.189.5.95/xiaosong.wgt");
        ReflectionTestUtils.setField(controller, "minApkVersionCode", 104);

        Map<String, Object> result = controller.android().getData();
        assertTrue((Boolean) result.get("enabled"));
        assertEquals("1.0.5", result.get("version"));
        assertEquals(104, result.get("minApkVersionCode"));
        assertEquals("0123456789abcdef0123456789abcdef01234567", result.get("sha1"));
    }
}
