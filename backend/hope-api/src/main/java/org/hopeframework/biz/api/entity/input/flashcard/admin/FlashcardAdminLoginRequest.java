package org.hopeframework.biz.api.entity.input.flashcard.admin;

import lombok.Data;

@Data
public class FlashcardAdminLoginRequest {
    private String username;
    private String password;
}
