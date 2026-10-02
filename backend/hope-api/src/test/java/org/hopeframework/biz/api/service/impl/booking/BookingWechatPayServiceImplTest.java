package org.hopeframework.biz.api.service.impl.booking;

import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.config.booking.BookingWechatPayProperties;
import org.hopeframework.biz.api.mapper.booking.BookingAppointmentMapper;
import org.hopeframework.biz.api.mapper.booking.BookingUserMapper;
import org.hopeframework.biz.api.model.booking.BookingAppointment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingWechatPayServiceImplTest {

    private static final String APP_ID = "wx-test-app";
    private static final String MCH_ID = "10000100";
    private static final String KEY = "192006250b4c09247ec02edce69f6a2d";

    private BookingWechatPayProperties properties;
    private BookingUserMapper userMapper;
    private BookingAppointmentMapper appointmentMapper;
    private BookingWechatPayServiceImpl service;

    @Before
    public void setUp() {
        properties = new BookingWechatPayProperties();
        properties.setAppId(APP_ID);
        properties.setMerchantId(MCH_ID);
        properties.setApiV2Key(KEY);
        properties.setNotifyUrl("https://example.com/api/v1/booking/payments/wechat/notify");
        userMapper = mock(BookingUserMapper.class);
        appointmentMapper = mock(BookingAppointmentMapper.class);
        service = new BookingWechatPayServiceImpl(properties, userMapper, appointmentMapper);
        TenantContext.set(1L, "default");
    }

    @After
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void leavesPaymentParamsEmptyWhenPaymentIsNotConfigured() {
        properties.setNotifyUrl("");
        assertNull(service.createJsapiPayment(new BookingAppointment(), null, 1L, "127.0.0.1"));
    }

    @Test
    public void usesConfiguredProductDepositAsPaymentAmount() {
        assertEquals(new BigDecimal("50.00"), service.resolvePaymentAmount(new BigDecimal("50.00")));
        assertEquals(new BigDecimal("269.00"), service.resolvePaymentAmount(new BigDecimal("269.00")));
        assertEquals(new BigDecimal("200.00"), service.resolvePaymentAmount(new BigDecimal("100.00"), 2));
    }

    @Test
    public void verifiesNotifyAmountAndConfirmsAppointmentIdempotently() {
        BookingAppointment appointment = new BookingAppointment();
        appointment.setAppointmentNo("BK202608240001");
        appointment.setDepositAmount(new BigDecimal("50.00"));
        appointment.setStatus("PENDING_PAYMENT");
        appointment.setPaymentStatus("UNPAID");
        when(appointmentMapper.lockByAppointmentNo(1L, appointment.getAppointmentNo())).thenReturn(appointment);

        Map<String, String> notify = new LinkedHashMap<>();
        notify.put("return_code", "SUCCESS");
        notify.put("result_code", "SUCCESS");
        notify.put("appid", APP_ID);
        notify.put("mch_id", MCH_ID);
        notify.put("out_trade_no", appointment.getAppointmentNo());
        notify.put("total_fee", "5000");
        notify.put("transaction_id", "4200000000202608240001");
        notify.put("nonce_str", "nonce");
        notify.put("sign", WechatPayV2Utils.sign(notify, KEY));

        String result = service.handlePaymentNotify(WechatPayV2Utils.toXml(notify));

        assertTrue(result.contains("SUCCESS"));
        ArgumentCaptor<BookingAppointment> captor = ArgumentCaptor.forClass(BookingAppointment.class);
        verify(appointmentMapper).updateById(captor.capture());
        assertEquals("CONFIRMED", captor.getValue().getStatus());
        assertEquals("PAID", captor.getValue().getPaymentStatus());
        assertEquals("4200000000202608240001", captor.getValue().getTransactionId());
    }

    @Test
    public void rejectsNotifyWithWrongAmount() {
        BookingAppointment appointment = new BookingAppointment();
        appointment.setAppointmentNo("BK202608240002");
        appointment.setDepositAmount(new BigDecimal("50.00"));
        appointment.setStatus("PENDING_PAYMENT");
        appointment.setPaymentStatus("UNPAID");
        when(appointmentMapper.lockByAppointmentNo(1L, appointment.getAppointmentNo())).thenReturn(appointment);

        Map<String, String> notify = new LinkedHashMap<>();
        notify.put("return_code", "SUCCESS");
        notify.put("result_code", "SUCCESS");
        notify.put("appid", APP_ID);
        notify.put("mch_id", MCH_ID);
        notify.put("out_trade_no", appointment.getAppointmentNo());
        notify.put("total_fee", "1");
        notify.put("transaction_id", "4200000000202608240002");
        notify.put("sign", WechatPayV2Utils.sign(notify, KEY));

        String result = service.handlePaymentNotify(WechatPayV2Utils.toXml(notify));

        assertTrue(result.contains("FAIL"));
        assertTrue(result.contains("AMOUNT_MISMATCH"));
    }
}
