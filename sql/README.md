# SQL 管理规范

本目录只保存开源社区业务数据库脚本。

- `community_business_v1.sql`：开源业务版数据库基线。
- `V20260810_01__seed_p0_permissions.sql`：业务权限目录初始化。
- `demo.sql`：脚手架 Demo 表。

开源版默认单社区运行，业务表中的 `tenant_id` 是内部数据作用域字段，由后端统一写入，客户端不能传递。商业 SaaS 控制面、套餐、计费和租户生命周期 SQL 仅保存在私有仓库。

后续增量脚本使用 `VYYYYMMDD_NN__description.sql`，人工回滚脚本使用 `RYYYYMMDD_NN__description.sql`。目标数据库为 MySQL 5.7+，统一使用 `utf8mb4`。
