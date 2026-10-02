package org.hopeframework.biz.api.entity.input.flashcard;

import lombok.Data;

@Data
public class FlashcardProfileUpdateRequest {
    private String nickname;
    private Long avatarFileId;
}
