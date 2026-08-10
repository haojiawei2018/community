package org.hopeframework.biz.api.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.user.TenantMember;

@Mapper
public interface TenantMemberMapper extends BaseMapper<TenantMember> {
}
