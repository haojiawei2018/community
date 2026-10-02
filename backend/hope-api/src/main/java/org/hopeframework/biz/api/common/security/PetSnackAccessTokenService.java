package org.hopeframework.biz.api.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/** 宠物零食小程序专用令牌服务，不与社区及其他业务共用签名和签发方。 */
@Service
public class PetSnackAccessTokenService {
    private static final String ISSUER = "pet-snack-miniapp";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public PetSnackAccessTokenService(@Value("${pet-snack.security.access-token-secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    public String create(Long userId) {
        Date now = new Date();
        return JWT.create().withIssuer(ISSUER).withAudience(String.valueOf(userId))
                .withClaim("principalType", "PET_SNACK").withIssuedAt(now).sign(algorithm);
    }

    public AuthPrincipal verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            Long userId = Long.valueOf(jwt.getAudience().get(0));
            if (!"PET_SNACK".equals(jwt.getClaim("principalType").asString())) {
                throw new IllegalArgumentException("invalid principal type");
            }
            return new AuthPrincipal(userId, userId, 0L, "PET_SNACK");
        } catch (Exception ex) {
            throw new HopeException(ResponseConst.ACCESS_TOKEN);
        }
    }
}
