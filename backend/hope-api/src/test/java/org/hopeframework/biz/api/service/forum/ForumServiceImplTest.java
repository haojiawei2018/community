package org.hopeframework.biz.api.service.forum;

import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.output.forum.LikeResponse;
import org.hopeframework.biz.api.mapper.forum.ForumCircleMapper;
import org.hopeframework.biz.api.mapper.forum.ForumCommentMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMapper;
import org.hopeframework.biz.api.mapper.forum.ForumReactionMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.model.forum.ForumReaction;
import org.hopeframework.biz.api.service.impl.forum.ForumServiceImpl;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ForumServiceImplTest {

    @After
    public void tearDown() {
        AuthContext.clear();
        TenantContext.clear();
    }

    @Test
    public void shouldKeepRepeatedPostLikeIdempotent() {
        ForumPostMapper postMapper = mock(ForumPostMapper.class);
        ForumReactionMapper reactionMapper = mock(ForumReactionMapper.class);
        ForumPost post = new ForumPost();
        post.setId(10L);
        post.setStatus("PUBLISHED");
        post.setLikeCount(7L);
        when(postMapper.selectById(10L)).thenReturn(post);
        when(reactionMapper.selectOne(any())).thenReturn(new ForumReaction());
        ForumServiceImpl service = new ForumServiceImpl(
                mock(ForumCircleMapper.class),
                postMapper,
                mock(ForumCommentMapper.class),
                reactionMapper,
                mock(TenantMemberMapper.class));
        TenantContext.set(1L, "default");
        AuthContext.set(new AuthPrincipal(1L, 2L, 1L));

        LikeResponse response = service.likePost(10L);

        assertTrue(response.getLiked());
        assertEquals(Long.valueOf(7L), response.getLikeCount());
        verify(reactionMapper, never()).insertPostLikeIgnore(1L, 2L, 10L);
        verify(postMapper, never()).incrementLike(1L, 10L);
    }
}
