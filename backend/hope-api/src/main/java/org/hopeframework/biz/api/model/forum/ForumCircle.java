package org.hopeframework.biz.api.model.forum;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("forum_circle")
public class ForumCircle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String circleCode;
    private String circleName;
    private String iconUrl;
    private String coverUrl;
    private String description;
    private String joinMode;
    private String status;
    private Long memberCount;
    private Long postCount;
    private Integer sortOrder;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
