package org.hopeframework.biz.api.controller.xiaosongtv;

import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XiaosongTvControllerIsolationTest {
    @Test
    public void exposesOnlyDedicatedRoutesForInitialScope() throws Exception {
        RequestMapping root = XiaosongTvController.class.getAnnotation(RequestMapping.class);
        assertEquals("/api/v1/xiaosong-tv", root.value()[0]);

        assertPost("register", "/auth/register", PassToken.class);
        assertPost("login", "/auth/login", PassToken.class);
        assertPost("sendPhoneCode", "/auth/phone/send-code", PassToken.class);
        assertPost("phoneLogin", "/auth/phone/login", PassToken.class);
        assertPost("saveCd", "/cds", UserLoginToken.class);

        Method list = XiaosongTvController.class.getMethod("listCds");
        assertEquals("/cds", list.getAnnotation(GetMapping.class).value()[0]);
        assertNotNull(list.getAnnotation(UserLoginToken.class));
    }

    private void assertPost(String methodName, String path,
                            Class<? extends java.lang.annotation.Annotation> authAnnotation) throws Exception {
        Method method = java.util.Arrays.stream(XiaosongTvController.class.getMethods())
                .filter(candidate -> methodName.equals(candidate.getName()))
                .findFirst().orElseThrow(NoSuchMethodException::new);
        assertEquals(path, method.getAnnotation(PostMapping.class).value()[0]);
        assertNotNull(method.getAnnotation(authAnnotation));
    }
}
