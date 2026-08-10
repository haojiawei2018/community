package org.hopeframework.biz.api.module.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String avatarUrl;
    private String bio;
}
