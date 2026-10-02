package org.hopeframework.biz.api.model.booking;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("booking_user")
public class BookingUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long iamUserId;
    private Long memberId;
    private String appId;
    private String openid;
    private String unionid;
    private String nickname;
    private String avatarUrl;
    private String status;
    private Date lastLoginAt;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
