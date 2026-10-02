package org.hopeframework.biz.api.entity.output.booking.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingAdminDataResponse {
    private Summary summary = new Summary();
    private List<StoreItem> stores = new ArrayList<>();
    private List<CategoryItem> categories = new ArrayList<>();
    private List<ProductItem> products = new ArrayList<>();
    private List<UserItem> users = new ArrayList<>();
    private List<AppointmentItem> appointments = new ArrayList<>();

    @Data
    public static class Summary {
        private int storeCount;
        private int productCount;
        private int userCount;
        private int orderCount;
        private int pendingCount;
        private int confirmedCount;
        private int todayCount;
    }

    @Data
    public static class StoreItem {
        private Long id;
        private String storeName;
        private String logoUrl;
        private String address;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String businessHours;
        private String servicePhone;
        private Integer advanceMinutes;
        private List<SlotItem> slotConfig = new ArrayList<>();
        private String status;
        private Integer sortOrder;
    }

    @Data
    public static class SlotItem {
        private String time;
        private Integer capacity;
    }

    @Data
    public static class CategoryItem {
        private Long id;
        private String categoryName;
        private String iconUrl;
        private String status;
        private Integer sortOrder;
    }

    @Data
    public static class ProductItem {
        private Long id;
        private Long storeId;
        private String storeName;
        private Long categoryId;
        private String categoryName;
        private String productName;
        private String coverUrl;
        private List<String> galleryUrls = new ArrayList<>();
        private String description;
        private String noticeContent;
        private String unavailableContent;
        private BigDecimal salePrice;
        private BigDecimal depositAmount;
        private Integer featured;
        private String status;
        private Integer sortOrder;
    }

    @Data
    public static class UserItem {
        private Long id;
        private String nickname;
        private String avatarUrl;
        private String status;
        private String lastLoginAt;
        private String createdAt;
    }

    @Data
    public static class AppointmentItem {
        private Long id;
        private String appointmentNo;
        private Long userId;
        private String nickname;
        private String avatarUrl;
        private Long storeId;
        private String storeName;
        private Long productId;
        private String productName;
        private String appointmentDate;
        private String slotTime;
        private Integer peopleCount;
        private BigDecimal saleAmount;
        private BigDecimal depositAmount;
        private String remark;
        private String status;
        private String paymentStatus;
        private String transactionId;
        private String expiresAt;
        private String paidAt;
        private String cancelledAt;
        private String createdAt;
    }
}
