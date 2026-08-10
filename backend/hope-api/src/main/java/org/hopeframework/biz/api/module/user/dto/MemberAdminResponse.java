package org.hopeframework.biz.api.module.user.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class MemberAdminResponse {
    private Long memberId;
    private Long userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String status;
    private Date muteUntil;
    private Date joinedAt;
    private List<String> roles = new ArrayList<>();
}
