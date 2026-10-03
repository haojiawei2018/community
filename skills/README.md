# Community 学习技能

三个技能以项目源码为依据，供使用者学习、运行与按项目约定开发；不包含真实密钥、密码或服务器登录信息。

| 技能 | 用途 | 调用示例 |
| --- | --- | --- |
| [community-frontend](community-frontend/SKILL.md) | uni-app 用户端与接口接入 | 使用 $community-frontend 解释帖子列表调用链，先不修改 |
| [community-backend-api](community-backend-api/SKILL.md) | Spring Boot API、鉴权与数据分层 | 使用 $community-backend-api 梳理预约接口并列出参数 |
| [community-admin](community-admin/SKILL.md) | 社区和宠物 Vue 3 后台 | 使用 $community-admin 带我运行社区后台 |

## 给其他使用者安装

将需要的技能目录（含 SKILL.md、agents、references）完整复制到自己的 Codex 用户技能目录，通常为 ~/.codex/skills；自定义 CODEX_HOME 时使用该目录下的 skills。例如 Windows 为 C:/Users/<用户名>/.codex/skills/community-frontend。不复制作者的 .env 或私密配置。

重新加载技能或开启新会话，在 Community 项目内使用 $community-frontend 等名称调用。只复制技能即可学习指南；修改或运行需要另有 Community 仓库，并告知路径。技能的源码路径相对于仓库根目录，不依赖作者电脑路径。

## 范围和维护

各 SKILL.md 是操作入口，references/learning.md 为学习流程、实际源码映射和练习。示例 API 地址为 localhost，占位凭据由使用者自行提供。学习不自动授权生产写入、扣款、部署或 Git 推送。

源码或 API 变更后，更新对应目录映射、调用方式和例子。README 的社区功能清单不覆盖所有新增业务；接口和行为以当前 Controller/API 模块为准。使用声明及本地配置保护遵循项目 README.md 和 AGENTS.md。
