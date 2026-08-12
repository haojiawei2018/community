package org.hopeframework.biz.api.service.impl.forum;

import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.mapper.forum.ForumCircleMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMapper;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.service.forum.ICommunityPostAdminService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityPostAdminServiceImpl implements ICommunityPostAdminService {
    private final ForumPostMapper postMapper;
    private final ForumCircleMapper circleMapper;

    public CommunityPostAdminServiceImpl(ForumPostMapper postMapper, ForumCircleMapper circleMapper) {
        this.postMapper = postMapper;
        this.circleMapper = circleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId) {
        if (postId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子 ID 不能为空");
        }
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "帖子不存在或已删除");
        }
        postMapper.deleteById(post.getId());
        if ("PUBLISHED".equals(post.getStatus())) {
            circleMapper.decrementPost(TenantContext.requireTenantId(), post.getCircleId());
        }
    }
}
