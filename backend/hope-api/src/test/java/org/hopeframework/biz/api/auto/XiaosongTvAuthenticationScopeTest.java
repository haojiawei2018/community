package org.hopeframework.biz.api.auto;

import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.MemberSecurityService;
import org.hopeframework.biz.api.common.security.PetSnackAccessTokenService;
import org.hopeframework.biz.api.common.security.XiaosongTvAccessTokenService;
import org.hopeframework.biz.api.common.security.IronBoxAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.controller.xiaosongtv.XiaosongTvController;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvService;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvPhoneCodeService;
import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class XiaosongTvAuthenticationScopeTest {
    private final AccessTokenService communityTokenService =
            new AccessTokenService("community-test-secret-at-least-32-bytes", 3600);
    private final PetSnackAccessTokenService petSnackTokenService =
            new PetSnackAccessTokenService("pet-snack-test-secret-at-least-32-bytes");
    private final XiaosongTvAccessTokenService xiaosongTvTokenService =
            new XiaosongTvAccessTokenService("xiaosong-tv-test-secret-at-least-32-bytes");
    private final AuthenticationInterceptor interceptor = new AuthenticationInterceptor(
            communityTokenService, petSnackTokenService, xiaosongTvTokenService,
            new IronBoxAccessTokenService("ironbox-test-secret-at-least-32-bytes"),
            mock(MemberSecurityService.class));

    @After
    public void tearDown() {
        AuthContext.clear();
        TenantContext.clear();
    }

    @Test
    public void acceptsXiaosongTokenForXiaosongController() throws Exception {
        TenantContext.set(1L, "default");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + xiaosongTvTokenService.create(10L, 1L));

        boolean accepted = interceptor.preHandle(
                request, new MockHttpServletResponse(), xiaosongHandler());

        assertTrue(accepted);
        assertTrue(AuthContext.current().isXiaosongTvUser());
    }

    @Test
    public void rejectsCommunityTokenForXiaosongController() throws Exception {
        TenantContext.set(1L, "default");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + communityTokenService.create(
                new AuthPrincipal(10L, 10L, 1L)));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean accepted = interceptor.preHandle(request, response, xiaosongHandler());

        assertFalse(accepted);
        assertEquals(401, response.getStatus());
    }

    @Test
    public void rejectsXiaosongTokenForAnotherBusinessController() throws Exception {
        TenantContext.set(1L, "default");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + xiaosongTvTokenService.create(10L, 1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean accepted = interceptor.preHandle(request, response, otherHandler());

        assertFalse(accepted);
        assertEquals(401, response.getStatus());
    }

    private HandlerMethod xiaosongHandler() throws NoSuchMethodException {
        XiaosongTvController controller = new XiaosongTvController(
                mock(IXiaosongTvService.class), mock(IXiaosongTvPhoneCodeService.class));
        return new HandlerMethod(controller, XiaosongTvController.class.getMethod("listCds"));
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
