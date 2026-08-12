package org.hopeframework.biz.api.mapper.forum;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.hopeframework.biz.api.model.forum.ForumReaction;

@Mapper
public interface ForumReactionMapper extends BaseMapper<ForumReaction> {
    @Insert("INSERT IGNORE INTO forum_reaction "
            + "(member_id, target_type, target_id, reaction_type, created_at) "
            + "VALUES (#{memberId}, 'POST', #{postId}, 'LIKE', NOW())")
    int insertPostLikeIgnore(@Param("memberId") Long memberId,
                             @Param("postId") Long postId);
}
