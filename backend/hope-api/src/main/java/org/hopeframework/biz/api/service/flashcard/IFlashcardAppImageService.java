package org.hopeframework.biz.api.service.flashcard;

import org.hopeframework.biz.api.entity.output.flashcard.FlashcardImageUploadResponse;
import org.hopeframework.biz.api.model.flashcard.FlashcardFile;
import org.springframework.web.multipart.MultipartFile;

public interface IFlashcardAppImageService {
    FlashcardImageUploadResponse upload(MultipartFile file, String layerType);

    String composeAndUpload(FlashcardFile subject, FlashcardFile foreground, FlashcardFile effect);
}
