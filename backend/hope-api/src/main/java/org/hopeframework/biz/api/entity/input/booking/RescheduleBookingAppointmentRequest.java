package org.hopeframework.biz.api.entity.input.booking;

import lombok.Data;

@Data
public class RescheduleBookingAppointmentRequest {
    private String appointmentDate;
    private String slotTime;
}
