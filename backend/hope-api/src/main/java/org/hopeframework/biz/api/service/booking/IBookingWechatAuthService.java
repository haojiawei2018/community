package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.entity.input.booking.BookingWechatLoginRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;

public interface IBookingWechatAuthService {
    TokenResponse login(BookingWechatLoginRequest request, String ip);
}
