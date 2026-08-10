package org.hopeframework.biz.api.common.security;

import org.hopeframework.biz.api.mapper.auth.MemberAuthorityMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.user.TenantMember;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberSecurityService {

    private final TenantMemberMapper memberMapper;
    private final MemberAuthorityMapper authorityMapper;

    public MemberSecurityService(TenantMemberMapper memberMapper, MemberAuthorityMapper authorityMapper) {
        this.memberMapper = memberMapper;
        this.authorityMapper = authorityMapper;
    }

    public void validate(AuthPrincipal principal) {
        TenantMember member = memberMapper.selectById(principal.getMemberId());
        if (member == null || !principal.getUserId().equals(member.getUserId()) ||
                "BANNED".equals(member.getStatus()) || "LEFT".equals(member.getStatus())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前租户成员不可用");
        }
    }

    public void requirePermission(Long memberId, String permissionCode) {
        List<String> roles = authorityMapper.selectRoleCodes(memberId);
        if (roles != null && roles.contains("OWNER")) {
            return;
        }
        List<String> permissions = authorityMapper.selectPermissionCodes(memberId);
        if (permissions == null || !permissions.contains(permissionCode)) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "无权执行当前操作");
        }
    }
}
