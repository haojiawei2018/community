package org.hopeframework.biz.api.auto;

import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.MemberSecurityService;
import org.hopeframework.biz.api.common.security.PetSnackAccessTokenService;
import org.hopeframework.biz.api.common.security.XiaosongTvAccessTokenService;
import org.hopeframework.biz.api.common.security.IronBoxAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.controller.petsnack.PetSnackController;
import org.hopeframework.biz.api.service.petsnack.IPetSnackService;
import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PetSnackAuthenticationScopeTest {
    private final AccessTokenService tokenService =
            new AccessTokenService("test-access-token-secret-at-least-32-bytes", 3600);
    private final PetSnackAccessTokenService petSnackTokenService =
            new PetSnackAccessTokenService("pet-snack-test-secret-at-least-32-bytes");
    private final XiaosongTvAccessTokenService xiaosongTvTokenService =
            new XiaosongTvAccessTokenService("xiaosong-tv-test-secret-at-least-32-bytes");
    private final AuthenticationInterceptor interceptor = new AuthenticationInterceptor(
            tokenService, petSnackTokenService, xiaosongTvTokenService,
            new IronBoxAccessTokenService("ironbox-test-secret-at-least-32-bytes"), mock(MemberSecurityService.class));

    @After
    public void tearDown() {
        AuthContext.clear();
        TenantContext.clear();
    }

    @Test
    public void acceptsPetSnackTokenOnlyForPetSnackController() throws Exception {
        TenantContext.set(1L, "default");
        MockHttpServletRequest request = requestWithToken(
                new AuthPrincipal(10L, 10L, 1L, "PET_SNACK"));

        boolean accepted = interceptor.preHandle(request, new MockHttpServletResponse(), petSnackHandler());

        assertTrue(accepted);
        assertTrue(AuthContext.current().isPetSnackUser());
    }

    @Test
    public void rejectsPetSnackTokenForAnotherBusinessController() throws Exception {
        TenantContext.set(1L, "default");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean accepted = interceptor.preHandle(requestWithToken(
                new AuthPrincipal(10L, 10L, 1L, "PET_SNACK")), response, otherHandler());

        assertFalse(accepted);
        assertEquals(401, response.getStatus());
    }

    @Test
    public void rejectsCommunityTokenForPetSnackPrivateEndpoint() throws Exception {
        TenantContext.set(1L, "default");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean accepted = interceptor.preHandle(requestWithToken(
                new AuthPrincipal(10L, 10L, 1L, "COMMUNITY")), response, petSnackHandler());

        assertFalse(accepted);
        assertEquals(401, response.getStatus());
    }

    private MockHttpServletRequest requestWithToken(AuthPrincipal principal) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token = principal.isPetSnackUser()
                ? petSnackTokenService.create(principal.getUserId())
                : tokenService.create(principal);
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    private HandlerMethod petSnackHandler() throws NoSuchMethodException {
        PetSnackController controller = new PetSnackController(mock(IPetSnackService.class));
        return new HandlerMethod(controller, PetSnackController.class.getMethod("currentUser"));
    }

    private HandlerMethod otherHandler() throws NoSuchMethodException {
        return new HandlerMethod(new OtherController(), OtherController.class.getMethod("secured"));
    }

    private static class OtherController {
        @UserLoginToken
        public void secured() {
        }
    }
}
