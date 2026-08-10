package org.hopeframework.biz.api.entity.input.user;

import lombok.Data;
import java.util.Date;

@Data
public class MemberStatusRequest {
    private String status;
    private Date muteUntil;
}
