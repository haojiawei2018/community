package org.hopeframework.biz.api.entity.output.flashcard;

import lombok.Data;

@Data
public class FlashcardGenerationResponse {
    private String taskId;
    private String status;
    private Integer progress;
    private Long cardId;
    private String errorMessage;
}
