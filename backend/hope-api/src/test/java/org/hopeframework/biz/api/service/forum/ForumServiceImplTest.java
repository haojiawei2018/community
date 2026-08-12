package org.hopeframework.biz.api.service.forum;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.output.forum.LikeResponse;
import org.hopeframework.biz.api.entity.input.forum.CreatePostRequest;
import org.hopeframework.biz.api.entity.input.forum.PostPageRequest;
import org.hopeframework.biz.api.mapper.forum.ForumCircleMapper;
import org.hopeframework.biz.api.mapper.forum.ForumCommentMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMediaMapper;
import org.hopeframework.biz.api.mapper.forum.ForumReactionMapper;
import org.hopeframework.biz.api.mapper.forum.ForumTopicMapper;
import org.hopeframework.biz.api.mapper.forum.UserFollowMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.model.forum.ForumReaction;
import org.hopeframework.biz.api.model.forum.ForumCircle;
import org.hopeframework.biz.api.model.forum.ForumPostMedia;
import org.hopeframework.biz.api.service.impl.forum.ForumServiceImpl;
import org.hopeframework.biz.api.service.notification.INotificationService;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ForumServiceImplTest {

    @Test
    public void shouldPersistUploadedImagesWhenCreatingPost() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "forumPostMediaTest"),
                ForumPostMedia.class);
        ForumCircleMapper circleMapper = mock(ForumCircleMapper.class);
        ForumPostMapper postMapper = mock(ForumPostMapper.class);
        ForumPostMediaMapper mediaMapper = mock(ForumPostMediaMapper.class);
        ForumCircle circle = new ForumCircle();
        circle.setId(3L);
        circle.setStatus("ACTIVE");
        when(circleMapper.selectById(3L)).thenReturn(circle);
        ForumServiceImpl service = new ForumServiceImpl(
                circleMapper,
                postMapper,
                mediaMapper,
                mock(ForumTopicMapper.class),
                mock(UserFollowMapper.class),
                mock(ForumCommentMapper.class),
                mock(ForumReactionMapper.class),
                mock(TenantMemberMapper.class),
                mock(INotificationService.class));
        TenantContext.set(1L, "default");
        AuthContext.set(new AuthPrincipal(1L, 2L, 1L));
        CreatePostRequest request = new CreatePostRequest();
        request.setCircleId(3L);
        request.setTitle("带图帖子");
        request.setContent("正文");
        request.setImages(Arrays.asList("https://cdn.example.com/1.jpg", "https://cdn.example.com/2.jpg"));

        service.createPost(request);

        ArgumentCaptor<ForumPostMedia> mediaCaptor = ArgumentCaptor.forClass(ForumPostMedia.class);
        verify(mediaMapper, org.mockito.Mockito.times(2)).insert(mediaCaptor.capture());
        assertEquals("https://cdn.example.com/1.jpg", mediaCaptor.getAllValues().get(0).getMediaUrl());
        assertEquals(Integer.valueOf(1), mediaCaptor.getAllValues().get(1).getSortOrder());
    }

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
                mock(ForumPostMediaMapper.class),
                mock(ForumTopicMapper.class),
                mock(UserFollowMapper.class),
                mock(ForumCommentMapper.class),
                reactionMapper,
                mock(TenantMemberMapper.class),
                mock(INotificationService.class));
        TenantContext.set(1L, "default");
        AuthContext.set(new AuthPrincipal(1L, 2L, 1L));

        LikeResponse response = service.likePost(10L);

        assertTrue(response.getLiked());
        assertEquals(Long.valueOf(7L), response.getLikeCount());
        verify(reactionMapper, never()).insertPostLikeIgnore(2L, 10L);
        verify(postMapper, never()).incrementLike(1L, 10L);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void shouldOrderHotFeedByEngagement() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "forumPostTest"),
                ForumPost.class);
        ForumPostMapper postMapper = mock(ForumPostMapper.class);
        when(postMapper.selectPage(any(Page.class), any(Wrapper.class)))
                .thenReturn(new Page<ForumPost>(1, 10));
        ForumServiceImpl service = new ForumServiceImpl(
                mock(ForumCircleMapper.class),
                postMapper,
                mock(ForumPostMediaMapper.class),
                mock(ForumTopicMapper.class),
                mock(UserFollowMapper.class),
                mock(ForumCommentMapper.class),
                mock(ForumReactionMapper.class),
                mock(TenantMemberMapper.class),
                mock(INotificationService.class));
        TenantContext.set(1L, "default");
        PostPageRequest request = new PostPageRequest();
        request.setSort("hot");

        service.pagePosts(request);

        ArgumentCaptor<Wrapper> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(postMapper).selectPage(any(Page.class), wrapperCaptor.capture());
        String sql = wrapperCaptor.getValue().getSqlSegment();
        assertTrue(sql.contains("like_count"));
        assertTrue(sql.contains("comment_count"));
        assertTrue(sql.contains("view_count"));
    }
}
