package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.model.booking.BookingAppointment;
import org.hopeframework.biz.api.model.booking.BookingProduct;

import java.math.BigDecimal;
import java.util.Map;

public interface IBookingWechatPayService {
    BigDecimal resolvePaymentAmount(BigDecimal productDepositAmount);

    BigDecimal resolvePaymentAmount(BigDecimal productDepositAmount, int peopleCount);

    Map<String, String> createJsapiPayment(BookingAppointment appointment,
                                           BookingProduct product,
                                           Long iamUserId,
                                           String clientIp);

    String handlePaymentNotify(String xml);
}
