package org.hopeframework.biz.api.module.user.dto;

import lombok.Data;
import java.util.Date;

@Data
public class MemberStatusRequest {
    private String status;
    private Date muteUntil;
}
