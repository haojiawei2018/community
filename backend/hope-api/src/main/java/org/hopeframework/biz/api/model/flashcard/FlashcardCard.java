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
@TableName("flashcard_app_card")
public class FlashcardCard {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long creatorUserId;
    private String categoryCode;
    private String cardName;
    private String rarity;
    private String seriesName;
    private String description;
    private String imageUrl;
    private String editionNo;
    private Long favoriteCount;
    private String visibility;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
