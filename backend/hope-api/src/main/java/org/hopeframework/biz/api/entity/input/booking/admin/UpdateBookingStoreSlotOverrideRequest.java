package org.hopeframework.biz.api.entity.input.booking.admin;

import lombok.Data;

import java.util.List;

@Data
public class UpdateBookingStoreSlotOverrideRequest {
    private List<SlotItem> slotConfig;

    @Data
    public static class SlotItem {
        private String time;
        private Integer capacity;
    }
}
