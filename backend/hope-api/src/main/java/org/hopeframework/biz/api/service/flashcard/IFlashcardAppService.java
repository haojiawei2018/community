package org.hopeframework.biz.api.service.flashcard;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.flashcard.CreateFlashcardGenerationRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardDeviceLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardAppleLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardPhoneLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardProfileUpdateRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardCardResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardCreateOptionsResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardGenerationResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardHomeResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardProfileResponse;

public interface IFlashcardAppService {
    TokenResponse deviceLogin(FlashcardDeviceLoginRequest request);

    TokenResponse phoneLogin(FlashcardPhoneLoginRequest request);

    TokenResponse appleLogin(FlashcardAppleLoginRequest request);

    FlashcardHomeResponse home(String keyword, String categoryCode, long page, long pageSize);

    FlashcardCardResponse cardDetail(Long cardId);

    FlashcardCreateOptionsResponse createOptions();

    FlashcardGenerationResponse createGeneration(CreateFlashcardGenerationRequest request);

    FlashcardGenerationResponse generation(String taskId);

    FlashcardProfileResponse profile();

    FlashcardProfileResponse updateProfile(FlashcardProfileUpdateRequest request);

    PageResult<FlashcardCardResponse> myCards(String type, String rarity, long page, long pageSize);

    FlashcardCardResponse favorite(Long cardId);

    FlashcardCardResponse unfavorite(Long cardId);
}
