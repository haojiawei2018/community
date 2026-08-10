package org.hopeframework.biz.api.module.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tenant_member")
public class TenantMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String status;
    private Date muteUntil;
    private Date joinedAt;
    private Date lastActiveAt;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
