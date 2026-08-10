package org.hopeframework.biz.api.entity.input.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String deviceId;
    private String clientType;
}
