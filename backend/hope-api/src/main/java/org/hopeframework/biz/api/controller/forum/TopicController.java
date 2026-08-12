package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.entity.output.forum.TopicResponse;
import org.hopeframework.biz.api.service.forum.IForumService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户端社区活动")
@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {
    private final IForumService forumService;

    public TopicController(IForumService forumService) {
        this.forumService = forumService;
    }

    @ApiOperation("查询当前进行中的社区话题活动")
    @GetMapping
    public RespBody<List<TopicResponse>> list() {
        return ResultUtil.success(forumService.listTopics());
    }
}
