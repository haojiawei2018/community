package org.hopeframework.biz.api.mapper.forum;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.hopeframework.biz.api.model.forum.ForumPost;

@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

    @Update("UPDATE forum_post SET view_count = view_count + 1 WHERE tenant_id = #{tenantId} AND id = #{postId} AND deleted = 0")
    int incrementView(@Param("tenantId") Long tenantId, @Param("postId") Long postId);

    @Update("UPDATE forum_post SET like_count = like_count + 1 WHERE tenant_id = #{tenantId} AND id = #{postId} AND deleted = 0")
    int incrementLike(@Param("tenantId") Long tenantId, @Param("postId") Long postId);

    @Update("UPDATE forum_post SET like_count = GREATEST(like_count - 1, 0) WHERE tenant_id = #{tenantId} AND id = #{postId} AND deleted = 0")
    int decrementLike(@Param("tenantId") Long tenantId, @Param("postId") Long postId);

    @Update("UPDATE forum_post SET comment_count = comment_count + 1 WHERE tenant_id = #{tenantId} AND id = #{postId} AND deleted = 0")
    int incrementComment(@Param("tenantId") Long tenantId, @Param("postId") Long postId);
}
