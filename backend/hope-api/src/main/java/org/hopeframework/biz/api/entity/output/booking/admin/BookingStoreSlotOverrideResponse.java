package org.hopeframework.biz.api.entity.output.booking.admin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookingStoreSlotOverrideResponse {
    private Long storeId;
    private String date;
    private String source;
    private List<SlotItem> slots = new ArrayList<>();

    @Data
    public static class SlotItem {
        private String time;
        private Integer capacity;
        private Integer bookedPeople;
        private Integer remainingPeople;
    }
}
