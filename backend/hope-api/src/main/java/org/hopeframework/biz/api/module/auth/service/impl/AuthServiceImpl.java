package org.hopeframework.biz.api.module.auth.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.module.auth.dto.LoginRequest;
import org.hopeframework.biz.api.module.auth.dto.LogoutRequest;
import org.hopeframework.biz.api.module.auth.dto.RefreshRequest;
import org.hopeframework.biz.api.module.auth.dto.RegisterRequest;
import org.hopeframework.biz.api.module.auth.dto.TokenResponse;
import org.hopeframework.biz.api.module.auth.dto.UserSessionResponse;
import org.hopeframework.biz.api.module.auth.mapper.IamRefreshTokenMapper;
import org.hopeframework.biz.api.module.auth.mapper.IamUserIdentityMapper;
import org.hopeframework.biz.api.module.auth.mapper.IamUserMapper;
import org.hopeframework.biz.api.module.auth.mapper.MemberAuthorityMapper;
import org.hopeframework.biz.api.module.auth.mapper.TenantMemberRoleMapper;
import org.hopeframework.biz.api.module.auth.mapper.TenantRoleMapper;
import org.hopeframework.biz.api.module.auth.model.IamRefreshToken;
import org.hopeframework.biz.api.module.auth.model.IamUser;
import org.hopeframework.biz.api.module.auth.model.IamUserIdentity;
import org.hopeframework.biz.api.module.auth.model.TenantMemberRole;
import org.hopeframework.biz.api.module.auth.model.TenantRole;
import org.hopeframework.biz.api.module.auth.service.AuthService;
import org.hopeframework.biz.api.module.user.dto.UpdateProfileRequest;
import org.hopeframework.biz.api.module.user.mapper.TenantMemberMapper;
import org.hopeframework.biz.api.module.user.model.TenantMember;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@DS("master")
public class AuthServiceImpl implements AuthService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{3,31}$");
    private static final long REFRESH_TOKEN_MILLIS = 30L * 24 * 60 * 60 * 1000;

    private final IamUserMapper userMapper;
    private final IamUserIdentityMapper identityMapper;
    private final IamRefreshTokenMapper refreshTokenMapper;
    private final TenantMemberMapper memberMapper;
    private final TenantRoleMapper roleMapper;
    private final TenantMemberRoleMapper memberRoleMapper;
    private final MemberAuthorityMapper authorityMapper;
    private final AccessTokenService accessTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(IamUserMapper userMapper,
                           IamUserIdentityMapper identityMapper,
                           IamRefreshTokenMapper refreshTokenMapper,
                           TenantMemberMapper memberMapper,
                           TenantRoleMapper roleMapper,
                           TenantMemberRoleMapper memberRoleMapper,
                           MemberAuthorityMapper authorityMapper,
                           AccessTokenService accessTokenService) {
        this.userMapper = userMapper;
        this.identityMapper = identityMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.memberMapper = memberMapper;
        this.roleMapper = roleMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.authorityMapper = authorityMapper;
        this.accessTokenService = accessTokenService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse register(RegisterRequest request, String ip) {
        validateRegister(request);
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        if (findIdentity(username) != null) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "用户名已存在");
        }
        Date now = new Date();
        IamUser user = new IamUser();
        user.setUsername(username);
        user.setNickname(request.getNickname().trim());
        user.setGender(0);
        user.setStatus("ACTIVE");
        user.setLastLoginAt(now);
        user.setLastLoginIp(ip);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setDeleted(0);
        userMapper.insert(user);

        IamUserIdentity identity = new IamUserIdentity();
        identity.setUserId(user.getId());
        identity.setIdentityType("PASSWORD");
        identity.setIdentityValue(username);
        identity.setCredentialHash(passwordEncoder.encode(request.getPassword()));
        identity.setVerified(1);
        identity.setCreatedAt(now);
        identity.setUpdatedAt(now);
        identity.setDeleted(0);
        identityMapper.insert(identity);

        TenantMember member = createMember(user, request.getNickname().trim(), now);
        assignDefaultMemberRole(member.getId(), now);
        return issueTokens(user, member, request.getDeviceId(), request.getClientType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse login(LoginRequest request, String ip) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "用户名和密码不能为空");
        }
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        IamUserIdentity identity = findIdentity(username);
        if (identity == null || !passwordEncoder.matches(request.getPassword(), identity.getCredentialHash())) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
        IamUser user = userMapper.selectById(identity.getUserId());
        requireActiveUser(user);
        TenantMember member = findMember(user.getId());
        requireUsableMember(member);
        user.setLastLoginAt(new Date());
        user.setLastLoginIp(ip);
        userMapper.updateById(user);
        return issueTokens(user, member, request.getDeviceId(), request.getClientType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse refresh(RefreshRequest request) {
        if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "刷新令牌不能为空");
        }
        IamRefreshToken stored = refreshTokenMapper.selectOne(new LambdaQueryWrapper<IamRefreshToken>()
                .eq(IamRefreshToken::getTokenHash, hash(request.getRefreshToken())));
        if (stored == null || stored.getRevokedAt() != null || stored.getExpiresAt().before(new Date())) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "刷新令牌无效或已过期");
        }
        stored.setRevokedAt(new Date());
        refreshTokenMapper.updateById(stored);
        IamUser user = userMapper.selectById(stored.getUserId());
        TenantMember member = memberMapper.selectById(stored.getMemberId());
        requireActiveUser(user);
        requireUsableMember(member);
        return issueTokens(user, member, request.getDeviceId(), request.getClientType());
    }

    @Override
    public void logout(LogoutRequest request) {
        if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
            return;
        }
        IamRefreshToken stored = refreshTokenMapper.selectOne(new LambdaQueryWrapper<IamRefreshToken>()
                .eq(IamRefreshToken::getTokenHash, hash(request.getRefreshToken())));
        AuthPrincipal principal = AuthContext.require();
        if (stored != null && principal.getUserId().equals(stored.getUserId()) && stored.getRevokedAt() == null) {
            stored.setRevokedAt(new Date());
            refreshTokenMapper.updateById(stored);
        }
    }

    @Override
    public UserSessionResponse currentUser() {
        AuthPrincipal principal = AuthContext.require();
        IamUser user = userMapper.selectById(principal.getUserId());
        TenantMember member = memberMapper.selectById(principal.getMemberId());
        requireActiveUser(user);
        requireUsableMember(member);
        return toSession(user, member);
    }

    @Override
    public UserSessionResponse updateProfile(UpdateProfileRequest request) {
        if (request == null || !StringUtils.hasText(request.getDisplayName())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "社区昵称不能为空");
        }
        if (request.getDisplayName().trim().length() > 64 ||
                (request.getBio() != null && request.getBio().length() > 512)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "社区昵称或个人简介长度超限");
        }
        AuthPrincipal principal = AuthContext.require();
        TenantMember member = memberMapper.selectById(principal.getMemberId());
        requireUsableMember(member);
        member.setDisplayName(request.getDisplayName().trim());
        member.setAvatarUrl(request.getAvatarUrl());
        member.setBio(request.getBio());
        member.setUpdatedAt(new Date());
        memberMapper.updateById(member);
        return toSession(userMapper.selectById(principal.getUserId()), member);
    }

    private TenantMember createMember(IamUser user, String displayName, Date now) {
        TenantMember member = new TenantMember();
        member.setTenantId(TenantContext.requireTenantId());
        member.setUserId(user.getId());
        member.setDisplayName(displayName);
        member.setStatus("ACTIVE");
        member.setJoinedAt(now);
        member.setLastActiveAt(now);
        member.setCreatedAt(now);
        member.setUpdatedAt(now);
        member.setDeleted(0);
        memberMapper.insert(member);
        return member;
    }

    private void assignDefaultMemberRole(Long memberId, Date now) {
        TenantRole role = roleMapper.selectOne(new LambdaQueryWrapper<TenantRole>()
                .eq(TenantRole::getRoleCode, "MEMBER"));
        if (role == null) {
            role = new TenantRole();
            role.setTenantId(TenantContext.requireTenantId());
            role.setRoleCode("MEMBER");
            role.setRoleName("普通成员");
            role.setRoleType("MEMBER");
            role.setSystemRole(1);
            role.setCreatedAt(now);
            role.setUpdatedAt(now);
            role.setDeleted(0);
            roleMapper.insert(role);
        }
        TenantMemberRole relation = new TenantMemberRole();
        relation.setTenantId(TenantContext.requireTenantId());
        relation.setMemberId(memberId);
        relation.setRoleId(role.getId());
        relation.setCreatedAt(now);
        memberRoleMapper.insert(relation);
    }

    private TokenResponse issueTokens(IamUser user, TenantMember member, String deviceId, String clientType) {
        AuthPrincipal principal = new AuthPrincipal(user.getId(), member.getId(), TenantContext.requireTenantId());
        String refreshToken = randomToken();
        IamRefreshToken stored = new IamRefreshToken();
        stored.setTenantId(principal.getTenantId());
        stored.setUserId(principal.getUserId());
        stored.setMemberId(principal.getMemberId());
        stored.setTokenHash(hash(refreshToken));
        stored.setDeviceId(deviceId);
        stored.setClientType(normalizeClientType(clientType));
        stored.setExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_MILLIS));
        stored.setCreatedAt(new Date());
        refreshTokenMapper.insert(stored);

        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.create(principal));
        response.setExpiresIn(accessTokenService.getAccessTokenSeconds());
        response.setRefreshToken(refreshToken);
        response.setUser(toSession(user, member));
        return response;
    }

    private UserSessionResponse toSession(IamUser user, TenantMember member) {
        UserSessionResponse response = new UserSessionResponse();
        response.setUserId(user.getId());
        response.setMemberId(member.getId());
        response.setTenantId(TenantContext.requireTenantId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setDisplayName(member.getDisplayName());
        response.setAvatarUrl(member.getAvatarUrl() == null ? user.getAvatarUrl() : member.getAvatarUrl());
        response.setBio(member.getBio());
        response.setMemberStatus(member.getStatus());
        List<String> roles = authorityMapper.selectRoleCodes(member.getId());
        List<String> permissions = authorityMapper.selectPermissionCodes(member.getId());
        if (roles != null) response.setRoles(roles);
        if (permissions != null) response.setPermissions(permissions);
        return response;
    }

    private IamUserIdentity findIdentity(String username) {
        return identityMapper.selectOne(new LambdaQueryWrapper<IamUserIdentity>()
                .eq(IamUserIdentity::getIdentityType, "PASSWORD")
                .eq(IamUserIdentity::getIdentityValue, username));
    }

    private TenantMember findMember(Long userId) {
        return memberMapper.selectOne(new LambdaQueryWrapper<TenantMember>().eq(TenantMember::getUserId, userId));
    }

    private void validateRegister(RegisterRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) ||
                !StringUtils.hasText(request.getPassword()) || !StringUtils.hasText(request.getNickname())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "用户名、密码和昵称不能为空");
        }
        if (!USERNAME_PATTERN.matcher(request.getUsername().trim()).matches()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "用户名须为 4-32 位字母、数字或下划线，并以字母开头");
        }
        int passwordLength = request.getPassword().length();
        if (passwordLength < 8 || passwordLength > 72) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "密码长度须为 8-72 位");
        }
        if (request.getNickname().trim().length() > 64) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "昵称不能超过 64 个字符");
        }
    }

    private void requireActiveUser(IamUser user) {
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "账号当前不可用");
        }
    }

    private void requireUsableMember(TenantMember member) {
        if (member == null || "BANNED".equals(member.getStatus()) || "LEFT".equals(member.getStatus())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前租户成员不可用");
        }
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(digest.length * 2);
            for (byte value : digest) result.append(String.format("%02x", value & 0xff));
            return result.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot hash refresh token", ex);
        }
    }

    private String normalizeClientType(String clientType) {
        return StringUtils.hasText(clientType) ? clientType.trim().toUpperCase(Locale.ROOT) : "H5";
    }
}
