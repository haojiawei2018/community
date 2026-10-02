package org.hopeframework.biz.api.entity.input.flashcard.admin;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateFlashcardHomeConfigRequest {
    private List<BannerItem> banners = new ArrayList<>();
    private List<Long> cardIds = new ArrayList<>();

    @Data
    public static class BannerItem {
        private Long cardId;
        private String eyebrow;
        private String title;
        private String subtitle;
        private String imageUrl;
    }
}
