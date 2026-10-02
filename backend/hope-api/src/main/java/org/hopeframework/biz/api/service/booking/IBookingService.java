package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.entity.input.booking.CreateBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.input.booking.RescheduleBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.output.booking.BookingAppointmentListResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAppointmentResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAvailabilityResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingBootstrapResponse;

public interface IBookingService {
    BookingBootstrapResponse bootstrap(Long storeId);

    BookingAvailabilityResponse availability(Long storeId, Long productId, String date);

    BookingAppointmentResponse createAppointment(CreateBookingAppointmentRequest request, String clientIp);

    BookingAppointmentResponse continuePayment(Long appointmentId, String clientIp);

    BookingAppointmentResponse cancelPendingAppointment(Long appointmentId);

    BookingAppointmentListResponse myAppointments();

    BookingAppointmentResponse rescheduleAppointment(Long appointmentId,
                                                     RescheduleBookingAppointmentRequest request);
}
