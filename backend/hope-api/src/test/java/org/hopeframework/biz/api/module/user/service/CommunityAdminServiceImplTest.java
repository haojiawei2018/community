package org.hopeframework.biz.api.module.user.service;

import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.module.auth.mapper.IamUserMapper;
import org.hopeframework.biz.api.module.auth.mapper.MemberAuthorityMapper;
import org.hopeframework.biz.api.module.auth.mapper.TenantMemberRoleMapper;
import org.hopeframework.biz.api.module.auth.mapper.TenantRoleMapper;
import org.hopeframework.biz.api.module.user.dto.MemberStatusRequest;
import org.hopeframework.biz.api.module.user.mapper.TenantMemberMapper;
import org.hopeframework.biz.api.module.user.model.TenantMember;
import org.hopeframework.biz.api.module.user.service.impl.CommunityAdminServiceImpl;
import org.hopeframework.core.exception.HopeException;
import org.junit.After;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommunityAdminServiceImplTest {

    @After
    public void tearDown() {
        AuthContext.clear();
    }

    @Test(expected = HopeException.class)
    public void shouldRejectBanningCurrentMember() {
        TenantMemberMapper memberMapper = mock(TenantMemberMapper.class);
        TenantMember member = new TenantMember();
        member.setId(2L);
        member.setUserId(1L);
        member.setStatus("ACTIVE");
        when(memberMapper.selectById(2L)).thenReturn(member);
        CommunityAdminServiceImpl service = new CommunityAdminServiceImpl(
                memberMapper,
                mock(IamUserMapper.class),
                mock(TenantRoleMapper.class),
                mock(TenantMemberRoleMapper.class),
                mock(MemberAuthorityMapper.class));
        AuthContext.set(new AuthPrincipal(1L, 2L, 1L));
        MemberStatusRequest request = new MemberStatusRequest();
        request.setStatus("BANNED");

        service.updateMemberStatus(2L, request);
    }
}
