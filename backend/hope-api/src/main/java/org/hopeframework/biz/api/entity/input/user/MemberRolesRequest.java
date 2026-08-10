package org.hopeframework.biz.api.entity.input.user;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberRolesRequest {
    private List<Long> roleIds = new ArrayList<>();
}
