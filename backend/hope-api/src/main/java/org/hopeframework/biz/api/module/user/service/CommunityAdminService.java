package org.hopeframework.biz.api.module.user.service;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.module.user.dto.MemberAdminResponse;
import org.hopeframework.biz.api.module.user.dto.MemberPageRequest;
import org.hopeframework.biz.api.module.user.dto.MemberRolesRequest;
import org.hopeframework.biz.api.module.user.dto.MemberStatusRequest;
import org.hopeframework.biz.api.module.user.dto.RoleResponse;

import java.util.List;

public interface CommunityAdminService {

    PageResult<MemberAdminResponse> pageMembers(MemberPageRequest request);

    MemberAdminResponse updateMemberStatus(Long memberId, MemberStatusRequest request);

    MemberAdminResponse assignMemberRoles(Long memberId, MemberRolesRequest request);

    List<RoleResponse> listRoles();
}
