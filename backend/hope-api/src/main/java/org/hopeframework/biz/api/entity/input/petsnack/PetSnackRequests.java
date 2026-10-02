package org.hopeframework.biz.api.entity.input.petsnack;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public final class PetSnackRequests {

    private PetSnackRequests() {
    }

    @Data
    public static class WechatLoginRequest {
        private String code;
    }

    @Data
    public static class ProfileRequest {
        private String nickname;
        private String avatarUrl;
    }

    @Data
    public static class AddressRequest {
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
        private String postalCode;
        private Boolean isDefault;
    }

    @Data
    public static class OrderItemRequest {
        private Long skuId;
        private Integer quantity;
    }

    @Data
    public static class OrderPreviewRequest {
        private List<OrderItemRequest> items = new ArrayList<>();
        private Long addressId;
        private Long userCouponId;
    }

    @Data
    public static class CreateOrderRequest extends OrderPreviewRequest {
        private String clientRequestNo;
        private String remark;
    }

    @Data
    public static class RefundRequest {
        private String reason;
    }

    @Data
    public static class FeedbackRequest {
        private String type;
        private Long orderId;
        private String content;
        private List<String> imageUrls = new ArrayList<>();
        private String contact;
    }
}
