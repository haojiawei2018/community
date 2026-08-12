package org.hopeframework.biz.api.model.forum;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("forum_report")
public class ForumReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long reporterMemberId;
    private String targetType;
    private Long targetId;
    private String reasonCode;
    private String reasonText;
    private String evidenceJson;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
