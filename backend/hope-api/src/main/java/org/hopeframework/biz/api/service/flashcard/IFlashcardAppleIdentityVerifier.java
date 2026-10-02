package org.hopeframework.biz.api.service.flashcard;

public interface IFlashcardAppleIdentityVerifier {
    FlashcardAppleIdentity verify(String identityToken, String nonce);
}
