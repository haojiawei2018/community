package org.hopeframework.biz.api.model.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tenant_member_role")
public class TenantMemberRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long memberId;
    private Long roleId;
    private Long createdBy;
    private Date createdAt;
}
