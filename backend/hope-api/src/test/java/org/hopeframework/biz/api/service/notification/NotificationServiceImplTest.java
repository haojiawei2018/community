package org.hopeframework.biz.api.service.notification;

import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.mapper.notification.NotificationMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.model.notification.CommunityNotification;
import org.hopeframework.biz.api.service.impl.notification.NotificationServiceImpl;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class NotificationServiceImplTest {

    @After
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void shouldNotNotifyMemberAboutOwnPostAction() {
        NotificationMapper mapper = mock(NotificationMapper.class);
        NotificationServiceImpl service = new NotificationServiceImpl(mapper, mock(TenantMemberMapper.class));
        ForumPost post = post(10L, 20L);

        service.notifyPostAuthor(post, 20L, "LIKE", "帖子标题");

        verify(mapper, never()).insert(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void shouldCreateTenantScopedPostNotification() {
        NotificationMapper mapper = mock(NotificationMapper.class);
        NotificationServiceImpl service = new NotificationServiceImpl(mapper, mock(TenantMemberMapper.class));
        TenantContext.set(3L, "community-3");

        service.notifyPostAuthor(post(10L, 20L), 21L, "COMMENT", "写得很好");

        ArgumentCaptor<CommunityNotification> captor = ArgumentCaptor.forClass(CommunityNotification.class);
        verify(mapper).insert(captor.capture());
        CommunityNotification notification = captor.getValue();
        assertEquals(Long.valueOf(3L), notification.getTenantId());
        assertEquals(Long.valueOf(20L), notification.getReceiverMemberId());
        assertEquals(Long.valueOf(21L), notification.getSenderMemberId());
        assertEquals("POST", notification.getBizType());
        assertEquals(Long.valueOf(10L), notification.getBizId());
        assertEquals("COMMENT", notification.getNotificationType());
    }

    private ForumPost post(Long id, Long authorMemberId) {
        ForumPost post = new ForumPost();
        post.setId(id);
        post.setAuthorMemberId(authorMemberId);
        return post;
    }
}
