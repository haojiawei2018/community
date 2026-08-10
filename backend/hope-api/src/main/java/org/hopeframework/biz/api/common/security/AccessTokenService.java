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

@Service
public class AccessTokenService {

    private static final String ISSUER = "gaming-community";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long accessTokenSeconds;

    public AccessTokenService(
            @Value("${community.security.access-token-secret}") String secret,
            @Value("${community.security.access-token-seconds:7200}") long accessTokenSeconds) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        this.accessTokenSeconds = accessTokenSeconds;
    }

    public String create(AuthPrincipal principal) {
        Date now = new Date();
        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(String.valueOf(principal.getUserId()))
                .withClaim("memberId", principal.getMemberId())
                .withClaim("tenantId", principal.getTenantId())
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + accessTokenSeconds * 1000L))
                .sign(algorithm);
    }

    public AuthPrincipal verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            Long userId = Long.valueOf(jwt.getAudience().get(0));
            Long memberId = jwt.getClaim("memberId").asLong();
            Long tenantId = jwt.getClaim("tenantId").asLong();
            if (memberId == null || tenantId == null) {
                throw new IllegalArgumentException("missing claims");
            }
            return new AuthPrincipal(userId, memberId, tenantId);
        } catch (Exception ex) {
            throw new HopeException(ResponseConst.ACCESS_TOKEN);
        }
    }

    public long getAccessTokenSeconds() {
        return accessTokenSeconds;
    }
}
