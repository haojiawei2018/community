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

/** AI 铁盒专用永久 Token：独立密钥、签发者和身份类型，不设置 exp。 */
@Service
public class IronBoxAccessTokenService {
    private static final String ISSUER = "ai-ironbox";
    private static final String TYPE = "IRON_BOX";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public IronBoxAccessTokenService(@Value("${ironbox.security.access-token-secret}") String secret) {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    public String create(Long userId, Long tenantId) {
        return JWT.create().withIssuer(ISSUER).withAudience(String.valueOf(userId))
                .withClaim("tenantId", tenantId).withClaim("principalType", TYPE)
                .withIssuedAt(new Date()).sign(algorithm);
    }

    public AuthPrincipal verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            Long userId = Long.valueOf(jwt.getAudience().get(0));
            Long tenantId = jwt.getClaim("tenantId").asLong();
            if (userId == null || tenantId == null || !TYPE.equals(jwt.getClaim("principalType").asString())) {
                throw new IllegalArgumentException("invalid claims");
            }
            return new AuthPrincipal(userId, userId, tenantId, TYPE);
        } catch (Exception exception) {
            throw new HopeException(ResponseConst.ACCESS_TOKEN);
        }
    }
}
