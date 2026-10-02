package org.hopeframework.biz.api.entity.input.booking;

import lombok.Data;

@Data
public class BookingWechatLoginRequest {
    private String code;
    private String nickname;
    private String avatarUrl;
}
