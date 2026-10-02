package org.hopeframework.biz.api.entity.input.flashcard;

import lombok.Data;

@Data
public class FlashcardDeviceLoginRequest {
    private String deviceId;
    private String clientType;
    private String nickname;
    private String avatarUrl;
}
