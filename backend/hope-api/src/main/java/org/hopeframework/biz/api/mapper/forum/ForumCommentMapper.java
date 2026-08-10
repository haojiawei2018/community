package org.hopeframework.biz.api.mapper.forum;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.forum.ForumComment;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
}
