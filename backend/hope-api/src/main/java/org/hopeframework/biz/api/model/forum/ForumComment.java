package org.hopeframework.biz.api.model.forum;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("forum_comment")
public class ForumComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long postId;
    private Long authorMemberId;
    private String content;
    private String status;
    private Long likeCount;
    private Long replyCount;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
