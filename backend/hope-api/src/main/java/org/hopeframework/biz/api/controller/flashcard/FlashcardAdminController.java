package org.hopeframework.biz.api.controller.flashcard;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.flashcard.admin.FlashcardAdminLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.admin.UpdateFlashcardHomeConfigRequest;
import org.hopeframework.biz.api.entity.input.flashcard.admin.UpdateFlashcardUserStatusRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.entity.output.flashcard.admin.FlashcardAdminDataResponse;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAdminService;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@Api(tags = "闪藏馆后台管理系统")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1/flashcard")
public class FlashcardAdminController {
    private final IFlashcardAdminService adminService;
    private final AccessTokenService accessTokenService;

    @Value("${flashcard.admin.username:admin}")
    private String adminUsername;
    @Value("${flashcard.admin.password}")
    private String adminPassword;

    public FlashcardAdminController(IFlashcardAdminService adminService,
                                    AccessTokenService accessTokenService) {
        this.adminService = adminService;
        this.accessTokenService = accessTokenService;
    }

    @PassToken
    @ApiOperation("闪藏馆后台登录")
    @PostMapping("/login")
    public RespBody<TokenResponse> login(@RequestBody FlashcardAdminLoginRequest request) {
        if (request == null || !adminUsername.equals(request.getUsername())
                || !adminPassword.equals(request.getPassword())) {
            throw new HopeException(401, "账号或密码错误");
        }
        Long tenantId = TenantContext.requireTenantId();
        AuthPrincipal principal = new AuthPrincipal(0L, 0L, tenantId, "FLASHCARD_ADMIN");
        UserSessionResponse user = new UserSessionResponse();
        user.setUserId(0L);
        user.setMemberId(0L);
        user.setTenantId(tenantId);
        user.setUsername(adminUsername);
        user.setNickname("闪藏馆管理员");
        user.setDisplayName("闪藏馆管理员");
        user.setMemberStatus("ACTIVE");
        user.setRoles(Collections.singletonList("FLASHCARD_ADMIN"));
        user.setPermissions(Arrays.asList("tenant.config.read", "tenant.config.write"));
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.createPermanent(principal));
        response.setExpiresIn(0L);
        response.setUser(user);
        return ResultUtil.success(response);
    }

    @RequirePermission("tenant.config.read")
    @GetMapping("/overview")
    public RespBody<FlashcardAdminDataResponse.Overview> overview() {
        return ResultUtil.success(adminService.overview());
    }

    @RequirePermission("tenant.config.read")
    @GetMapping("/cards")
    public RespBody<PageResult<FlashcardAdminDataResponse.CardItem>> cards(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ResultUtil.success(adminService.cards(keyword, page, pageSize));
    }

    @RequirePermission("tenant.config.read")
    @GetMapping("/users")
    public RespBody<PageResult<FlashcardAdminDataResponse.UserItem>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize) {
        return ResultUtil.success(adminService.users(keyword, status, page, pageSize));
    }

    @RequirePermission("tenant.config.read")
    @GetMapping("/home-config")
    public RespBody<FlashcardAdminDataResponse.HomeConfig> homeConfig() {
        return ResultUtil.success(adminService.homeConfig());
    }

    @RequirePermission("tenant.config.write")
    @PutMapping("/home-config")
    public RespBody<FlashcardAdminDataResponse.HomeConfig> updateHomeConfig(
            @RequestBody UpdateFlashcardHomeConfigRequest request) {
        return ResultUtil.success(adminService.updateHomeConfig(request));
    }

    @RequirePermission("tenant.config.write")
    @PutMapping("/users/{userId}/status")
    public RespBody<FlashcardAdminDataResponse.UserItem> updateUserStatus(
            @PathVariable Long userId, @RequestBody UpdateFlashcardUserStatusRequest request) {
        return ResultUtil.success(adminService.updateUserStatus(userId, request == null ? null : request.getStatus()));
    }
}
