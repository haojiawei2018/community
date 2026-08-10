package org.hopeframework.biz.api.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.module.user.model.TenantMember;

@Mapper
public interface TenantMemberMapper extends BaseMapper<TenantMember> {
}
