package org.hopeframework.biz.api.entity.output.flashcard;

import lombok.Data;
import org.hopeframework.biz.api.entity.PageResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlashcardHomeResponse {
    private List<Category> categories = new ArrayList<>();
    private List<Banner> banners = new ArrayList<>();
    private PageResult<FlashcardCardResponse> cards;

    @Data
    public static class Category {
        private String code;
        private String name;

        public Category(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    @Data
    public static class Banner {
        private Long id;
        private String eyebrow;
        private String title;
        private String subtitle;
        private String imageUrl;
        private Long cardId;
    }
}
