package org.hopeframework.biz.api.controller.banshu;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 半熟 iOS 社交 APP 的独立用户端接口入口。
 *
 * 后续接口统一放在本 Controller 的 /api/v1/banshu 命名空间下，
 * 用户身份与业务数据不复用社区、闪卡等其他产品。
 */
@Api(tags = "半熟社交APP")
@RestController
@RequestMapping("/api/v1/banshu")
public class BanshuAppController {
}
