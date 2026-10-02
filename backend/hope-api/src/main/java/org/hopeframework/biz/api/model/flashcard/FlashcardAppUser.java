package org.hopeframework.biz.api.model.flashcard;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("flashcard_app_user")
public class FlashcardAppUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private String deviceId;
    private String clientType;
    private String phone;
    private String appleSubject;
    private String appleEmail;
    private String nickname;
    private String avatarUrl;
    private String bio;
    private String status;
    private Date lastLoginAt;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
