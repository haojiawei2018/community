package org.hopeframework.biz.api.controller.forum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.entity.output.forum.CircleResponse;
import org.hopeframework.biz.api.service.forum.IForumService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户端圈子")
@RestController
@RequestMapping("/api/v1/circles")
public class CircleController {

    private final IForumService forumService;

    public CircleController(IForumService forumService) {
        this.forumService = forumService;
    }

    @ApiOperation("查询当前社区启用的圈子")
    @GetMapping
    public RespBody<List<CircleResponse>> list() {
        return ResultUtil.success(forumService.listCircles());
    }

    @ApiOperation("查询圈子详情")
    @GetMapping("/{circleId}")
    public RespBody<CircleResponse> detail(@PathVariable Long circleId) {
        return ResultUtil.success(forumService.getCircle(circleId));
    }
}
