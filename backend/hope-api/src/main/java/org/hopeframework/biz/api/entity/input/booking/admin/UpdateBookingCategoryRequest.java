package org.hopeframework.biz.api.entity.input.booking.admin;

import lombok.Data;

@Data
public class UpdateBookingCategoryRequest {
    private String categoryName;
    private String iconUrl;
}
