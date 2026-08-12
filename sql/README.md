# SQL 管理规范

本目录只保存开源社区业务数据库脚本。

- `community_business_full.sql`：推荐用于新建空数据库的完整初始化文件，包含当前开源业务所需的 36 张表、权限、默认角色和综合圈子。
- `community_business_v1.sql`：开源业务版数据库基线。
- `V20260810_01__seed_p0_permissions.sql`：业务权限目录初始化。
- `V20260810_02__seed_default_roles.sql`：默认角色及角色权限初始化。
- `V20260810_03__create_forum_content_core.sql`：圈子、帖子、评论、点赞表及默认综合圈子。
- `V20260812_04__create_user_safety.sql`：用户举报与屏蔽表。
- `R20260810_03__create_forum_content_core.sql`：论坛内容核心表人工回滚脚本，会删除已有论坛数据。
- `demo.sql`：脚手架 Demo 表。

开源版默认单社区运行，业务表中的 `tenant_id` 是内部数据作用域字段，由后端统一写入，客户端不能传递。商业 SaaS 控制面、套餐、计费和租户生命周期 SQL 仅保存在私有仓库。

后续增量脚本使用 `VYYYYMMDD_NN__description.sql`，人工回滚脚本使用 `RYYYYMMDD_NN__description.sql`。目标数据库为 MySQL 5.7+，统一使用 `utf8mb4`。

新环境只执行 `community_business_full.sql`，不要再重复执行其他基线和迁移。历史环境继续按迁移编号升级；首位注册用户会自动绑定 `OWNER`，后续注册用户默认绑定 `MEMBER`。

完整初始化文件应在空数据库执行。`CREATE TABLE IF NOT EXISTS` 不会自动补齐已有同名表缺失的字段，因此结构已经混乱的旧库应先备份，再新建数据库导入完整文件并迁移业务数据。
