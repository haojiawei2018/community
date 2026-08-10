package org.hopeframework.biz.api.entity.input.auth;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
