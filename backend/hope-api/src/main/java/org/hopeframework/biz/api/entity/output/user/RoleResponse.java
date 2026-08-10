package org.hopeframework.biz.api.entity.output.user;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleResponse {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String roleType;
    private Integer systemRole;
    private List<String> permissions = new ArrayList<>();
}
