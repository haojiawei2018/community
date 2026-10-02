package org.hopeframework.biz.api.entity.input.booking;

import lombok.Data;

@Data
public class CreateBookingAppointmentRequest {
    private Long storeId;
    private Long productId;
    /** yyyy-MM-dd */
    private String appointmentDate;
    /** HH:mm */
    private String slotTime;
    private Integer peopleCount;
    private String remark;
}
