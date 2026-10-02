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
@TableName("flashcard_app_file")
public class FlashcardFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long userId;
    private String layerType;
    private String originalName;
    private String objectName;
    private String fileUrl;
    private Long originalSize;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String imageFormat;
    private Date createdAt;
    @TableLogic
    private Integer deleted;
}
