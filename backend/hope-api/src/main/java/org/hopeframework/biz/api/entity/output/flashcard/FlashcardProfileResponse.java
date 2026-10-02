package org.hopeframework.biz.api.entity.output.flashcard;

import lombok.Data;

@Data
public class FlashcardProfileResponse {
    private Long userId;
    private String displayId;
    private String name;
    private String bio;
    private String avatarUrl;
    private long collectedCount;
    private long createdCount;
}
