package org.hopeframework.biz.api.module.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberAuthorityMapper {

    @Select("SELECT DISTINCT r.role_code FROM tenant_role r " +
            "JOIN tenant_member_role mr ON mr.role_id = r.id " +
            "WHERE mr.member_id = #{memberId} AND r.deleted = 0")
    List<String> selectRoleCodes(@Param("memberId") Long memberId);

    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "JOIN tenant_role_permission rp ON rp.permission_id = p.id " +
            "JOIN tenant_member_role mr ON mr.role_id = rp.role_id " +
            "WHERE mr.member_id = #{memberId}")
    List<String> selectPermissionCodes(@Param("memberId") Long memberId);

    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "JOIN tenant_role_permission rp ON rp.permission_id = p.id " +
            "WHERE rp.role_id = #{roleId}")
    List<String> selectPermissionCodesByRoleId(@Param("roleId") Long roleId);
}
