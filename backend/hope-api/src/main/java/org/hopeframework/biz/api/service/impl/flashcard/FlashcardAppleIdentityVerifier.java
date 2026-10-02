package org.hopeframework.biz.api.service.impl.flashcard;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hopeframework.biz.api.service.flashcard.FlashcardAppleIdentity;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppleIdentityVerifier;
import org.hopeframework.core.exception.HopeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlashcardAppleIdentityVerifier implements IFlashcardAppleIdentityVerifier {
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final long KEY_CACHE_MILLIS = 6L * 60L * 60L * 1000L;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Set<String> clientIds;
    private volatile Map<String, RSAPublicKey> cachedKeys = Collections.emptyMap();
    private volatile long keysExpireAt;

    public FlashcardAppleIdentityVerifier(
            @Value("${flashcard.apple.client-ids:}") String configuredClientIds) {
        Set<String> values = new HashSet<>();
        if (StringUtils.hasText(configuredClientIds)) {
            for (String item : configuredClientIds.split(",")) {
                if (StringUtils.hasText(item)) values.add(item.trim());
            }
        }
        this.clientIds = Collections.unmodifiableSet(values);
    }

    @Override
    public FlashcardAppleIdentity verify(String identityToken, String nonce) {
        if (!StringUtils.hasText(identityToken)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "Apple identityToken不能为空");
        }
        if (clientIds.isEmpty()) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "闪卡APP Apple登录尚未配置Client ID");
        }
        try {
            DecodedJWT unverified = JWT.decode(identityToken.trim());
            String keyId = unverified.getKeyId();
            RSAPublicKey publicKey = getAppleKeys(false).get(keyId);
            if (publicKey == null) publicKey = getAppleKeys(true).get(keyId);
            if (publicKey == null) {
                throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "Apple登录凭证签名密钥无效");
            }

            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null))
                    .withIssuer(APPLE_ISSUER)
                    .build();
            DecodedJWT verified = verifier.verify(identityToken.trim());
            boolean acceptedAudience = verified.getAudience() != null
                    && verified.getAudience().stream().anyMatch(clientIds::contains);
            if (!acceptedAudience) {
                throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "Apple登录凭证不属于当前APP");
            }
            if (StringUtils.hasText(nonce)) {
                String tokenNonce = verified.getClaim("nonce").asString();
                if (!StringUtils.hasText(tokenNonce) || !constantTimeEquals(nonce.trim(), tokenNonce)) {
                    throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "Apple登录nonce校验失败");
                }
            }
            if (!StringUtils.hasText(verified.getSubject())) {
                throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "Apple登录凭证缺少用户标识");
            }
            return new FlashcardAppleIdentity(verified.getSubject(), verified.getClaim("email").asString());
        } catch (HopeException ex) {
            throw ex;
        } catch (JWTVerificationException | IllegalArgumentException ex) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "Apple登录凭证无效或已过期", ex);
        }
    }

    private Map<String, RSAPublicKey> getAppleKeys(boolean forceRefresh) {
        long now = System.currentTimeMillis();
        if (!forceRefresh && now < keysExpireAt && !cachedKeys.isEmpty()) return cachedKeys;
        synchronized (this) {
            now = System.currentTimeMillis();
            if (!forceRefresh && now < keysExpireAt && !cachedKeys.isEmpty()) return cachedKeys;
            try {
                AppleKeysResponse response = restTemplate.getForObject(APPLE_KEYS_URL, AppleKeysResponse.class);
                Map<String, RSAPublicKey> keys = new HashMap<>();
                if (response != null && response.getKeys() != null) {
                    for (AppleJwk item : response.getKeys()) {
                        if (item != null && StringUtils.hasText(item.getKid())
                                && "RSA".equals(item.getKty()) && "RS256".equals(item.getAlg())) {
                            keys.put(item.getKid(), toPublicKey(item));
                        }
                    }
                }
                if (keys.isEmpty()) throw new IllegalStateException("Apple没有返回可用公钥");
                cachedKeys = Collections.unmodifiableMap(keys);
                keysExpireAt = now + KEY_CACHE_MILLIS;
                return cachedKeys;
            } catch (RestClientException | java.security.GeneralSecurityException | IllegalStateException ex) {
                if (!cachedKeys.isEmpty()) return cachedKeys;
                throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "暂时无法校验Apple登录凭证", ex);
            }
        }
    }

    private RSAPublicKey toPublicKey(AppleJwk key) throws java.security.GeneralSecurityException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        BigInteger modulus = new BigInteger(1, decoder.decode(key.getN()));
        BigInteger exponent = new BigInteger(1, decoder.decode(key.getE()));
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new RSAPublicKeySpec(modulus, exponent));
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    public static class AppleKeysResponse {
        private List<AppleJwk> keys;
        public List<AppleJwk> getKeys() { return keys; }
        public void setKeys(List<AppleJwk> keys) { this.keys = keys; }
    }

    public static class AppleJwk {
        private String kty;
        private String kid;
        private String alg;
        private String n;
        private String e;
        public String getKty() { return kty; }
        public void setKty(String kty) { this.kty = kty; }
        public String getKid() { return kid; }
        public void setKid(String kid) { this.kid = kid; }
        public String getAlg() { return alg; }
        public void setAlg(String alg) { this.alg = alg; }
        public String getN() { return n; }
        public void setN(String n) { this.n = n; }
        public String getE() { return e; }
        public void setE(String e) { this.e = e; }
    }
}
