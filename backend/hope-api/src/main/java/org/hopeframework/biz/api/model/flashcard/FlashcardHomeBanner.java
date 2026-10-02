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
@TableName("flashcard_app_home_banner")
public class FlashcardHomeBanner {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long cardId;
    private String eyebrow;
    private String title;
    private String subtitle;
    private String imageUrl;
    private Integer sortOrder;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
