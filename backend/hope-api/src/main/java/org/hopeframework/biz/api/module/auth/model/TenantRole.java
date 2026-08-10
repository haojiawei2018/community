package org.hopeframework.biz.api.module.auth.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tenant_role")
public class TenantRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String roleCode;
    private String roleName;
    private String roleType;
    private String description;
    private Integer systemRole;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
