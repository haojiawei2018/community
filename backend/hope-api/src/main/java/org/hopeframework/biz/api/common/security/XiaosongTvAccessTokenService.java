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

/** 小松的电视机专用永久 Token，与其他项目的 Token 签名和签发者完全隔离。 */
@Service
public class XiaosongTvAccessTokenService {
    private static final String ISSUER = "xiaosong-tv";
    private static final String PRINCIPAL_TYPE = "XIAOSONG_TV";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public XiaosongTvAccessTokenService(
            @Value("${xiaosong-tv.security.access-token-secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    public String create(Long userId, Long tenantId) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(String.valueOf(userId))
                .withClaim("memberId", userId)
                .withClaim("tenantId", tenantId)
                .withClaim("principalType", PRINCIPAL_TYPE)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public AuthPrincipal verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            Long userId = Long.valueOf(jwt.getAudience().get(0));
            Long tenantId = jwt.getClaim("tenantId").asLong();
            String principalType = jwt.getClaim("principalType").asString();
            if (userId == null || tenantId == null || !PRINCIPAL_TYPE.equals(principalType)) {
                throw new IllegalArgumentException("missing or invalid claims");
            }
            return new AuthPrincipal(userId, userId, tenantId, PRINCIPAL_TYPE);
        } catch (Exception exception) {
            throw new HopeException(ResponseConst.ACCESS_TOKEN);
        }
    }
}
