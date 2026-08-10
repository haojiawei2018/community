package org.hopeframework.biz.api.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.auth.IamUser;

@Mapper
public interface IamUserMapper extends BaseMapper<IamUser> {
}
