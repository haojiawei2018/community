package org.hopeframework.biz.api.model.notification;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notification")
public class CommunityNotification {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long receiverMemberId;
    private Long senderMemberId;
    private String notificationType;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private Integer readStatus;
    private Date readAt;
    private Date createdAt;
    @TableLogic
    private Integer deleted;
}
