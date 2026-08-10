package org.hopeframework.biz.api.model.forum;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("forum_post")
public class ForumPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long circleId;
    private Long authorMemberId;
    private String postType;
    private String title;
    private String summary;
    private String content;
    private String status;
    private String visibility;
    private Integer isTop;
    private Integer isFeatured;
    private Integer allowComment;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long favoriteCount;
    private Date publishedAt;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
