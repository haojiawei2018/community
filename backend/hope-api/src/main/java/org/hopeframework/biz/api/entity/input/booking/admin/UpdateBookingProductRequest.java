package org.hopeframework.biz.api.entity.input.booking.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateBookingProductRequest {
    private Long storeId;
    private Long categoryId;
    private String productName;
    private String coverUrl;
    private List<String> galleryUrls;
    private String description;
    private String noticeContent;
    private String unavailableContent;
    private BigDecimal salePrice;
    private BigDecimal depositAmount;
    private Integer featured;
    private String status;
    private Integer sortOrder;
}
