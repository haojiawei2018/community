# Community

开源社区项目，采用前后端分离结构，可运行于微信小程序、H5、Android 和 iOS。

## 项目结构

```text
community/
├── admin/      # Vue 3 商户管理后台
├── frontend/   # uni-app 用户端
├── backend/    # Spring Boot 业务服务
├── docs/       # 开源业务架构和接口文档
└── sql/        # 开源业务数据库脚本
```

## 移动端前端

前端位于 `frontend`，基于 uni-app、Vue 2 和图鸟 UI，使用 HBuilderX 打开该目录运行。接口地址、社区编码和帖子接口开关见 [`frontend/README.md`](frontend/README.md)。

## 商户管理后台

商户后台位于 `admin`，只管理当前开源社区，不包含 SaaS 平台、租户开通、套餐或计费功能。

```bash
cd admin
pnpm install
pnpm dev
```

默认地址为 `http://localhost:3000`，网页会把 `/api` 请求代理到你手动启动的开源 Java 服务 `http://localhost:10003`。开发账号为 `merchant_admin / Merchant@123456`。

## 后端启动

后端基于 Spring Boot 2.1.10、JDK 8 和 MyBatis-Plus。

```bash
cd backend
mvn -pl hope-api -am test
mvn -pl hope-api -am spring-boot:run -Dspring-boot.run.profiles=dev
```

默认端口为 `10003`，Swagger 文档地址为 `http://localhost:10003/doc.html`。

新数据库初始化执行：

```text
sql/community_business_full.sql
```

数据库、Redis、OSS 和多端发行配置见[配置与部署说明](docs/configuration.md)。

## 开源版与商业版

本仓库只包含可独立部署的论坛业务能力。租户控制台、套餐计费、配额、商业授权和跨租户运营属于私有商业增强版，不在本仓库维护。

- [Open Core 边界](docs/architecture/open-core-boundary.md)
- [后端包结构](docs/architecture/backend-package-structure.md)
- [业务接口文档](docs/api/business-api.md)
- [小程序、H5、APP 功能清单](docs/features.md)
- [配置与部署说明](docs/configuration.md)
- [SQL 管理规范](sql/README.md)

后端脚手架来源：[Hope Framework](https://gitee.com/hao_jiawei/java-master)。
