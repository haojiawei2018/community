package org.hopeframework.biz.api.module.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.module.auth.model.IamUserIdentity;

@Mapper
public interface IamUserIdentityMapper extends BaseMapper<IamUserIdentity> {
}
