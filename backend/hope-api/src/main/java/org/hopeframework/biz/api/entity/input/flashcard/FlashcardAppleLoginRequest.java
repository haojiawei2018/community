package org.hopeframework.biz.api.entity.input.flashcard;

import lombok.Data;

@Data
public class FlashcardAppleLoginRequest {
    /** Apple 返回的、由 Apple 私钥签名的 JWT。 */
    private String identityToken;
    /** 客户端发起授权时使用的原始 nonce；传入时服务端会强制核对。 */
    private String nonce;
    private String authorizationCode;
    private String deviceId;
    private String clientType;
    private String nickname;
    private String avatarUrl;
}
