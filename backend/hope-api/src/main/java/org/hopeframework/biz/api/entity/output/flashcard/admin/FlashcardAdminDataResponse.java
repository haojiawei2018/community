package org.hopeframework.biz.api.entity.output.flashcard.admin;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

public final class FlashcardAdminDataResponse {
    private FlashcardAdminDataResponse() {}

    @Data
    public static class Overview {
        private long userCount;
        private long todayUserCount;
        private long cardCount;
        private long todayCardCount;
        private long bannerCount;
        private long homeCardCount;
    }

    @Data
    public static class CardItem {
        private Long id;
        private String name;
        private String rarity;
        private String series;
        private String categoryCode;
        private String description;
        private String imageUrl;
        private String visibility;
        private String status;
        private Long favoriteCount;
        private Long creatorId;
        private String creatorName;
        private String creatorAvatarUrl;
        private String createdAt;
        private boolean inBanner;
        private Integer bannerOrder;
        private boolean inHomeList;
        private Integer homeOrder;
    }

    @Data
    public static class UserItem {
        private Long id;
        private String nickname;
        private String avatarUrl;
        private String phone;
        private String clientType;
        private String status;
        private long cardCount;
        private String lastLoginAt;
        private String createdAt;
    }

    @Data
    public static class HomeConfig {
        private List<BannerItem> banners = new ArrayList<>();
        private List<CardItem> cards = new ArrayList<>();
    }

    @Data
    public static class BannerItem {
        private Long id;
        private Long cardId;
        private String eyebrow;
        private String title;
        private String subtitle;
        private String imageUrl;
        private Integer sortOrder;
        private CardItem card;
    }
}
