package org.hopeframework.biz.api.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal {
    private final Long userId;
    private final Long memberId;
    private final Long tenantId;
}
