package org.hopeframework.biz.api.common.security;

import com.auth0.jwt.JWT;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PetSnackAccessTokenServiceTest {

    @Test
    public void createsPermanentPetSnackTokenWithoutExpiration() {
        PetSnackAccessTokenService service =
                new PetSnackAccessTokenService("pet-snack-test-secret-at-least-32-bytes");

        String token = service.create(1001L);

        assertNull(JWT.decode(token).getExpiresAt());
        assertEquals("pet-snack-miniapp", JWT.decode(token).getIssuer());
        assertTrue(service.verify(token).isPetSnackUser());
    }
}
