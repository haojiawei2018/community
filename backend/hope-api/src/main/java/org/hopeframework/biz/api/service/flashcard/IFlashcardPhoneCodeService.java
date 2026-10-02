package org.hopeframework.biz.api.service.flashcard;

public interface IFlashcardPhoneCodeService {
    void send(String phone);

    void verifyAndConsume(String phone, String code);
}
