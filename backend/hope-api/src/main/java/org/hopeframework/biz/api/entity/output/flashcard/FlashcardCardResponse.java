package org.hopeframework.biz.api.entity.output.flashcard;

import lombok.Data;

@Data
public class FlashcardCardResponse {
    private Long id;
    private String name;
    private String rarity;
    private String series;
    private String categoryCode;
    private String description;
    private String imageUrl;
    private String finishCode;
    private String subjectImageUrl;
    private String foregroundImageUrl;
    private String effectImageUrl;
    private String lineartImageUrl;
    private String backImageUrl;
    private Boolean foregroundTransparent;
    private Boolean effectTransparent;
    private String editionNo;
    private Long favoriteCount;
    private Boolean favorited;
    private Long creatorId;
}
