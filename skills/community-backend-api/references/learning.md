# 后端学习指南

路径相对于使用者的 Community 仓库根目录。先确认本机工具和当前源码，不复制作者服务器配置。

## 工程与启动

- backend/pom.xml：JDK 8、Spring Boot 2.1.10，聚合 hope-api 与 hope-dependencies。
- backend/hope-api/pom.xml：业务依赖、资源过滤和可执行模块。
- dev/test/prod 是 Maven profile；application.yml 使用 @profileActive@。构建前确认目标环境。
- 使用自己的 MySQL、Redis、管理员密码与 Token 密钥。变量说明见 docs/configuration.md，新增模块以 Git 中脱敏配置为准。
- 本机配置可能和 Git 版本不同且含凭据。学习时读 Git 版本或只提取变量名称，不打印实际密码。

```powershell
cd backend
mvn -pl hope-api -am test
mvn -pl hope-api -am package -Pdev
```

从 hope-api/target 找实际生成的可执行 JAR，再用 java -jar 启动；不要把固定 jar 文件名或已有运行进程当作保证。默认端口 10003，Swagger 文档入口 README 记录为 /doc.html。启动需要自己的配置与数据库，不自动执行初始化 SQL。

社区空库脚本是 sql/community_business_full.sql；其他业务位于 backend/hope-api/src/main/resources/sql。先审阅 SQL，再在明确允许的空测试库执行；不要把完整基线重复导入已有业务库。

## 先理解一个只读接口

路径：
- Controller：controller/forum/PostController.java。
- 契约：entity/input/forum/PostPageRequest.java 与 entity/output/forum/PostResponse.java。
- 业务：service/forum/IForumService.java、service/impl/forum/ForumServiceImpl.java。
- 返回：entity/PageResult.java；公共包装在 hope-dependencies/hope-core 的 RespBody.java。

```powershell
# 服务已在本机运行时，以下查询不会创建订单或内容。
Invoke-RestMethod 'http://localhost:10003/api/v1/bootstrap'
Invoke-RestMethod 'http://localhost:10003/api/v1/posts?page=1&pageSize=10&sort=LATEST'
```

统一响应：{ code, message, data }。分页 data 为 { records, total, pageSize, page }。接口可能返回 HTTP 错误，也可能返回业务 code，调用者须按实际封装处理两层结果。

## 新接口最小落点

| 层 | 责任 |
| --- | --- |
| controller/<domain> | 路由、请求接收、鉴权入口、ResultUtil.success |
| entity/input、entity/output | 参数契约与响应字段 |
| service/<domain> | I*Service 接口 |
| service/impl/<domain> | 校验、业务规则、事务和归属检查 |
| mapper/<domain> 与 resources/xml | 查询与更新 |
| model/<domain> | 数据库实体 |
| resources/sql 或根 sql | 与业务相符的迁移 |

先复用一个同域接口作为范例，不为演示另造一套 MVC 结构。新增字段检查空值、日期、分页上限、逻辑删除；MyBatis XML 的 < 使用 &lt; 或 CDATA。

## 特殊模块

- 鉴权：auto/AuthenticationInterceptor.java、common/security/*AccessTokenService.java、common/tenant/。
- 社区图片：controller/file/ImageUploadController.java；具体 OSS/COS 服务位于 service/impl/file。
- 预约与宠物微信支付：config/booking、config/petsnack 及对应 service/impl；测试参考 BookingWechatPayServiceImplTest、WechatPayV2UtilsTest。
- 独立模块：controller/booking、flashcard、ironbox、petsnack、xiaosongtv、banshu；路由与身份以当前代码为准。

练习：列出一个接口从输入到数据库再到输出的调用链；随后在测试中模拟错误业务 Token 或对象归属错误，确认请求被拒绝。不要以修改请求头租户 ID 作为“修复权限”的方式。
