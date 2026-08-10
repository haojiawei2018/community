package org.hopeframework.biz.api.entity.input.user;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String avatarUrl;
    private String bio;
}
