package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.entity.input.booking.BookingWechatLoginRequest;
import org.hopeframework.biz.api.service.impl.booking.BookingWechatAuthServiceImpl;
import org.hopeframework.core.exception.HopeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BookingWechatAuthServiceImplTest {

    @Test
    public void rejectsEmptyWechatCode() {
        BookingWechatAuthServiceImpl service = service("", "");
        HopeException exception = captureException(service, new BookingWechatLoginRequest());
        assertEquals(400, exception.getCode());
        assertEquals("微信登录code不能为空", exception.getMessage());
    }

    @Test
    public void rejectsMissingWechatConfigurationBeforeCallingWechat() {
        BookingWechatAuthServiceImpl service = service("", "");
        BookingWechatLoginRequest request = new BookingWechatLoginRequest();
        request.setCode("temporary-code");
        HopeException exception = captureException(service, request);
        assertEquals(503, exception.getCode());
        assertEquals("微信登录尚未配置AppID或AppSecret", exception.getMessage());
    }

    private BookingWechatAuthServiceImpl service(String appId, String appSecret) {
        return new BookingWechatAuthServiceImpl(null, null, appId, appSecret);
    }

    private HopeException captureException(BookingWechatAuthServiceImpl service,
                                           BookingWechatLoginRequest request) {
        try {
            service.login(request, "127.0.0.1");
            fail("Expected HopeException");
            return null;
        } catch (HopeException exception) {
            return exception;
        }
    }
}
