package org.hopeframework.biz.api.service.impl.forum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.forum.CommentPageRequest;
import org.hopeframework.biz.api.entity.input.forum.CreateCommentRequest;
import org.hopeframework.biz.api.entity.input.forum.CreatePostRequest;
import org.hopeframework.biz.api.entity.input.forum.PostPageRequest;
import org.hopeframework.biz.api.entity.output.forum.CircleResponse;
import org.hopeframework.biz.api.entity.output.forum.CommentResponse;
import org.hopeframework.biz.api.entity.output.forum.LikeResponse;
import org.hopeframework.biz.api.entity.output.forum.PostResponse;
import org.hopeframework.biz.api.entity.output.forum.TopicResponse;
import org.hopeframework.biz.api.entity.output.user.UserCommunitySummaryResponse;
import org.hopeframework.biz.api.entity.output.user.MemberProfileResponse;
import org.hopeframework.biz.api.mapper.forum.ForumCircleMapper;
import org.hopeframework.biz.api.mapper.forum.ForumCommentMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMapper;
import org.hopeframework.biz.api.mapper.forum.ForumPostMediaMapper;
import org.hopeframework.biz.api.mapper.forum.ForumReactionMapper;
import org.hopeframework.biz.api.mapper.forum.ForumTopicMapper;
import org.hopeframework.biz.api.mapper.forum.UserFollowMapper;
import org.hopeframework.biz.api.mapper.user.TenantMemberMapper;
import org.hopeframework.biz.api.model.forum.ForumCircle;
import org.hopeframework.biz.api.model.forum.ForumComment;
import org.hopeframework.biz.api.model.forum.ForumPost;
import org.hopeframework.biz.api.model.forum.ForumPostMedia;
import org.hopeframework.biz.api.model.forum.ForumReaction;
import org.hopeframework.biz.api.model.forum.ForumTopic;
import org.hopeframework.biz.api.model.forum.UserFollow;
import org.hopeframework.biz.api.model.user.TenantMember;
import org.hopeframework.biz.api.service.forum.IForumService;
import org.hopeframework.biz.api.service.notification.INotificationService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements IForumService {

    private final ForumCircleMapper circleMapper;
    private final ForumPostMapper postMapper;
    private final ForumPostMediaMapper postMediaMapper;
    private final ForumTopicMapper topicMapper;
    private final UserFollowMapper followMapper;
    private final ForumCommentMapper commentMapper;
    private final ForumReactionMapper reactionMapper;
    private final TenantMemberMapper memberMapper;
    private final INotificationService notificationService;

    public ForumServiceImpl(ForumCircleMapper circleMapper,
                            ForumPostMapper postMapper,
                            ForumPostMediaMapper postMediaMapper,
                            ForumTopicMapper topicMapper,
                            UserFollowMapper followMapper,
                            ForumCommentMapper commentMapper,
                            ForumReactionMapper reactionMapper,
                            TenantMemberMapper memberMapper,
                            INotificationService notificationService) {
        this.circleMapper = circleMapper;
        this.postMapper = postMapper;
        this.postMediaMapper = postMediaMapper;
        this.topicMapper = topicMapper;
        this.followMapper = followMapper;
        this.commentMapper = commentMapper;
        this.reactionMapper = reactionMapper;
        this.memberMapper = memberMapper;
        this.notificationService = notificationService;
    }

    @Override
    public List<CircleResponse> listCircles() {
        return circleMapper.selectList(new LambdaQueryWrapper<ForumCircle>()
                        .eq(ForumCircle::getStatus, "ACTIVE")
                        .orderByAsc(ForumCircle::getSortOrder)
                        .orderByDesc(ForumCircle::getId))
                .stream().map(this::toCircleResponse).collect(Collectors.toList());
    }

    @Override
    public CircleResponse getCircle(Long circleId) {
        return toCircleResponse(requireCircle(circleId));
    }

    @Override
    public PageResult<PostResponse> pagePosts(PostPageRequest request) {
        PostPageRequest query = request == null ? new PostPageRequest() : request;
        int page = query.getPage() == null ? 1 : Math.max(query.getPage(), 1);
        int pageSize = query.getPageSize() == null ? 10 : Math.min(Math.max(query.getPageSize(), 1), 50);
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<ForumPost>()
                .eq(ForumPost::getStatus, "PUBLISHED")
                .eq(ForumPost::getVisibility, "PUBLIC")
                .eq(query.getCircleId() != null, ForumPost::getCircleId, query.getCircleId())
                .and(StringUtils.hasText(query.getKeyword()), value -> value
                        .like(ForumPost::getTitle, query.getKeyword().trim())
                        .or().like(ForumPost::getContent, query.getKeyword().trim()));
        AuthPrincipal feedPrincipal = AuthContext.current();
        if (feedPrincipal != null) {
            wrapper.notInSql(ForumPost::getAuthorMemberId,
                    "SELECT blocked_member_id FROM user_block WHERE tenant_id = " + TenantContext.requireTenantId()
                            + " AND member_id = " + feedPrincipal.getMemberId());
        }
        if (Boolean.TRUE.equals(query.getFollowing())) {
            AuthPrincipal principal = AuthContext.current();
            if (principal == null) {
                throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "登录后才能查看关注动态");
            }
            List<Long> followedIds = followMapper.selectList(new LambdaQueryWrapper<UserFollow>()
                            .eq(UserFollow::getFollowerMemberId, principal.getMemberId()))
                    .stream().map(UserFollow::getFollowedMemberId).collect(Collectors.toList());
            if (followedIds.isEmpty()) {
                return new PageResult<>(Collections.emptyList(), 0L, pageSize, page);
            }
            wrapper.in(ForumPost::getAuthorMemberId, followedIds);
        }
        wrapper.orderByDesc(ForumPost::getIsTop);
        String sort = normalizeOrDefault(query.getSort(), "RECOMMENDED");
        if ("RECOMMENDED".equals(sort)) {
            wrapper.orderByDesc(ForumPost::getIsFeatured)
                    .orderByDesc(ForumPost::getLikeCount)
                    .orderByDesc(ForumPost::getCommentCount);
        } else if ("HOT".equals(sort)) {
            wrapper.orderByDesc(ForumPost::getLikeCount)
                    .orderByDesc(ForumPost::getCommentCount)
                    .orderByDesc(ForumPost::getViewCount);
        }
        wrapper.orderByDesc(ForumPost::getPublishedAt)
                .orderByDesc(ForumPost::getId);
        Page<ForumPost> result = postMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<PostResponse> records = result.getRecords().stream()
                .map(this::toPostResponse).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    public List<TopicResponse> listTopics() {
        Date now = new Date();
        return topicMapper.selectList(new LambdaQueryWrapper<ForumTopic>()
                        .eq(ForumTopic::getStatus, "ACTIVE")
                        .and(value -> value.isNull(ForumTopic::getStartAt).or().le(ForumTopic::getStartAt, now))
                        .and(value -> value.isNull(ForumTopic::getEndAt).or().ge(ForumTopic::getEndAt, now))
                        .orderByAsc(ForumTopic::getSortOrder)
                        .orderByDesc(ForumTopic::getId))
                .stream().map(this::toTopicResponse).collect(Collectors.toList());
    }

    private TopicResponse toTopicResponse(ForumTopic topic) {
        TopicResponse response = new TopicResponse();
        response.setId(topic.getId());
        response.setCircleId(topic.getCircleId());
        ForumCircle circle = topic.getCircleId() == null ? null : circleMapper.selectById(topic.getCircleId());
        response.setCircleName(circle == null ? null : circle.getCircleName());
        response.setTopicName(topic.getTopicName());
        response.setDescription(topic.getDescription());
        response.setCoverUrl(topic.getCoverUrl());
        response.setStatus(topic.getStatus());
        response.setStartAt(topic.getStartAt());
        response.setEndAt(topic.getEndAt());
        response.setSortOrder(topic.getSortOrder());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostResponse getPost(Long postId) {
        ForumPost post = requirePublishedPost(postId);
        postMapper.incrementView(TenantContext.requireTenantId(), postId);
        post.setViewCount(safe(post.getViewCount()) + 1L);
        return toPostResponse(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostResponse createPost(CreatePostRequest request) {
        AuthPrincipal principal = AuthContext.require();
        if (request == null || !StringUtils.hasText(request.getTitle()) || !StringUtils.hasText(request.getContent())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子标题和正文不能为空");
        }
        String title = request.getTitle().trim();
        String content = request.getContent().trim();
        if (title.length() > 255 || content.length() > 20000) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子标题或正文超过长度限制");
        }
        ForumCircle circle = request.getCircleId() == null ? firstActiveCircle() : requireCircle(request.getCircleId());
        Date now = new Date();
        boolean draft = Boolean.TRUE.equals(request.getSaveAsDraft());
        ForumPost post = new ForumPost();
        post.setTenantId(TenantContext.requireTenantId());
        post.setCircleId(circle.getId());
        post.setAuthorMemberId(principal.getMemberId());
        post.setPostType(normalizeOrDefault(request.getPostType(), "ARTICLE"));
        post.setTitle(title);
        post.setSummary(content.length() > 200 ? content.substring(0, 200) : content);
        post.setContent(content);
        post.setStatus(draft ? "DRAFT" : "PUBLISHED");
        post.setVisibility(normalizeOrDefault(request.getVisibility(), "PUBLIC"));
        post.setIsTop(0);
        post.setIsFeatured(0);
        post.setAllowComment(1);
        post.setViewCount(0L);
        post.setLikeCount(0L);
        post.setCommentCount(0L);
        post.setFavoriteCount(0L);
        post.setPublishedAt(draft ? null : now);
        post.setCreatedBy(principal.getMemberId());
        post.setCreatedAt(now);
        post.setUpdatedBy(principal.getMemberId());
        post.setUpdatedAt(now);
        post.setDeleted(0);
        postMapper.insert(post);
        savePostImages(post.getId(), request.getImages(), now);
        if (!draft) {
            circleMapper.incrementPost(TenantContext.requireTenantId(), circle.getId());
        }
        return toPostResponse(post);
    }

    @Override
    public PageResult<CommentResponse> pageComments(Long postId, CommentPageRequest request) {
        requirePublishedPost(postId);
        CommentPageRequest query = request == null ? new CommentPageRequest() : request;
        int page = query.getPage() == null ? 1 : Math.max(query.getPage(), 1);
        int pageSize = query.getPageSize() == null ? 10 : Math.min(Math.max(query.getPageSize(), 1), 50);
        Page<ForumComment> result = commentMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<ForumComment>()
                        .eq(ForumComment::getPostId, postId)
                        .eq(ForumComment::getStatus, "PUBLISHED")
                        .orderByAsc(ForumComment::getCreatedAt)
                        .orderByAsc(ForumComment::getId));
        List<CommentResponse> records = result.getRecords().stream()
                .map(this::toCommentResponse).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentResponse createComment(Long postId, CreateCommentRequest request) {
        AuthPrincipal principal = AuthContext.require();
        ForumPost post = requirePublishedPost(postId);
        if (post.getAllowComment() == null || post.getAllowComment() != 1) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "该帖子已关闭评论");
        }
        if (request == null || !StringUtils.hasText(request.getContent())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "评论内容不能为空");
        }
        String content = request.getContent().trim();
        if (content.length() > 2000) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "评论内容不能超过 2000 字");
        }
        Date now = new Date();
        ForumComment comment = new ForumComment();
        comment.setTenantId(TenantContext.requireTenantId());
        comment.setPostId(postId);
        comment.setAuthorMemberId(principal.getMemberId());
        comment.setContent(content);
        comment.setStatus("PUBLISHED");
        comment.setLikeCount(0L);
        comment.setReplyCount(0L);
        comment.setCreatedBy(principal.getMemberId());
        comment.setCreatedAt(now);
        comment.setUpdatedBy(principal.getMemberId());
        comment.setUpdatedAt(now);
        comment.setDeleted(0);
        commentMapper.insert(comment);
        postMapper.incrementComment(TenantContext.requireTenantId(), postId);
        notificationService.notifyPostAuthor(post, principal.getMemberId(), "COMMENT", content);
        return toCommentResponse(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeResponse likePost(Long postId) {
        AuthPrincipal principal = AuthContext.require();
        ForumPost post = requirePublishedPost(postId);
        ForumReaction existing = findPostLike(principal.getMemberId(), postId);
        if (existing != null) {
            return new LikeResponse(true, safe(post.getLikeCount()));
        }
        int inserted = reactionMapper.insertPostLikeIgnore(principal.getMemberId(), postId);
        if (inserted > 0) {
            postMapper.incrementLike(TenantContext.requireTenantId(), postId);
            notificationService.notifyPostAuthor(post, principal.getMemberId(), "LIKE", post.getTitle());
            return new LikeResponse(true, safe(post.getLikeCount()) + 1L);
        }
        return new LikeResponse(true, currentLikeCount(postId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeResponse unlikePost(Long postId) {
        AuthPrincipal principal = AuthContext.require();
        ForumPost post = requirePublishedPost(postId);
        int deleted = reactionMapper.delete(new LambdaQueryWrapper<ForumReaction>()
                .eq(ForumReaction::getMemberId, principal.getMemberId())
                .eq(ForumReaction::getTargetType, "POST")
                .eq(ForumReaction::getTargetId, postId)
                .eq(ForumReaction::getReactionType, "LIKE"));
        if (deleted > 0) {
            postMapper.decrementLike(TenantContext.requireTenantId(), postId);
            return new LikeResponse(false, Math.max(safe(post.getLikeCount()) - 1L, 0L));
        }
        return new LikeResponse(false, safe(post.getLikeCount()));
    }

    @Override
    public UserCommunitySummaryResponse currentUserSummary() {
        Long memberId = AuthContext.require().getMemberId();
        Integer commentCount = commentMapper.selectCount(new LambdaQueryWrapper<ForumComment>()
                .eq(ForumComment::getAuthorMemberId, memberId)
                .eq(ForumComment::getStatus, "PUBLISHED"));
        UserCommunitySummaryResponse response = new UserCommunitySummaryResponse();
        response.setPostCount(postMapper.countPublishedByAuthor(memberId));
        response.setReceivedLikeCount(postMapper.sumReceivedLikesByAuthor(memberId));
        response.setCommentCount(commentCount == null ? 0L : commentCount.longValue());
        return response;
    }

    @Override
    public PageResult<PostResponse> pageCurrentUserPosts(PostPageRequest request) {
        AuthPrincipal principal = AuthContext.require();
        PostPageRequest query = request == null ? new PostPageRequest() : request;
        int page = query.getPage() == null ? 1 : Math.max(query.getPage(), 1);
        int pageSize = query.getPageSize() == null ? 10 : Math.min(Math.max(query.getPageSize(), 1), 50);
        Page<ForumPost> result = postMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<ForumPost>()
                        .eq(ForumPost::getAuthorMemberId, principal.getMemberId())
                        .eq(ForumPost::getStatus, "PUBLISHED")
                        .orderByDesc(ForumPost::getPublishedAt)
                        .orderByDesc(ForumPost::getId));
        List<PostResponse> records = result.getRecords().stream()
                .map(this::toPostResponse).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCurrentUserPost(Long postId) {
        if (postId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子 ID 不能为空");
        }
        AuthPrincipal principal = AuthContext.require();
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "帖子不存在或已删除");
        }
        if (!Objects.equals(post.getAuthorMemberId(), principal.getMemberId())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "只能删除自己发布的帖子");
        }
        postMapper.deleteById(post.getId());
        if ("PUBLISHED".equals(post.getStatus())) {
            circleMapper.decrementPost(TenantContext.requireTenantId(), post.getCircleId());
        }
    }

    @Override
    public MemberProfileResponse getMemberProfile(Long memberId) {
        if (memberId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "成员 ID 不能为空");
        }
        TenantMember member = memberMapper.selectById(memberId);
        if (member == null || !"ACTIVE".equals(member.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "社区成员不存在或不可访问");
        }
        MemberProfileResponse response = new MemberProfileResponse();
        response.setMemberId(member.getId());
        response.setDisplayName(member.getDisplayName());
        response.setAvatarUrl(member.getAvatarUrl());
        response.setBio(member.getBio());
        response.setJoinedAt(member.getJoinedAt());
        response.setPostCount(postMapper.countPublishedByAuthor(member.getId()));
        response.setReceivedLikeCount(postMapper.sumReceivedLikesByAuthor(member.getId()));
        return response;
    }

    @Override
    public PageResult<PostResponse> pageMemberPosts(Long memberId, PostPageRequest request) {
        getMemberProfile(memberId);
        PostPageRequest query = request == null ? new PostPageRequest() : request;
        int page = query.getPage() == null ? 1 : Math.max(query.getPage(), 1);
        int pageSize = query.getPageSize() == null ? 10 : Math.min(Math.max(query.getPageSize(), 1), 50);
        Page<ForumPost> result = postMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<ForumPost>()
                        .eq(ForumPost::getAuthorMemberId, memberId)
                        .eq(ForumPost::getStatus, "PUBLISHED")
                        .eq(ForumPost::getVisibility, "PUBLIC")
                        .orderByDesc(ForumPost::getPublishedAt)
                        .orderByDesc(ForumPost::getId));
        List<PostResponse> records = result.getRecords().stream()
                .map(this::toPostResponse).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    private ForumCircle firstActiveCircle() {
        ForumCircle circle = circleMapper.selectOne(new LambdaQueryWrapper<ForumCircle>()
                .eq(ForumCircle::getStatus, "ACTIVE").orderByAsc(ForumCircle::getSortOrder).last("LIMIT 1"));
        if (circle == null) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "当前社区还没有可发帖的圈子");
        }
        return circle;
    }

    private ForumCircle requireCircle(Long circleId) {
        if (circleId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "圈子 ID 不能为空");
        }
        ForumCircle circle = circleMapper.selectById(circleId);
        if (circle == null || !"ACTIVE".equals(circle.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "圈子不存在或已停用");
        }
        return circle;
    }

    private ForumPost requirePublishedPost(Long postId) {
        if (postId == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子 ID 不能为空");
        }
        ForumPost post = postMapper.selectById(postId);
        if (post == null || !"PUBLISHED".equals(post.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "帖子不存在或不可访问");
        }
        return post;
    }

    private ForumReaction findPostLike(Long memberId, Long postId) {
        return reactionMapper.selectOne(new LambdaQueryWrapper<ForumReaction>()
                .eq(ForumReaction::getMemberId, memberId)
                .eq(ForumReaction::getTargetType, "POST")
                .eq(ForumReaction::getTargetId, postId)
                .eq(ForumReaction::getReactionType, "LIKE"));
    }

    private long currentLikeCount(Long postId) {
        ForumPost current = postMapper.selectById(postId);
        return current == null ? 0L : safe(current.getLikeCount());
    }

    private CircleResponse toCircleResponse(ForumCircle circle) {
        CircleResponse response = new CircleResponse();
        response.setId(circle.getId());
        response.setCircleCode(circle.getCircleCode());
        response.setCircleName(circle.getCircleName());
        response.setIconUrl(circle.getIconUrl());
        response.setCoverUrl(circle.getCoverUrl());
        response.setDescription(circle.getDescription());
        response.setJoinMode(circle.getJoinMode());
        response.setStatus(circle.getStatus());
        response.setSortOrder(circle.getSortOrder());
        response.setMemberCount(safe(circle.getMemberCount()));
        response.setPostCount(safe(circle.getPostCount()));
        return response;
    }

    private PostResponse toPostResponse(ForumPost post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setCircleId(post.getCircleId());
        ForumCircle circle = circleMapper.selectById(post.getCircleId());
        response.setCircleName(circle == null ? null : circle.getCircleName());
        response.setAuthorMemberId(post.getAuthorMemberId());
        TenantMember author = memberMapper.selectById(post.getAuthorMemberId());
        if (author != null) {
            response.setUserId(author.getUserId());
            response.setUsername(author.getDisplayName());
            response.setAvatar(author.getAvatarUrl());
        }
        response.setPostType(post.getPostType());
        response.setTitle(post.getTitle());
        response.setSummary(post.getSummary());
        response.setContent(post.getContent());
        response.setStatus(post.getStatus());
        response.setVisibility(post.getVisibility());
        response.setViewCount(safe(post.getViewCount()));
        response.setLikeCount(safe(post.getLikeCount()));
        response.setCommentCount(safe(post.getCommentCount()));
        AuthPrincipal principal = AuthContext.current();
        response.setIsLiked(principal != null && findPostLike(principal.getMemberId(), post.getId()) != null);
        response.setCreateTime(post.getPublishedAt() == null ? post.getCreatedAt() : post.getPublishedAt());
        response.setImages(postMediaMapper.selectList(new LambdaQueryWrapper<ForumPostMedia>()
                        .eq(ForumPostMedia::getPostId, post.getId())
                        .eq(ForumPostMedia::getMediaType, "IMAGE")
                        .orderByAsc(ForumPostMedia::getSortOrder)
                        .orderByAsc(ForumPostMedia::getId))
                .stream().map(ForumPostMedia::getMediaUrl).collect(Collectors.toList()));
        return response;
    }

    private void savePostImages(Long postId, List<String> images, Date now) {
        if (images == null || images.isEmpty()) return;
        if (images.size() > 9) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子图片不能超过 9 张");
        }
        int sort = 0;
        for (String image : images) {
            if (!StringUtils.hasText(image) || image.trim().length() > 1024) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "帖子图片地址不合法");
            }
            ForumPostMedia media = new ForumPostMedia();
            media.setTenantId(TenantContext.requireTenantId());
            media.setPostId(postId);
            media.setMediaType("IMAGE");
            media.setMediaUrl(image.trim());
            media.setSortOrder(sort++);
            media.setCreatedAt(now);
            postMediaMapper.insert(media);
        }
    }

    private CommentResponse toCommentResponse(ForumComment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setAuthorMemberId(comment.getAuthorMemberId());
        TenantMember author = memberMapper.selectById(comment.getAuthorMemberId());
        if (author != null) {
            response.setUserId(author.getUserId());
            response.setUsername(author.getDisplayName());
            response.setAvatar(author.getAvatarUrl());
        }
        response.setContent(comment.getContent());
        response.setLikeCount(safe(comment.getLikeCount()));
        response.setCreateTime(comment.getCreatedAt());
        return response;
    }

    private String normalizeOrDefault(String value, String fallback) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase(Locale.ROOT) : fallback;
    }

    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
