package org.hopeframework.biz.api.common.security;

public final class AuthContext {
    private static final ThreadLocal<AuthPrincipal> CONTEXT = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthPrincipal principal) {
        CONTEXT.set(principal);
    }

    public static AuthPrincipal require() {
        AuthPrincipal principal = CONTEXT.get();
        if (principal == null) {
            throw new IllegalStateException("Authenticated user context is required");
        }
        return principal;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
