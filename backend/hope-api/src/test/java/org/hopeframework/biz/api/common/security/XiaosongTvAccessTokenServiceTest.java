package org.hopeframework.biz.api.common.security;

import com.auth0.jwt.JWT;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class XiaosongTvAccessTokenServiceTest {
    @Test
    public void createsPermanentIsolatedToken() {
        XiaosongTvAccessTokenService service =
                new XiaosongTvAccessTokenService("xiaosong-tv-test-secret-at-least-32-bytes");

        String token = service.create(101L, 1L);
        AuthPrincipal principal = service.verify(token);

        assertNull(JWT.decode(token).getExpiresAt());
        assertEquals("xiaosong-tv", JWT.decode(token).getIssuer());
        assertEquals(Long.valueOf(101L), principal.getUserId());
        assertTrue(principal.isXiaosongTvUser());
    }
}
