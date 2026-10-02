package org.hopeframework.biz.api.entity.output.booking;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingBootstrapResponse {
    private Long currentStoreId;
    private String servicePhone;
    private List<Store> stores = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    @Data
    public static class Store {
        private Long id;
        private String name;
        private String logoUrl;
        private String address;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String businessHours;
        private String servicePhone;
    }

    @Data
    public static class Category {
        private Long id;
        private String code;
        private String name;
        private String iconUrl;
        private Integer iconIndex;
        private Integer sortOrder;
    }

    @Data
    public static class Product {
        private Long id;
        private Long storeId;
        private Long categoryId;
        private String name;
        private String coverUrl;
        private Integer coverIndex;
        private List<String> galleryUrls = new ArrayList<>();
        private String description;
        private String noticeContent;
        private String unavailableContent;
        private BigDecimal salePrice;
        private BigDecimal depositAmount;
        private Boolean featured;
        private Integer sortOrder;
    }
}
