# Community

开源游戏社区项目，采用前后端分离的单仓库结构。

## 项目结构

```text
community/
├── frontend/   # uni-app 前端：小程序、H5、Android、iOS
└── backend/    # Hope Framework / Spring Boot 后端服务
```

## 前端

前端位于 `frontend`，基于 uni-app、Vue 2 和图鸟 UI。

使用 HBuilderX 时，请直接打开 `frontend` 目录运行或发行。

开发环境接口地址配置在 `frontend/config/env.js`，默认连接：

```text
http://localhost:10003
```

## 后端

后端位于 `backend`，是基于 Spring Boot 2.1.10 和 JDK 8 的 Maven 多模块项目，包含完整 Demo CRUD 示例。

后端详细说明、环境要求和启动命令见 [`backend/README.md`](backend/README.md)。

常用命令：

```bash
cd backend
mvn clean package -DskipTests
mvn -pl hope-api -am spring-boot:run -Dspring-boot.run.profiles=dev
```

服务默认端口为 `10003`，接口文档地址：

```text
http://localhost:10003/doc.html
```

## 后端脚手架来源

后端代码来源：https://gitee.com/hao_jiawei/java-master

当前仓库通过 Git subtree 将脚手架纳入 `backend` 目录，整个社区项目统一版本管理，不包含嵌套 Git 仓库。
