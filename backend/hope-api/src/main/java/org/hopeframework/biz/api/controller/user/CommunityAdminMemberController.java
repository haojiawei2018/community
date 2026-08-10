package org.hopeframework.biz.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.RequirePermission;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.output.user.MemberAdminResponse;
import org.hopeframework.biz.api.entity.input.user.MemberPageRequest;
import org.hopeframework.biz.api.entity.input.user.MemberRolesRequest;
import org.hopeframework.biz.api.entity.input.user.MemberStatusRequest;
import org.hopeframework.biz.api.entity.output.user.RoleResponse;
import org.hopeframework.biz.api.service.user.CommunityAdminService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "社区后台成员与角色")
@UserLoginToken
@RestController
@RequestMapping("/api/admin/v1")
public class CommunityAdminMemberController {

    private final CommunityAdminService communityAdminService;

    public CommunityAdminMemberController(CommunityAdminService communityAdminService) {
        this.communityAdminService = communityAdminService;
    }

    @ApiOperation("分页查询社区成员")
    @RequirePermission("member.read")
    @GetMapping("/members")
    public RespBody<PageResult<MemberAdminResponse>> pageMembers(MemberPageRequest request) {
        return ResultUtil.success(communityAdminService.pageMembers(request));
    }

    @ApiOperation("修改社区成员状态")
    @RequirePermission("member.status.write")
    @PutMapping("/members/{memberId}/status")
    public RespBody<MemberAdminResponse> updateMemberStatus(@PathVariable Long memberId,
                                                            @RequestBody MemberStatusRequest request) {
        return ResultUtil.success(communityAdminService.updateMemberStatus(memberId, request));
    }

    @ApiOperation("覆盖设置社区成员角色")
    @RequirePermission("member.role.write")
    @PutMapping("/members/{memberId}/roles")
    public RespBody<MemberAdminResponse> assignMemberRoles(@PathVariable Long memberId,
                                                           @RequestBody MemberRolesRequest request) {
        return ResultUtil.success(communityAdminService.assignMemberRoles(memberId, request));
    }

    @ApiOperation("查询社区角色和权限")
    @RequirePermission("member.role.write")
    @GetMapping("/roles")
    public RespBody<List<RoleResponse>> listRoles() {
        return ResultUtil.success(communityAdminService.listRoles());
    }
}
