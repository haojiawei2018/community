package org.hopeframework.biz.api.service.auth;

import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.common.tenant.SingleCommunityProperties;
import org.hopeframework.biz.api.entity.input.auth.RegisterRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.mapper.auth.IamRefreshTokenMapper;
import org.hopeframework.biz.api.mapper.auth.IamUserIdentityMapper;
import org.hopeframework.biz.api.mapper.auth.IamUserMapper;
import org.hopeframework.biz.api.mapper.auth.MemberAuthorityMapper;
import org.hopeframework.biz.api.mapper.auth.TenantMemberRoleMapper;
import org.hopeframework.biz.api.mapper.auth.TenantRoleMapper;
import org.hopeframework.biz.api.model.auth.IamRefreshToken;
import org.hopeframework.biz.api.model.auth.IamUser;
import org.hopeframework.biz.api.model.auth.IamUserIdentity;
import org.hopeframework.biz.api.model.auth.TenantRole;
import org.hopeframework.biz.api.service.impl.auth.AuthServiceImpl;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.user.TenantMember;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceImplTest {
    private IamUserMapper userMapper;
    private IamUserIdentityMapper identityMapper;
    private IamRefreshTokenMapper refreshTokenMapper;
    private TenantMemberMapper memberMapper;
    private TenantRoleMapper roleMapper;
    private AuthServiceImpl service;

    @Before
    public void setUp() {
        userMapper = mock(IamUserMapper.class);
        identityMapper = mock(IamUserIdentityMapper.class);
        refreshTokenMapper = mock(IamRefreshTokenMapper.class);
        memberMapper = mock(TenantMemberMapper.class);
        roleMapper = mock(TenantRoleMapper.class);
        TenantMemberRoleMapper memberRoleMapper = mock(TenantMemberRoleMapper.class);
        MemberAuthorityMapper authorityMapper = mock(MemberAuthorityMapper.class);
        AccessTokenService tokenService = new AccessTokenService("test-access-token-secret-at-least-32-bytes", 3600);
        SingleCommunityProperties communityProperties = new SingleCommunityProperties();
        service = new AuthServiceImpl(userMapper, identityMapper, refreshTokenMapper, memberMapper,
                roleMapper, memberRoleMapper, authorityMapper, tokenService, communityProperties);
        TenantContext.set(10L, "official");
        when(identityMapper.selectOne(any())).thenReturn(null);
        doAnswer(invocation -> { ((IamUser) invocation.getArgument(0)).setId(1L); return 1; })
                .when(userMapper).insert(any());
        doAnswer(invocation -> { ((TenantMember) invocation.getArgument(0)).setId(2L); return 1; })
                .when(memberMapper).insert(any());
        TenantRole role = new TenantRole();
        role.setId(3L);
        when(roleMapper.selectOne(any())).thenReturn(role);
        when(authorityMapper.selectRoleCodes(2L)).thenReturn(Collections.singletonList("MEMBER"));
        when(authorityMapper.selectPermissionCodes(2L)).thenReturn(Collections.emptyList());
    }

    @After
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void shouldHashPasswordAndRefreshTokenDuringRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Player_01");
        request.setPassword("safe-password-123");
        request.setNickname("玩家一号");

        TokenResponse response = service.register(request, "127.0.0.1");

        ArgumentCaptor<IamUserIdentity> identityCaptor = ArgumentCaptor.forClass(IamUserIdentity.class);
        verify(identityMapper).insert(identityCaptor.capture());
        assertTrue(new BCryptPasswordEncoder().matches(request.getPassword(), identityCaptor.getValue().getCredentialHash()));
        assertFalse(identityCaptor.getValue().getCredentialHash().contains(request.getPassword()));

        ArgumentCaptor<IamRefreshToken> refreshCaptor = ArgumentCaptor.forClass(IamRefreshToken.class);
        verify(refreshTokenMapper).insert(refreshCaptor.capture());
        assertEquals(64, refreshCaptor.getValue().getTokenHash().length());
        assertFalse(refreshCaptor.getValue().getTokenHash().equals(response.getRefreshToken()));
        assertEquals("player_01", response.getUser().getUsername());
    }
}
