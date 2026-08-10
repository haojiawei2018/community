package org.hopeframework.biz.api.mapper.forum;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.hopeframework.biz.api.model.forum.ForumCircle;

@Mapper
public interface ForumCircleMapper extends BaseMapper<ForumCircle> {
    @Update("UPDATE forum_circle SET post_count = post_count + 1 WHERE tenant_id = #{tenantId} AND id = #{circleId} AND deleted = 0")
    int incrementPost(@Param("tenantId") Long tenantId, @Param("circleId") Long circleId);
}
