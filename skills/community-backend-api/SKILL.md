---
name: community-backend-api
description: 学习和开发 Community 项目的 Java Spring Boot 接口，梳理 Controller、Service、Mapper、数据库契约、鉴权及上传支付集成。适用于 backend，不用于其他 Java 项目或生产运维。
---

# Community 后端接口

以当前实现为证据，帮助使用者理解、运行和扩展 API。

## 定位与读取

从当前工作区定位有 backend/pom.xml 和 AGENTS.md 的项目根目录；不要使用技能目录当仓库。先读 AGENTS.md、README.md、docs/architecture/backend-package-structure.md。学习/运行或接口示例读 [后端学习指南](references/learning.md)；真实接口字段以目标 Controller、input/output 和 Service 为准。业务文档 docs/api/business-api.md 主要描述社区，不能视为预约或宠物模块的完整文档。

## 工作流

先确认业务域与调用者，追踪 Controller → Service 接口/实现 → Mapper/XML → Model/SQL；记录 HTTP 方法、路径、登录/权限、参数、RespBody 和分页格式。只读请求输出调用链和证据；开发请求保持分层，按需同时更新契约、迁移与测试。

源码位于 backend/hope-api/src/main/java/org/hopeframework/biz/api。基础依赖在 hope-dependencies。保持 JDK 8、现有 Spring Boot/MyBatis-Plus 版本，不为普通接口任务升级整个框架。

## 关键边界

- 身份从 AuthContext/TenantContext 等服务端上下文取得。社区、预约、闪卡、铁盒、宠物、小松等模块有业务身份约束；先读 AuthenticationInterceptor 和所用 Token 服务，不能假定所有业务共用一个 Token。
- 入口使用既有 UserLoginToken / RequirePermission / PassToken 规则；权限还要核对对象归属。公开入口不自动授权写入。
- Controller 用 ResultUtil/RespBody，分页用 PageResult；不要返回数据库凭据或未筛选的实体。
- 上传、微信登录、短信、支付只读取私密配置；缺配置应报告，不填作者密钥或默认生产密码。支付金额、订单状态和回调签名遵循对应实现，测试用 mock/测试商户。
- SQL 新建/迁移是源码，数据库导出及生产数据不是学习材料；说明执行顺序，未经授权不执行迁移。
- 遵守 AGENTS 的暂存区脱敏流程，保留本地打包配置。不要输出 application-dev/prod 中的真实值。

## 验证

在 backend 运行 mvn -pl hope-api -am test；指定用例时注意上游模块没有同名测试，按当前 Surefire 配置处理。需要产物才运行 package，prod 构建选 -Pprod。核对目标 profile，禁止为构建擅自关闭服务。

鉴权修改覆盖匿名、有效、错误业务 Token、越权和作用域；上传/支付用现有服务测试扩展，不触发真实扣款。报告测试、数据库与生产验证的区别。部署、重启、支付和 Git 推送按用户明确请求执行。

起始请求：“使用 $community-backend-api 解释帖子分页接口。”或“使用 $community-backend-api 给预约模块增加只读查询，沿用当前鉴权并编写测试。”
