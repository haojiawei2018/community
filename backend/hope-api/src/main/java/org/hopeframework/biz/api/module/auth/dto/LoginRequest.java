package org.hopeframework.biz.api.module.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String deviceId;
    private String clientType;
}
