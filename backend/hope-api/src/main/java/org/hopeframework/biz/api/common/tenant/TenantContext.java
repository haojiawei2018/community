package org.hopeframework.biz.api.common.tenant;

/**
 * 当前请求的租户上下文。租户 ID 只能由服务端根据租户编码解析，不能信任客户端直接传入。
 */
public final class TenantContext {

    private static final ThreadLocal<TenantInfo> CONTEXT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(Long tenantId, String tenantCode) {
        CONTEXT.set(new TenantInfo(tenantId, tenantCode));
    }

    public static Long getTenantId() {
        TenantInfo info = CONTEXT.get();
        return info == null ? null : info.tenantId;
    }

    public static Long requireTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required for tenant-scoped data access");
        }
        return tenantId;
    }

    public static String getTenantCode() {
        TenantInfo info = CONTEXT.get();
        return info == null ? null : info.tenantCode;
    }

    public static void clear() {
        CONTEXT.remove();
    }

    private static final class TenantInfo {
        private final Long tenantId;
        private final String tenantCode;

        private TenantInfo(Long tenantId, String tenantCode) {
            this.tenantId = tenantId;
            this.tenantCode = tenantCode;
        }
    }
}
