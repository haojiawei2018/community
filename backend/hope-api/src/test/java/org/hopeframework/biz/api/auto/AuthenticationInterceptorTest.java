package org.hopeframework.biz.api.auto;

import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.MemberSecurityService;
import org.hopeframework.biz.api.common.security.PetSnackAccessTokenService;
import org.hopeframework.biz.api.common.security.XiaosongTvAccessTokenService;
import org.hopeframework.biz.api.common.security.IronBoxAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthenticationInterceptorTest {
    private final AccessTokenService tokenService =
            new AccessTokenService("test-access-token-secret-at-least-32-bytes", 3600);
    private final MemberSecurityService memberSecurityService = mock(MemberSecurityService.class);
    private final PetSnackAccessTokenService petSnackTokenService =
            new PetSnackAccessTokenService("pet-snack-test-secret-at-least-32-bytes");
    private final XiaosongTvAccessTokenService xiaosongTvTokenService =
            new XiaosongTvAccessTokenService("xiaosong-tv-test-secret-at-least-32-bytes");
    private final IronBoxAccessTokenService ironBoxTokenService =
            new IronBoxAccessTokenService("ironbox-test-secret-at-least-32-bytes");
    private final AuthenticationInterceptor interceptor =
            new AuthenticationInterceptor(
                    tokenService, petSnackTokenService, xiaosongTvTokenService, ironBoxTokenService, memberSecurityService);

    @After
    public void tearDown() {
        AuthContext.clear();
        TenantContext.clear();
    }

    @Test
    public void shouldAcceptBearerTokenForCurrentTenant() throws Exception {
        AuthPrincipal principal = new AuthPrincipal(1L, 2L, 3L);
        TenantContext.set(3L, "official");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokenService.create(principal));

        boolean accepted = interceptor.preHandle(request, new MockHttpServletResponse(), handlerMethod());

        assertTrue(accepted);
        verify(memberSecurityService).validate(org.mockito.ArgumentMatchers.any(AuthPrincipal.class));
    }

    @Test
    public void shouldRejectTokenFromAnotherTenant() throws Exception {
        TenantContext.set(99L, "another");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " +
                tokenService.create(new AuthPrincipal(1L, 2L, 3L)));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean accepted = interceptor.preHandle(request, response, handlerMethod());

        assertFalse(accepted);
        assertEquals(403, response.getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedJsonWhenTokenIsMissing() throws Exception {
        TenantContext.set(3L, "official");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean accepted = interceptor.preHandle(
                new MockHttpServletRequest(), response, handlerMethod());

        assertFalse(accepted);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":401"));
    }

    @Test
    public void shouldEstablishContextForOptionalAuthenticatedRequest() throws Exception {
        TenantContext.set(3L, "official");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " +
                tokenService.create(new AuthPrincipal(1L, 2L, 3L)));

        boolean accepted = interceptor.preHandle(request, new MockHttpServletResponse(), openHandlerMethod());

        assertTrue(accepted);
        assertNotNull(AuthContext.current());
        verify(memberSecurityService).validate(org.mockito.ArgumentMatchers.any(AuthPrincipal.class));
    }

    @Test
    public void shouldKeepFlashcardIdentityIsolatedFromCommunityMemberValidation() throws Exception {
        TenantContext.set(3L, "official");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokenService.create(
                new AuthPrincipal(31L, 31L, 99L, "FLASHCARD_APP")));

        boolean accepted = interceptor.preHandle(request, new MockHttpServletResponse(), handlerMethod());

        assertTrue(accepted);
        assertTrue(AuthContext.current().isFlashcardUser());
        verify(memberSecurityService, org.mockito.Mockito.never())
                .validate(org.mockito.ArgumentMatchers.any(AuthPrincipal.class));
    }

    private HandlerMethod handlerMethod() throws NoSuchMethodException {
        return new HandlerMethod(new SecuredController(), SecuredController.class.getMethod("secured"));
    }

    private HandlerMethod openHandlerMethod() throws NoSuchMethodException {
        return new HandlerMethod(new SecuredController(), SecuredController.class.getMethod("open"));
    }

    private static class SecuredController {
        @UserLoginToken
        public void secured() {
        }


        public void open() {
        }
    }
}
