package org.hopeframework.biz.api.entity.input.flashcard;

import lombok.Data;

@Data
public class CreateFlashcardGenerationRequest {
    private Long subjectFileId;
    private Long foregroundFileId;
    private Long effectFileId;
    /** 新版单图流程：本地生成后由用户选中的透明线稿。 */
    private Long lineartFileId;
    /** 新版单图流程：本地生成后由用户选中的卡背。 */
    private Long backFileId;
    private String prompt;
    private String styleCode;
    private String rarityCode;
}
