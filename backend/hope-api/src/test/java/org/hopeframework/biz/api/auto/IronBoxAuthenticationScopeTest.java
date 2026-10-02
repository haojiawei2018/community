package org.hopeframework.biz.api.auto;

import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.IronBoxAccessTokenService;
import org.hopeframework.biz.api.common.security.MemberSecurityService;
import org.hopeframework.biz.api.common.security.PetSnackAccessTokenService;
import org.hopeframework.biz.api.common.security.XiaosongTvAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.controller.ironbox.IronBoxController;
import org.hopeframework.biz.api.service.ironbox.IronBoxService;
import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IronBoxAuthenticationScopeTest {
    private final AccessTokenService community = new AccessTokenService("community-test-secret-at-least-32-bytes", 3600);
    private final IronBoxAccessTokenService ironbox = new IronBoxAccessTokenService("ironbox-test-secret-at-least-32-bytes");
    private final AuthenticationInterceptor interceptor = new AuthenticationInterceptor(
            community, new PetSnackAccessTokenService("pet-snack-test-secret-at-least-32-bytes"),
            new XiaosongTvAccessTokenService("xiaosong-tv-test-secret-at-least-32-bytes"),
            ironbox, mock(MemberSecurityService.class));

    @After public void tearDown() { AuthContext.clear(); TenantContext.clear(); }

    @Test public void acceptsOwnToken() throws Exception {
        TenantContext.set(1L, "default");
        assertTrue(interceptor.preHandle(request(ironbox.create(7L, 1L)), new MockHttpServletResponse(), ironboxHandler()));
        assertTrue(AuthContext.current().isIronBoxUser());
    }

    @Test public void rejectsCommunityTokenForIronBox() throws Exception {
        TenantContext.set(1L, "default"); MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request(community.create(new AuthPrincipal(7L, 7L, 1L))), response, ironboxHandler()));
        assertEquals(401, response.getStatus());
    }

    @Test public void rejectsIronBoxTokenForAnotherProduct() throws Exception {
        TenantContext.set(1L, "default"); MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request(ironbox.create(7L, 1L)), response, otherHandler()));
        assertEquals(401, response.getStatus());
    }

    @Test public void rejectsIronBoxTokenFromAnotherTenant() throws Exception {
        TenantContext.set(2L, "another"); MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(request(ironbox.create(7L, 1L)), response, ironboxHandler()));
        assertEquals(403, response.getStatus());
    }

    private MockHttpServletRequest request(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token); return request;
    }
    private HandlerMethod ironboxHandler() throws Exception {
        return new HandlerMethod(new IronBoxController(mock(IronBoxService.class)),
                IronBoxController.class.getMethod("create", Map.class));
    }
    private HandlerMethod otherHandler() throws Exception {
        return new HandlerMethod(new OtherController(), OtherController.class.getMethod("secured"));
    }
    private static class OtherController { @UserLoginToken public void secured() {} }
}
