package org.hopeframework.biz.api.module.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private UserSessionResponse user;
}
