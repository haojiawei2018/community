package org.hopeframework.biz.api.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.user.MemberCheckIn;

@Mapper
public interface MemberCheckInMapper extends BaseMapper<MemberCheckIn> {
}
