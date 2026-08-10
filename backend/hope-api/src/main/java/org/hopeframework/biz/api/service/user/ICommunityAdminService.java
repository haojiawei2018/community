package org.hopeframework.biz.api.service.user;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.output.user.MemberAdminResponse;
import org.hopeframework.biz.api.entity.input.user.MemberPageRequest;
import org.hopeframework.biz.api.entity.input.user.MemberRolesRequest;
import org.hopeframework.biz.api.entity.input.user.MemberStatusRequest;
import org.hopeframework.biz.api.entity.output.user.RoleResponse;

import java.util.List;

public interface ICommunityAdminService {

    PageResult<MemberAdminResponse> pageMembers(MemberPageRequest request);

    MemberAdminResponse updateMemberStatus(Long memberId, MemberStatusRequest request);

    MemberAdminResponse assignMemberRoles(Long memberId, MemberRolesRequest request);

    List<RoleResponse> listRoles();
}
