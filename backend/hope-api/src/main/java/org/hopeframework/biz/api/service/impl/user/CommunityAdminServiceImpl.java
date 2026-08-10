package org.hopeframework.biz.api.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.mapper.auth.IamUserMapper;
import org.hopeframework.biz.api.mapper.auth.MemberAuthorityMapper;
import org.hopeframework.biz.api.mapper.auth.TenantMemberRoleMapper;
import org.hopeframework.biz.api.mapper.auth.TenantRoleMapper;
import org.hopeframework.biz.api.model.auth.IamUser;
import org.hopeframework.biz.api.model.auth.TenantMemberRole;
import org.hopeframework.biz.api.model.auth.TenantRole;
import org.hopeframework.biz.api.entity.output.user.MemberAdminResponse;
import org.hopeframework.biz.api.entity.input.user.MemberPageRequest;
import org.hopeframework.biz.api.entity.input.user.MemberRolesRequest;
import org.hopeframework.biz.api.entity.input.user.MemberStatusRequest;
import org.hopeframework.biz.api.entity.output.user.RoleResponse;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.user.TenantMember;
import org.hopeframework.biz.api.service.user.CommunityAdminService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommunityAdminServiceImpl implements CommunityAdminService {

    private static final Set<String> MEMBER_STATUSES = new HashSet<>(
            Arrays.asList("ACTIVE", "MUTED", "BANNED", "LEFT"));

    private final TenantMemberMapper memberMapper;
    private final IamUserMapper userMapper;
    private final TenantRoleMapper roleMapper;
    private final TenantMemberRoleMapper memberRoleMapper;
    private final MemberAuthorityMapper authorityMapper;

    public CommunityAdminServiceImpl(TenantMemberMapper memberMapper,
                                     IamUserMapper userMapper,
                                     TenantRoleMapper roleMapper,
                                     TenantMemberRoleMapper memberRoleMapper,
                                     MemberAuthorityMapper authorityMapper) {
        this.memberMapper = memberMapper;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.authorityMapper = authorityMapper;
    }

    @Override
    public PageResult<MemberAdminResponse> pageMembers(MemberPageRequest request) {
        MemberPageRequest query = request == null ? new MemberPageRequest() : request;
        int pageNumber = query.getPage() == null ? 1 : Math.max(query.getPage(), 1);
        int pageSize = query.getPageSize() == null ? 10 : Math.min(Math.max(query.getPageSize(), 1), 100);
        String status = null;
        if (StringUtils.hasText(query.getStatus())) {
            status = normalizeStatus(query.getStatus());
            if (!MEMBER_STATUSES.contains(status)) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不支持的成员状态");
            }
        }
        LambdaQueryWrapper<TenantMember> wrapper = new LambdaQueryWrapper<TenantMember>()
                .like(StringUtils.hasText(query.getKeyword()), TenantMember::getDisplayName, query.getKeyword())
                .eq(status != null, TenantMember::getStatus, status)
                .orderByDesc(TenantMember::getId);
        Page<TenantMember> result = memberMapper.selectPage(new Page<>(pageNumber, pageSize), wrapper);
        List<MemberAdminResponse> records = result.getRecords().stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberAdminResponse updateMemberStatus(Long memberId, MemberStatusRequest request) {
        TenantMember member = requireMember(memberId);
        if (request == null || !StringUtils.hasText(request.getStatus())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "成员状态不能为空");
        }
        String status = normalizeStatus(request.getStatus());
        if (!MEMBER_STATUSES.contains(status)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不支持的成员状态");
        }
        AuthPrincipal principal = AuthContext.require();
        if (principal.getMemberId().equals(memberId) && ("BANNED".equals(status) || "LEFT".equals(status))) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不能封禁或移出当前登录成员");
        }
        if ("MUTED".equals(status) && (request.getMuteUntil() == null || !request.getMuteUntil().after(new Date()))) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "禁言截止时间必须晚于当前时间");
        }
        member.setStatus(status);
        member.setMuteUntil("MUTED".equals(status) ? request.getMuteUntil() : null);
        member.setUpdatedBy(principal.getMemberId());
        member.setUpdatedAt(new Date());
        memberMapper.updateById(member);
        return toMemberResponse(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberAdminResponse assignMemberRoles(Long memberId, MemberRolesRequest request) {
        TenantMember member = requireMember(memberId);
        if (request == null || request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "成员至少需要一个角色");
        }
        AuthPrincipal principal = AuthContext.require();
        List<String> operatorRoles = authorityMapper.selectRoleCodes(principal.getMemberId());
        boolean operatorIsOwner = operatorRoles != null && operatorRoles.contains("OWNER");
        List<String> currentTargetRoles = authorityMapper.selectRoleCodes(memberId);
        boolean targetIsOwner = currentTargetRoles != null && currentTargetRoles.contains("OWNER");
        List<TenantRole> roles = new ArrayList<>();
        for (Long roleId : new LinkedHashSet<>(request.getRoleIds())) {
            if (roleId == null) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "角色 ID 不能为空");
            }
            TenantRole role = roleMapper.selectById(roleId);
            if (role == null) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "角色不存在或不属于当前社区");
            }
            if ("OWNER".equals(role.getRoleCode()) && !operatorIsOwner) {
                throw new HopeException(HttpStatus.FORBIDDEN.value(), "只有社区所有者可以授予 OWNER 角色");
            }
            roles.add(role);
        }
        boolean keepsOwnerRole = roles.stream().anyMatch(role -> "OWNER".equals(role.getRoleCode()));
        if (targetIsOwner && !operatorIsOwner) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "只有社区所有者可以调整 OWNER 成员的角色");
        }
        if (principal.getMemberId().equals(memberId) && targetIsOwner && !keepsOwnerRole) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "不能移除当前登录成员自己的 OWNER 角色");
        }
        memberRoleMapper.delete(new LambdaQueryWrapper<TenantMemberRole>()
                .eq(TenantMemberRole::getMemberId, memberId));
        Date now = new Date();
        for (TenantRole role : roles) {
            TenantMemberRole relation = new TenantMemberRole();
            relation.setTenantId(TenantContext.requireTenantId());
            relation.setMemberId(memberId);
            relation.setRoleId(role.getId());
            relation.setCreatedBy(principal.getMemberId());
            relation.setCreatedAt(now);
            memberRoleMapper.insert(relation);
        }
        return toMemberResponse(member);
    }

    @Override
    public List<RoleResponse> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<TenantRole>()
                        .orderByDesc(TenantRole::getSystemRole)
                        .orderByAsc(TenantRole::getId))
                .stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    private TenantMember requireMember(Long memberId) {
        if (memberId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "成员 ID 不能为空");
        }
        TenantMember member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "成员不存在");
        }
        return member;
    }

    private MemberAdminResponse toMemberResponse(TenantMember member) {
        IamUser user = userMapper.selectById(member.getUserId());
        MemberAdminResponse response = new MemberAdminResponse();
        response.setMemberId(member.getId());
        response.setUserId(member.getUserId());
        response.setUsername(user == null ? null : user.getUsername());
        response.setDisplayName(member.getDisplayName());
        response.setAvatarUrl(member.getAvatarUrl());
        response.setStatus(member.getStatus());
        response.setMuteUntil(member.getMuteUntil());
        response.setJoinedAt(member.getJoinedAt());
        List<String> roles = authorityMapper.selectRoleCodes(member.getId());
        response.setRoles(roles == null ? new ArrayList<>() : roles);
        return response;
    }

    private RoleResponse toRoleResponse(TenantRole role) {
        RoleResponse response = new RoleResponse();
        response.setRoleId(role.getId());
        response.setRoleCode(role.getRoleCode());
        response.setRoleName(role.getRoleName());
        response.setRoleType(role.getRoleType());
        response.setSystemRole(role.getSystemRole());
        List<String> permissions = authorityMapper.selectPermissionCodesByRoleId(role.getId());
        response.setPermissions(permissions == null ? new ArrayList<>() : permissions);
        return response;
    }

    private String normalizeStatus(String status) {
        return status == null ? null : status.trim().toUpperCase(Locale.ROOT);
    }
}
