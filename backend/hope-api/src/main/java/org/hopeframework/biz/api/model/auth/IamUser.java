package org.hopeframework.biz.api.model.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("iam_user")
public class IamUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String mobile;
    private String email;
    private Integer gender;
    private String bio;
    private String status;
    private Date lastLoginAt;
    private String lastLoginIp;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
