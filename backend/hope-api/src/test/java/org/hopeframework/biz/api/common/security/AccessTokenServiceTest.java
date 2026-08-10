package org.hopeframework.biz.api.common.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
