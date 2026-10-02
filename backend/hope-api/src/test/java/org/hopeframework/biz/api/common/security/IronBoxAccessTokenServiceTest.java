package org.hopeframework.biz.api.common.security;

import com.auth0.jwt.JWT;
import org.hopeframework.core.exception.HopeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IronBoxAccessTokenServiceTest {
    @Test
    public void permanentTokenHasSeparateIssuerAndType() {
        IronBoxAccessTokenService service = new IronBoxAccessTokenService("ironbox-test-secret-at-least-32-bytes");
        String token = service.create(42L, 1L);
        assertNull(JWT.decode(token).getExpiresAt());
        assertEquals("ai-ironbox", JWT.decode(token).getIssuer());
        assertTrue(service.verify(token).isIronBoxUser());
        assertEquals(Long.valueOf(42L), service.verify(token).getUserId());
    }

    @Test(expected = HopeException.class)
    public void rejectsTokenFromOtherProduct() {
        IronBoxAccessTokenService service = new IronBoxAccessTokenService("ironbox-test-secret-at-least-32-bytes");
        String other = new XiaosongTvAccessTokenService("xiaosong-tv-test-secret-at-least-32-bytes").create(42L, 1L);
        service.verify(other);
    }
}
