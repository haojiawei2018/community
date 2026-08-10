package org.hopeframework.biz.api.entity.output.auth;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSessionResponse {
    private Long userId;
    private Long memberId;
    private Long tenantId;
    private String username;
    private String nickname;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String memberStatus;
    private List<String> roles = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
}
