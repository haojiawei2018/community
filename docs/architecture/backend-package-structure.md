# 后端包结构

业务后端沿用脚手架 `DemoController` 的顶层分层方式。除 Demo 外，各层内部按业务域建立子包：

```text
org.hopeframework.biz.api
├── controller/<domain>
├── service/<domain>
├── service/impl/<domain>
├── mapper/<domain>
├── model/<domain>
├── entity/input/<domain>
└── entity/output/<domain>
```

- `controller` 只负责参数接收、权限入口和响应转换。
- `service` 保存接口，`service/impl` 保存实现类和事务边界。
- Service 接口按 Demo 命名为 `I<Domain>Service`，实现类命名为 `<Domain>ServiceImpl`。
- `mapper` 只负责数据访问，跨业务域调用必须经过 Service。
- `model` 保存数据库实体。
- `entity/input` 和 `entity/output` 分别保存请求与响应对象。
- 不再使用旧的 `module/<domain>` 目录新增代码。
- 本开源仓库禁止出现租户售卖、套餐、订阅、额度、平台管理员及跨租户运营源码。
