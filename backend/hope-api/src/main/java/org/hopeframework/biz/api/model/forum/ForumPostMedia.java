package org.hopeframework.biz.api.model.forum;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("forum_post_media")
public class ForumPostMedia {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long postId;
    private Long fileId;
    private String mediaType;
    private String mediaUrl;
    private String coverUrl;
    private Integer width;
    private Integer height;
    private Integer durationSeconds;
    private Integer sortOrder;
    private Date createdAt;
}
