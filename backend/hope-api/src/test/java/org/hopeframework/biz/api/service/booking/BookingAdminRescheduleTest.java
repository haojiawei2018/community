package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.booking.admin.RescheduleBookingAdminAppointmentRequest;
import org.hopeframework.biz.api.mapper.booking.BookingAppointmentMapper;
import org.hopeframework.biz.api.mapper.booking.BookingCategoryMapper;
import org.hopeframework.biz.api.mapper.booking.BookingProductMapper;
import org.hopeframework.biz.api.mapper.booking.BookingStoreMapper;
import org.hopeframework.biz.api.mapper.booking.BookingStoreSlotOverrideMapper;
import org.hopeframework.biz.api.mapper.booking.BookingUserMapper;
import org.hopeframework.biz.api.model.booking.BookingAppointment;
import org.hopeframework.biz.api.model.booking.BookingStore;
import org.hopeframework.biz.api.service.impl.booking.BookingAdminServiceImpl;
import org.hopeframework.core.exception.HopeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingAdminRescheduleTest {
    private BookingAppointmentMapper appointmentMapper;
    private BookingStoreMapper storeMapper;
    private BookingAdminServiceImpl service;
    private BookingAppointment appointment;
    private BookingStore store;

    @Before
    public void setUp() {
        storeMapper = mock(BookingStoreMapper.class);
        appointmentMapper = mock(BookingAppointmentMapper.class);
        service = new BookingAdminServiceImpl(storeMapper, mock(BookingCategoryMapper.class),
                mock(BookingProductMapper.class), mock(BookingUserMapper.class), appointmentMapper,
                mock(BookingStoreSlotOverrideMapper.class));
        TenantContext.set(1L, "default");

        appointment = new BookingAppointment();
        appointment.setId(10L);
        appointment.setStoreId(20L);
        appointment.setAppointmentDate(Date.valueOf(LocalDate.now().plusDays(1)));
        appointment.setSlotTime(Time.valueOf("10:30:00"));
        appointment.setPeopleCount(2);
        appointment.setSaleAmount(BigDecimal.valueOf(200));
        appointment.setDepositAmount(BigDecimal.valueOf(100));
        appointment.setStatus("CONFIRMED");
        appointment.setPaymentStatus("PAID");
        when(appointmentMapper.lockByIdForAdmin(1L, 10L)).thenReturn(appointment);

        store = new BookingStore();
        store.setId(20L);
        store.setStoreName("测试店铺");
        store.setSlotConfig("[{\"time\":\"16:30\",\"capacity\":2}]");
        when(storeMapper.lockActiveStore(1L, 20L)).thenReturn(store);
    }

    @After
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void adminCanMovePaidBookingWithinFortyEightHoursWithoutChangingPayment() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(appointmentMapper.sumReservedPeople(1L, 20L, Date.valueOf(tomorrow), Time.valueOf("16:30:00")))
                .thenReturn(0);

        service.rescheduleConfirmedAppointment(10L, request(tomorrow, "16:30"));

        assertEquals(Date.valueOf(tomorrow), appointment.getAppointmentDate());
        assertEquals(Time.valueOf("16:30:00"), appointment.getSlotTime());
        assertEquals("CONFIRMED", appointment.getStatus());
        assertEquals("PAID", appointment.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(100), appointment.getDepositAmount());
        verify(appointmentMapper).updateById(appointment);
    }

    @Test
    public void rejectsFullSlotWithoutUpdatingOrder() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(appointmentMapper.sumReservedPeople(1L, 20L, Date.valueOf(tomorrow), Time.valueOf("16:30:00")))
                .thenReturn(1);

        try {
            service.rescheduleConfirmedAppointment(10L, request(tomorrow, "16:30"));
            fail("Expected the full slot to be rejected");
        } catch (HopeException expected) {
            verify(appointmentMapper, never()).updateById(any(BookingAppointment.class));
            assertEquals(Time.valueOf("10:30:00"), appointment.getSlotTime());
        }
    }

    @Test
    public void rejectsUnpaidOrder() {
        appointment.setPaymentStatus("UNPAID");
        try {
            service.rescheduleConfirmedAppointment(10L, request(LocalDate.now().plusDays(1), "16:30"));
            fail("Expected the unpaid order to be rejected");
        } catch (HopeException expected) {
            verify(storeMapper, never()).lockActiveStore(eq(1L), eq(20L));
            verify(appointmentMapper, never()).updateById(any(BookingAppointment.class));
        }
    }

    private RescheduleBookingAdminAppointmentRequest request(LocalDate date, String time) {
        RescheduleBookingAdminAppointmentRequest request = new RescheduleBookingAdminAppointmentRequest();
        request.setAppointmentDate(date.toString());
        request.setSlotTime(time);
        return request;
    }
}
