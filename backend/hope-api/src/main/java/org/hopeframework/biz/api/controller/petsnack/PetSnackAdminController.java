package org.hopeframework.biz.api.controller.petsnack;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.service.petsnack.IPetSnackAdminService;
import org.hopeframework.biz.api.service.petsnack.IPetSnackImageStorageService;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Api(tags = "宠物零食商城后台管理")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1/pet-snack")
public class PetSnackAdminController {
    private final IPetSnackAdminService adminService;
    private final AccessTokenService accessTokenService;
    private final IPetSnackImageStorageService imageStorageService;
    @Value("${pet-snack.admin.username:admin}") private String username;
    @Value("${pet-snack.admin.password}") private String password;

    public PetSnackAdminController(IPetSnackAdminService adminService, AccessTokenService accessTokenService,
                                   IPetSnackImageStorageService imageStorageService) {
        this.adminService = adminService;
        this.accessTokenService = accessTokenService;
        this.imageStorageService = imageStorageService;
    }

    @PassToken
    @ApiOperation("宠物商城后台登录")
    @PostMapping("/login")
    public RespBody<TokenResponse> login(@RequestBody Map<String, String> request) {
        if (request == null || !username.equals(request.get("username")) || !password.equals(request.get("password"))) {
            throw new HopeException(401, "账号或密码错误");
        }
        Long tenantId = TenantContext.requireTenantId();
        AuthPrincipal principal = new AuthPrincipal(0L, 0L, tenantId, "PET_SNACK_ADMIN");
        UserSessionResponse user = new UserSessionResponse();
        user.setUserId(0L); user.setMemberId(0L); user.setTenantId(tenantId);
        user.setUsername(username); user.setNickname("宠物商城管理员"); user.setDisplayName("宠物商城管理员");
        user.setMemberStatus("ACTIVE"); user.setRoles(Collections.singletonList("PET_SNACK_ADMIN"));
        user.setPermissions(Arrays.asList("tenant.config.read", "tenant.config.write"));
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.createPermanent(principal));
        response.setExpiresIn(0L); response.setUser(user);
        return ResultUtil.success(response);
    }

    @RequirePermission("tenant.config.read") @GetMapping("/overview")
    public RespBody<Map<String, Object>> overview() { return ResultUtil.success(adminService.overview()); }

    @RequirePermission("tenant.config.read") @GetMapping("/store")
    public RespBody<Map<String, Object>> store() { return ResultUtil.success(adminService.store()); }

    @RequirePermission("tenant.config.write") @PutMapping("/store")
    public RespBody<Map<String, Object>> updateStore(@RequestBody Map<String, Object> request) {
        return ResultUtil.success(adminService.updateStore(request));
    }

    @RequirePermission("tenant.config.read") @GetMapping("/users")
    public RespBody<PageResult<Map<String, Object>>> users(@RequestParam(required=false) String keyword,
            @RequestParam(required=false) String status, @RequestParam(defaultValue="1") long page,
            @RequestParam(defaultValue="20") long pageSize) {
        return ResultUtil.success(adminService.users(keyword, status, page, pageSize));
    }

    @RequirePermission("tenant.config.read") @GetMapping("/orders")
    public RespBody<PageResult<Map<String, Object>>> orders(@RequestParam(required=false) String keyword,
            @RequestParam(required=false) String status, @RequestParam(defaultValue="1") long page,
            @RequestParam(defaultValue="20") long pageSize) {
        return ResultUtil.success(adminService.orders(keyword, status, page, pageSize));
    }

    @RequirePermission("tenant.config.read") @GetMapping("/orders/{orderId}")
    public RespBody<Map<String, Object>> order(@PathVariable Long orderId) {
        return ResultUtil.success(adminService.order(orderId));
    }

    @RequirePermission("tenant.config.read") @GetMapping("/categories")
    public RespBody<java.util.List<Map<String,Object>>> categories(){return ResultUtil.success(adminService.categories());}
    @RequirePermission("tenant.config.write") @PostMapping("/categories")
    public RespBody<Map<String,Object>> createCategory(@RequestBody Map<String,Object> request){return ResultUtil.success(adminService.saveCategory(null,request));}
    @RequirePermission("tenant.config.write") @PutMapping("/categories/{id}")
    public RespBody<Map<String,Object>> updateCategory(@PathVariable Long id,@RequestBody Map<String,Object> request){return ResultUtil.success(adminService.saveCategory(id,request));}
    @RequirePermission("tenant.config.read") @GetMapping("/products")
    public RespBody<java.util.List<Map<String,Object>>> products(@RequestParam(required=false)String keyword,@RequestParam(required=false)Long categoryId){return ResultUtil.success(adminService.products(keyword,categoryId));}
    @RequirePermission("tenant.config.read") @GetMapping("/products/{id}")
    public RespBody<Map<String,Object>> product(@PathVariable Long id){return ResultUtil.success(adminService.product(id));}
    @RequirePermission("tenant.config.write") @PostMapping("/products")
    public RespBody<Map<String,Object>> createProduct(@RequestBody Map<String,Object> request){return ResultUtil.success(adminService.saveProduct(null,request));}
    @RequirePermission("tenant.config.write") @PutMapping("/products/{id}")
    public RespBody<Map<String,Object>> updateProduct(@PathVariable Long id,@RequestBody Map<String,Object> request){return ResultUtil.success(adminService.saveProduct(id,request));}
    @RequirePermission("tenant.config.write") @PostMapping("/images")
    public RespBody<ImageUploadResponse> upload(@RequestParam("file") MultipartFile file){return ResultUtil.success(imageStorageService.upload(file));}
}
