package org.hopeframework.biz.api.module.auth.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
