package org.hopeframework.biz.api.module.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String deviceId;
    private String clientType;
}
