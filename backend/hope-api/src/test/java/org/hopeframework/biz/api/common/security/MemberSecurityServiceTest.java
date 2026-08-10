package org.hopeframework.biz.api.common.security;

import org.hopeframework.biz.api.mapper.auth.MemberAuthorityMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MemberSecurityServiceTest {

    @Test
    public void ownerShouldBypassPermissionCatalogLookup() {
        TenantMemberMapper memberMapper = mock(TenantMemberMapper.class);
        MemberAuthorityMapper authorityMapper = mock(MemberAuthorityMapper.class);
        MemberSecurityService service = new MemberSecurityService(memberMapper, authorityMapper);
        when(authorityMapper.selectRoleCodes(2L)).thenReturn(Collections.singletonList("OWNER"));

        service.requirePermission(2L, "member.role.write");

        verify(authorityMapper, never()).selectPermissionCodes(2L);
    }
}
