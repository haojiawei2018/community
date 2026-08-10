package org.hopeframework.biz.api.module.auth.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("iam_refresh_token")
public class IamRefreshToken {
    @TableId(type = IdType.AUTO)
    private Long id;
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
