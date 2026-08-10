package org.hopeframework.biz.api.entity.input.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
    private String deviceId;
    private String clientType;
}
