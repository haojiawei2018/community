package org.hopeframework.biz.api.module.auth.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
    private String deviceId;
    private String clientType;
}
