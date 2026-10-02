package org.hopeframework.biz.api.entity.input.booking.admin;

import lombok.Data;

@Data
public class RescheduleBookingAdminAppointmentRequest {
    private String appointmentDate;
    private String slotTime;
}
