package org.hopeframework.biz.api.common.security;

import com.auth0.jwt.JWT;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AccessTokenServiceTest {

    @Test
    public void shouldCreateAndVerifyTenantBoundToken() {
        AccessTokenService service = new AccessTokenService("test-access-token-secret-at-least-32-bytes", 3600);
        AuthPrincipal expected = new AuthPrincipal(11L, 22L, 33L);

        AuthPrincipal actual = service.verify(service.create(expected));

        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getMemberId(), actual.getMemberId());
        assertEquals(expected.getTenantId(), actual.getTenantId());
    }

    @Test
    public void shouldCreatePermanentBookingAdminTokenWithoutExpiration() {
        AccessTokenService service = new AccessTokenService("test-access-token-secret-at-least-32-bytes", 1);
        AuthPrincipal expected = new AuthPrincipal(0L, 0L, 33L, "BOOKING_ADMIN");

        String token = service.createPermanent(expected);
        AuthPrincipal actual = service.verify(token);

        assertNull(JWT.decode(token).getExpiresAt());
        assertEquals("BOOKING_ADMIN", actual.getPrincipalType());
        assertEquals(expected.getTenantId(), actual.getTenantId());
    }
}
