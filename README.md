# Community

开源游戏社区项目，采用前后端分离结构，可运行于微信小程序、H5、Android 和 iOS。

## 项目结构

```text
community/
├── frontend/   # uni-app 用户端
├── backend/    # Spring Boot 业务服务
├── docs/       # 开源业务架构和接口文档
└── sql/        # 开源业务数据库脚本
```

## 后端启动

后端基于 Spring Boot 2.1.10、JDK 8 和 MyBatis-Plus。

```bash
cd backend
mvn -pl hope-api -am test
mvn -pl hope-api -am spring-boot:run -Dspring-boot.run.profiles=dev
```

默认端口为 `10003`，Swagger 文档地址为 `http://localhost:10003/doc.html`。

初始化数据库时依次执行：

```text
sql/community_business_v1.sql
sql/V20260810_01__seed_p0_permissions.sql
sql/V20260810_02__seed_default_roles.sql
```

## 开源版与商业版

本仓库只包含可独立部署的论坛业务能力。租户控制台、套餐计费、配额、商业授权和跨租户运营属于私有商业增强版，不在本仓库维护。

- [Open Core 边界](docs/architecture/open-core-boundary.md)
- [业务接口文档](docs/api/business-api.md)
- [SQL 管理规范](sql/README.md)

后端脚手架来源：[Hope Framework](https://gitee.com/hao_jiawei/java-master)。
