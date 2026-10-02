package org.hopeframework.biz.api.entity.input.booking.admin;

import lombok.Data;

@Data
public class BookingAdminLoginRequest {
    private String username;
    private String password;
}
