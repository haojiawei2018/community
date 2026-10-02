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
@TableName("flashcard_app_generation")
public class FlashcardGenerationTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private String taskNo;
    private Long userId;
    private Long subjectFileId;
    private Long foregroundFileId;
    private Long effectFileId;
    private String promptText;
    private String styleCode;
    private String rarityCode;
    private String status;
    private Integer progress;
    private Long cardId;
    private String errorMessage;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
