package org.hopeframework.biz.api.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.auth.IamRefreshToken;

@Mapper
public interface IamRefreshTokenMapper extends BaseMapper<IamRefreshToken> {
}
