package org.hopeframework.biz.api.service.forum;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.forum.CommentPageRequest;
import org.hopeframework.biz.api.entity.input.forum.CreateCommentRequest;
import org.hopeframework.biz.api.entity.input.forum.CreatePostRequest;
import org.hopeframework.biz.api.entity.input.forum.PostPageRequest;
import org.hopeframework.biz.api.entity.output.forum.CircleResponse;
import org.hopeframework.biz.api.entity.output.forum.CommentResponse;
import org.hopeframework.biz.api.entity.output.forum.LikeResponse;
import org.hopeframework.biz.api.entity.output.forum.PostResponse;

import java.util.List;

public interface IForumService {
    List<CircleResponse> listCircles();
    CircleResponse getCircle(Long circleId);
    PageResult<PostResponse> pagePosts(PostPageRequest request);
    PostResponse getPost(Long postId);
    PostResponse createPost(CreatePostRequest request);
    PageResult<CommentResponse> pageComments(Long postId, CommentPageRequest request);
    CommentResponse createComment(Long postId, CreateCommentRequest request);
    LikeResponse likePost(Long postId);
    LikeResponse unlikePost(Long postId);
}
