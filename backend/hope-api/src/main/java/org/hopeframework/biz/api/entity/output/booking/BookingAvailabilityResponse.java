package org.hopeframework.biz.api.entity.output.booking;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingAvailabilityResponse {
    private Long storeId;
    private Long productId;
    private String date;
    private BigDecimal salePrice;
    private BigDecimal depositAmount;
    private Integer advanceMinutes;
    private List<Slot> slots = new ArrayList<>();

    @Data
    public static class Slot {
        private String time;
        private Integer capacityPeople;
        private Integer remainingPeople;
        private Boolean available;
        /** AVAILABLE、FULL、EXPIRED */
        private String status;
        private String statusText;
    }
}
