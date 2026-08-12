package org.hopeframework.biz.api.mapper.forum;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.forum.UserFollow;

@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
}
