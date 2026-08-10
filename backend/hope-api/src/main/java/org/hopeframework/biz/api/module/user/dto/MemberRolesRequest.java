package org.hopeframework.biz.api.module.user.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberRolesRequest {
    private List<Long> roleIds = new ArrayList<>();
}
