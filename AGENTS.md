# 项目协作指南

适用于本仓库所有目录。若子目录存在更具体的 `AGENTS.md`，在该目录工作时同时遵循它。默认使用中文沟通。

## 项目与目录

- `backend/`：JDK 8、Spring Boot 2.1.10、多模块 Maven、MyBatis-Plus；`hope-api` 为业务模块，`hope-dependencies` 为基础依赖。
- `frontend/`：uni-app、Vue 2、图鸟 UI，使用 HBuilderX 发布小程序、H5 和 APP。
- `admin/`：Vue 3、TypeScript、Vite、Element Plus，社区商户后台。
- `pet-admin/`：Vue 3 宠物零食商城后台。与 `admin/` 是两个独立工程，先确认任务对应哪个后台。
- `sql/`：开源社区初始化、增量迁移及人工回滚脚本。
- `backend/hope-api/src/main/resources/sql/`：预约、闪卡、铁盒、宠物零食、小松电视等业务的数据库脚本。
- `docs/`：架构、接口、配置与部署文档；入口为 `README.md` 和 `docs/configuration.md`。

保持开源版与私有商业版边界，参见 `docs/architecture/open-core-boundary.md`。不把其他项目目录或商业 SaaS 控制面源码带入本仓库。

## 工作方式

- 开始前查看 `git status --short`，保留用户已有修改。用户要求“先梳理”时先说明调用流程；要求只改指定范围时严格限制差异。
- 后端沿用 `controller/<domain>`、`service/<domain>`、`service/impl/<domain>`、`mapper/<domain>`、`model/<domain>`、`entity/input/<domain>`、`entity/output/<domain>` 分层，不新增旧式 `module/<domain>` 结构。
- Controller 负责入口、参数和响应；业务与事务放 Service；数据访问放 Mapper。参见 `docs/architecture/backend-package-structure.md`。
- 社区、预约、闪卡、铁盒、宠物零食、小松电视等业务共用后端，修改公共拦截器、Token、上传或异常处理时检查各业务兼容性。
- 保持 Token 签发者、身份类型、业务路由和数据作用域隔离；客户端传入的用户 ID、租户 ID 不能代替服务端鉴权。
- Java 保持 JDK 8 兼容；MyBatis XML 中 `<` 使用 `&lt;` 或 CDATA，可空日期先判空。
- 前端复用现有 API、状态管理、组件及样式；不直接修改 `dist/`、`unpackage/`、`target/`、`release/` 等生成目录。

## 密钥与提交：仓库脱敏，本地保留

**用户明确要求：密钥只在提交版本中去掉，本机打包所需的原配置必须保留。**

- 不提交 OSS/COS 上传密钥、微信 AppSecret、支付 API Key、短信凭据、Token 签名密钥、数据库/Redis/服务器密码、SSH 私钥和支付/签名证书，也不在命令输出、日志或回复中展示真实值。
- 环境变量名称、空占位符、读取配置的业务代码可以提交；真实值、可用默认密码、前端预填密码不得进入提交。单元测试中明确的虚构凭据只用于测试。
- `.env`、私密 YAML、证书、私钥、数据库备份、上传数据及构建压缩包按 `.gitignore` 排除；SQL 结构和迁移脚本可以提交，但先检查是否包含真实账号或生产数据。
- 当前 `application-dev.yml` 和 `application-prod.yml` 可能标记为 `skip-worktree`。先用 `git ls-files -v` 核对；该标记只影响本机，并不表示文件已从 Git 删除。
- 本机与 Git 中的同一文件可能故意不同：公共 `application.yml`、后台登录代码或页面可能含本地打包配置。禁止未经检查执行 `git add .`、`git add -A` 或批量 `git add -u`。
- 提交前先记录需要保留的本地版本，制作脱敏版本并仅暂存指定文件，再恢复本机版本。恢复后不要重新暂存这些文件；以 `git show :路径` 和 `git diff --cached` 检查实际提交内容。临时私密备份必须放在 Git 忽略的位置，不能残留在提交中。
- 提交后确认本机配置已恢复；本地敏感配置差异是预期状态，不应为追求工作区干净而丢弃。后续修改这些文件时必须区分业务改动和私密配置。
- 仓库不保存密钥值或指纹。新拉取的脱敏代码需要通过环境变量或本地私密配置提供凭据才能启动/打包。

## 验证与构建

后端命令在 `backend/` 执行：

```powershell
mvn -pl hope-api -am test
mvn -pl hope-api -am package -Pprod
```

本机 Maven 不在 PATH 时，先检查 `D:\apache-maven-3.5.0\bin\mvn.cmd` 是否存在，再用完整路径执行；其他环境自行定位 Maven。构建 profile 有 `dev`、`test`、`prod`，打包前确认目标 profile 和本地私密配置。不要为构建擅自停止用户正在运行的 Java 服务。

两个 Vue 3 后台分别进入各自目录执行：

```powershell
pnpm install
pnpm dev
pnpm build
```

已有依赖但 pnpm 版本或安装策略阻止执行时，可直接验证现有工具链：

```powershell
node node_modules/vue-tsc/bin/vue-tsc.js --noEmit
node node_modules/vite/bin/vite.js build
```

`frontend/` 用 HBuilderX 验证实际目标平台。社区后台、宠物后台当前构建基础路径均为 `/admin/`，社区 H5 为 `/h5/`；各站点按对应部署配置部署，不假设它们属于同一个后台。

按改动风险验证：鉴权检查未登录、错误业务 Token、越权及跨作用域访问；支付/上传测试用 mock 或明确测试环境，不能为了验证触发真实扣款或上传生产对象。编译或单测通过不代表真机、数据库、支付或生产部署已验证。纯文档改动检查差异与路径即可。

## Git 与交付

- 远端 `gitee` 和 `github` 是项目仓库，`backend-upstream` 是脚手架来源。用户要求同步两个仓库时推送 `gitee`、`github`，不要推送脚手架上游。
- 普通修改请求不自动授权部署、运行生产迁移或真实支付。提交/推送按用户请求执行；推送前检查远端分支和暂存区，不强推或改写已发布历史。
- 提交前检查 `git diff --cached --stat`、`git diff --cached --check`，扫描实际暂存内容中的凭据，并确认本地私密配置已恢复。
- 数据库迁移注明执行顺序、兼容性和回滚条件；不要擅自执行 SQL、删除生产数据或替换正在运行的服务。
- 交付说明改动、验证结果、提交号、两个远端的实际结果，以及保留的本地私密差异。失败或未运行的检查必须明确说明。
