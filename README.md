# Hope Framework

一个开箱即用的 **Spring Boot 后端服务脚手架**，基于 Spring Boot 2.1.10 + JDK 8。内置了统一响应、全局异常、JWT 认证、多数据源、MyBatis-Plus、Redis、分页、Swagger 等基础能力，并提供一个完整的 CRUD Demo，方便直接在其上叠加业务代码。

## 项目地址

- 仓库：https://gitee.com/hao_jiawei/java-master
- 克隆：`git clone https://gitee.com/hao_jiawei/java-master.git`

## 技术栈

| 分类 | 组件 |
| --- | --- |
| 框架 | Spring Boot 2.1.10、Spring MVC |
| ORM | MyBatis-Plus 3.3.1、PageHelper |
| 数据源 | Druid、dynamic-datasource（动态多数据源） |
| 数据库 | MySQL 5.7 |
| 缓存 | Redis（Jedis 客户端 + RedisUtil 工具） |
| 认证 | JWT（java-jwt / jjwt）+ 拦截器注解鉴权 |
| 文档 | Swagger 2 / swagger-bootstrap-ui |
| 构建 | Maven 多模块 |

## 项目结构

```
java-master
├── hope-api                     # 业务接口服务（可执行模块）
│   └── src/main
│       ├── java/org/hopeframework/biz/api
│       │   ├── auto             # 认证拦截器、注解、Web 配置
│       │   ├── config           # Swagger、ID 生成、Redis、分页配置
│       │   ├── controller       # 接口层（DemoController 示例）
│       │   ├── entity           # 入参/出参/分页对象
│       │   ├── helper           # 通用辅助类
│       │   ├── mapper           # MyBatis-Plus Mapper（DemoMapper 示例）
│       │   ├── model            # 数据库实体（Demo 示例）
│       │   ├── service          # 业务层（接口 + 实现）
│       │   └── util             # 通用工具类
│       └── resources
│           ├── application*.yml # 多环境配置（dev/test/prod）
│           ├── sql/demo.sql     # 演示表建表脚本
│           └── xml              # MyBatis XML（自定义 SQL 示例）
└── hope-dependencies            # 基础组件库（聚合模块）
    ├── hope-core                # 统一响应、异常、日志、Web 配置
    ├── hope-utils               # 通用工具库
    └── hope-test                # 测试辅助库
```

## 快速开始

1. **准备环境**：JDK 8、Maven 3.6+、MySQL 5.7、Redis。
2. **初始化数据库**：在本地 MySQL 执行 `hope-api/src/main/resources/sql/demo.sql`（默认数据库名 `hope`）。
3. **修改配置**：按需修改 `hope-api/src/main/resources/application-dev.yml` 中的数据库和 Redis 连接（默认 `localhost:3306/hope`，账号 `root/root`）。
4. **启动服务**：

```bash
# 方式一：直接运行
mvn -pl hope-api -am spring-boot:run -Dspring-boot.run.profiles=dev

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar hope-api/target/hope-biz-api-1.0.0.jar --spring.profiles.active=dev
```

5. **访问接口文档**：http://localhost:10003/doc.html （或 http://localhost:10003/swagger-ui.html）

## 内置能力说明

- **统一响应**：所有接口返回 `RespBody`（code / message / data），配合 `ResultUtil.success(...)` 使用。
- **全局异常**：`hope-core` 内置 `ExceptionHandler` 与 `HopeException`，业务中直接抛出即可。
- **JWT 认证**：
  - `@PassToken`：跳过鉴权；
  - `@UserLoginToken`：校验用户 token；
  - `@AdminLoginToken`：校验管理员 token。
- **动态多数据源**：在 `application-*.yml` 的 `spring.datasource.dynamic.datasource` 下配置数据源，Service/Mapper 上使用 `@DS("数据源名")` 切换。
- **Redis**：注入 `RedisUtil` 即可使用常用缓存操作。
- **分页**：MyBatis-Plus `Page` + `PageResultHelper`，或 PageHelper 均可。

## Demo 接口（示例）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | /demo/{id} | 根据 ID 查询 |
| GET | /demo/list | 列表查询（支持 name/phone 模糊/精确过滤） |
| GET | /demo/page | 分页查询（page / pageSize） |
| POST | /demo | 新增 |
| PUT | /demo | 修改 |
| DELETE | /demo/{id} | 删除 |

## 如何新增业务

参照 Demo 的完整链路新增即可：

1. `model` 中新增实体（`@TableName` 指向表）；
2. `mapper` 中新增接口继承 `BaseMapper<T>`；
3. `service` 中新增接口与实现（实现类加 `@Service` 和 `@DS(...)`）；
4. `controller` 中新增接口；
5. 需要自定义 SQL 时在 `resources/xml` 下新增对应的 XML。

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 开源说明

- 本仓库由业务项目精简而来，已删除原业务代码与敏感配置（数据库账号密码等），所有环境默认连接本地 `localhost`。
- 欢迎 Star / Fork / Issue，一起把它做得更好。
