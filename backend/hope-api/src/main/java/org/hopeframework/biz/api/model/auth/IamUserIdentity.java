package org.hopeframework.biz.api.model.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("iam_user_identity")
public class IamUserIdentity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String identityType;
    private String identityValue;
    private String credentialHash;
    private String unionId;
    private Integer verified;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
