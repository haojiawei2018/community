package org.hopeframework.biz.api.entity.output.booking;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class BookingAppointmentListResponse {
    private Map<String, Integer> statusCounts = new LinkedHashMap<>();
    private List<BookingAppointmentResponse> appointments = new ArrayList<>();
}
