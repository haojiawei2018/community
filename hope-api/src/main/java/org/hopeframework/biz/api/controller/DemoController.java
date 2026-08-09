package org.hopeframework.biz.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.DemoInput;
import org.hopeframework.biz.api.entity.output.DemoOutput;
import org.hopeframework.biz.api.service.IDemoService;
import org.hopeframework.biz.api.util.ResultUtil;
import org.hopeframework.core.response.RespBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 演示 Controller：一个完整的 REST CRUD 示例。
 * 开发时参照本类新增自己的业务 Controller。
 */
@Api(tags = "演示接口")
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final IDemoService demoService;

    public DemoController(IDemoService demoService) {
        this.demoService = demoService;
    }

    @PassToken
    @ApiOperation("根据ID查询")
    @GetMapping("/{id}")
    public RespBody<DemoOutput> getById(@PathVariable Long id) {
        return ResultUtil.success(demoService.getById(id));
    }

    @PassToken
    @ApiOperation("列表查询")
    @GetMapping("/list")
    public RespBody<List<DemoOutput>> list(DemoInput input) {
        return ResultUtil.success(demoService.list(input));
    }

    @PassToken
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public RespBody<PageResult> page(DemoInput input) {
        return ResultUtil.success(demoService.page(input));
    }

    @PassToken
    @ApiOperation("新增")
    @PostMapping
    public RespBody<Boolean> save(@RequestBody DemoInput input) {
        return ResultUtil.success(demoService.save(input));
    }

    @PassToken
    @ApiOperation("修改")
    @PutMapping
    public RespBody<Boolean> update(@RequestBody DemoInput input) {
        return ResultUtil.success(demoService.update(input));
    }

    @PassToken
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public RespBody<Boolean> delete(@PathVariable Long id) {
        return ResultUtil.success(demoService.delete(id));
    }
}