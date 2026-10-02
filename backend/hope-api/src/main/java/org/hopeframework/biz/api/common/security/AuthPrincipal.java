package org.hopeframework.biz.api.common.security;

import lombok.Getter;

@Getter
public class AuthPrincipal {
    private final Long userId;
    private final Long memberId;
    private final Long tenantId;
    private final String principalType;

    public AuthPrincipal(Long userId, Long memberId, Long tenantId) {
        this(userId, memberId, tenantId, "COMMUNITY");
    }

    public AuthPrincipal(Long userId, Long memberId, Long tenantId, String principalType) {
        this.userId = userId;
        this.memberId = memberId;
        this.tenantId = tenantId;
        this.principalType = principalType;
    }

    public boolean isBookingUser() {
        return "BOOKING".equals(principalType);
    }

    public boolean isBookingAdmin() {
        return "BOOKING_ADMIN".equals(principalType);
    }

    public boolean isFlashcardUser() {
        return "FLASHCARD_APP".equals(principalType);
    }

    public boolean isFlashcardAdmin() {
        return "FLASHCARD_ADMIN".equals(principalType);
    }

    public boolean isPetSnackUser() {
        return "PET_SNACK".equals(principalType);
    }

    public boolean isPetSnackAdmin() {
        return "PET_SNACK_ADMIN".equals(principalType);
    }

    public boolean isXiaosongTvUser() {
        return "XIAOSONG_TV".equals(principalType);
    }

    public boolean isIronBoxUser() {
        return "IRON_BOX".equals(principalType);
    }
}
