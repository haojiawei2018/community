package org.hopeframework.biz.api.service.flashcard;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.flashcard.admin.UpdateFlashcardHomeConfigRequest;
import org.hopeframework.biz.api.entity.output.flashcard.admin.FlashcardAdminDataResponse;

public interface IFlashcardAdminService {
    FlashcardAdminDataResponse.Overview overview();
    PageResult<FlashcardAdminDataResponse.CardItem> cards(String keyword, long page, long pageSize);
    PageResult<FlashcardAdminDataResponse.UserItem> users(String keyword, String status, long page, long pageSize);
    FlashcardAdminDataResponse.HomeConfig homeConfig();
    FlashcardAdminDataResponse.HomeConfig updateHomeConfig(UpdateFlashcardHomeConfigRequest request);
    FlashcardAdminDataResponse.UserItem updateUserStatus(Long userId, String status);
}
