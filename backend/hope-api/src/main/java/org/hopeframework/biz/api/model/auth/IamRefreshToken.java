package org.hopeframework.biz.api.model.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("iam_refresh_token")
public class IamRefreshToken {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long userId;
    private Long memberId;
    private String tokenHash;
    private String deviceId;
    private String clientType;
    private Date expiresAt;
    private Date revokedAt;
    private Date createdAt;
}
