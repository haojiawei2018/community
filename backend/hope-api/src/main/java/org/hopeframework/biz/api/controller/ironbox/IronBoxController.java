package org.hopeframework.biz.api.controller.ironbox;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.service.ironbox.IronBoxService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** AI 铁盒独立用户、作品与图片上传入口。 */
@Api(tags = "AI铁盒")
@RestController
@RequestMapping("/api/ironbox")
public class IronBoxController {
    private final IronBoxService service;

    public IronBoxController(IronBoxService service) { this.service = service; }

    @PassToken @ApiOperation("注册AI铁盒账号")
    @PostMapping("/auth/register")
    public RespBody<Map<String, Object>> register(@RequestBody Map<String, Object> request) {
        return ResultUtil.success(service.register(request));
    }

    @PassToken @ApiOperation("登录AI铁盒账号")
    @PostMapping("/auth/login")
    public RespBody<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        return ResultUtil.success(service.login(request));
    }

    @UserLoginToken @ApiOperation("当前AI铁盒用户")
    @GetMapping("/auth/me")
    public RespBody<Map<String, Object>> me() { return ResultUtil.success(service.me()); }

    @PassToken @ApiOperation("首页统计")
    @GetMapping("/home/stats")
    public RespBody<Map<String, Object>> stats() { return ResultUtil.success(service.stats()); }

    @ApiOperation("铁盒列表与搜索")
    @GetMapping("/boxes")
    public RespBody<Map<String, Object>> boxes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String keyword) {
        return ResultUtil.success(service.list(page, pageSize, category, sort, keyword, null));
    }

    @ApiOperation("随机获取一只铁盒，不计开盒次数")
    @GetMapping("/boxes/random")
    public RespBody<Map<String, Object>> random() { return ResultUtil.success(service.random()); }

    @ApiOperation("铁盒详情，计浏览次数")
    @GetMapping("/boxes/{id}")
    public RespBody<Map<String, Object>> detail(@PathVariable Long id) {
        return ResultUtil.success(service.detail(id, true));
    }

    @ApiOperation("开盒前预取指定铁盒，不计浏览次数")
    @GetMapping("/boxes/{id}/preview")
    public RespBody<Map<String, Object>> preview(@PathVariable Long id) {
        return ResultUtil.success(service.detail(id, false));
    }

    @PassToken @ApiOperation("完成开盒动画后计开盒次数")
    @PostMapping("/boxes/{id}/open")
    public RespBody<Void> open(@PathVariable Long id) { service.opened(id); return ResultUtil.success(null); }

    @UserLoginToken @ApiOperation("申请COS直传签名")
    @PostMapping("/upload/token")
    public RespBody<Map<String, Object>> uploadToken(@RequestBody Map<String, Object> request) {
        return ResultUtil.success(service.uploadToken(request));
    }

    @UserLoginToken @ApiOperation("发布一张完整的AI铁盒作品图")
    @PostMapping("/boxes")
    public RespBody<Map<String, Object>> create(@RequestBody Map<String, Object> request) {
        return ResultUtil.success(service.create(request));
    }

    @UserLoginToken @ApiOperation("删除自己的铁盒")
    @DeleteMapping("/boxes/{id}")
    public RespBody<Void> delete(@PathVariable Long id) { service.delete(id); return ResultUtil.success(null); }

    @UserLoginToken @ApiOperation("点赞")
    @PostMapping("/boxes/{id}/like")
    public RespBody<Map<String, Object>> like(@PathVariable Long id) {
        return ResultUtil.success(service.reaction(id, "like", true));
    }

    @UserLoginToken @ApiOperation("取消点赞")
    @DeleteMapping("/boxes/{id}/like")
    public RespBody<Map<String, Object>> unlike(@PathVariable Long id) {
        return ResultUtil.success(service.reaction(id, "like", false));
    }

    @UserLoginToken @ApiOperation("收藏")
    @PostMapping("/boxes/{id}/collect")
    public RespBody<Map<String, Object>> collect(@PathVariable Long id) {
        return ResultUtil.success(service.reaction(id, "collect", true));
    }

    @UserLoginToken @ApiOperation("取消收藏")
    @DeleteMapping("/boxes/{id}/collect")
    public RespBody<Map<String, Object>> uncollect(@PathVariable Long id) {
        return ResultUtil.success(service.reaction(id, "collect", false));
    }

    @ApiOperation("一级评论列表")
    @GetMapping("/boxes/{id}/comments")
    public RespBody<Map<String, Object>> comments(@PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResultUtil.success(service.comments(id, page, pageSize));
    }

    @UserLoginToken @ApiOperation("发布一级评论")
    @PostMapping("/boxes/{id}/comments")
    public RespBody<Map<String, Object>> comment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return ResultUtil.success(service.comment(id, request));
    }

    @ApiOperation("AI铁盒用户主页")
    @GetMapping("/users/{id}")
    public RespBody<Map<String, Object>> user(@PathVariable Long id) { return ResultUtil.success(service.user(id)); }

    @ApiOperation("AI铁盒用户公开作品")
    @GetMapping("/users/{id}/boxes")
    public RespBody<Map<String, Object>> userBoxes(@PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResultUtil.success(service.list(page, pageSize, null, "latest", null, id));
    }

    @UserLoginToken @ApiOperation("我的作品")
    @GetMapping("/mine/boxes")
    public RespBody<Map<String, Object>> mine(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResultUtil.success(service.myBoxes(page, pageSize));
    }

    @UserLoginToken @ApiOperation("我收藏的铁盒")
    @GetMapping("/mine/collects")
    public RespBody<Map<String, Object>> myCollects(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResultUtil.success(service.savedBoxes("collect", page, pageSize));
    }

    @UserLoginToken @ApiOperation("我点赞的铁盒")
    @GetMapping("/mine/likes")
    public RespBody<Map<String, Object>> myLikes(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResultUtil.success(service.savedBoxes("like", page, pageSize));
    }
}
