package org.hopeframework.biz.api.service.flashcard;

import org.hopeframework.biz.api.service.impl.flashcard.FlashcardPhoneCodeServiceImpl;
import org.hopeframework.core.exception.HopeException;
import org.junit.Test;

public class FlashcardPhoneCodeServiceImplTest {

    private final FlashcardPhoneCodeServiceImpl service =
            new FlashcardPhoneCodeServiceImpl("", "", "", "", "test-salt");

    @Test
    public void defaultCodePassesWithoutSendingSmsFirst() {
        service.verifyAndConsume("13800138000", "8888");
    }

    @Test(expected = HopeException.class)
    public void rejectsNonFourDigitCode() {
        service.verifyAndConsume("13800138000", "888888");
    }
}
