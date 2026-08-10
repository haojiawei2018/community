package org.hopeframework.biz.api.entity.input.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String deviceId;
    private String clientType;
}
