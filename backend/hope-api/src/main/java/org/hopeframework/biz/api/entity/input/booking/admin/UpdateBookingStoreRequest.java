package org.hopeframework.biz.api.entity.input.booking.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateBookingStoreRequest {
    private String storeName;
    private String logoUrl;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String businessHours;
    private String servicePhone;
    private Integer advanceMinutes;
    private List<SlotItem> slotConfig;
    private String status;
    private Integer sortOrder;

    @Data
    public static class SlotItem {
        private String time;
        private Integer capacity;
    }
}
