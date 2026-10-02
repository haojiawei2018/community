package org.hopeframework.biz.api.model.xiaosongtv;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("xiaosong_tv_user")
public class XiaosongTvUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private String username;
    private String phone;
    private String passwordHash;
    private String nickname;
    private String status;
    private Date lastLoginAt;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
