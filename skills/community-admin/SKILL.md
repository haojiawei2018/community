---
name: community-admin
description: 学习、运行和修改 Community 的 Vue 3 管理后台，包括社区 admin 与宠物 pet-admin 的页面、API、登录路由和构建部署配置。适用于后台管理前端，不用于 uni-app 用户端或 Java 接口实现。
---

# Community 后台管理

先识别后台工程，再沿页面、API、会话与服务端权限链工作。

## 定位与学习

在当前工作区找含 admin/package.json、pet-admin/package.json 和 AGENTS.md 的 Community 仓库；不要固定作者磁盘路径。先读 AGENTS.md 与目标工程的配置；初次使用读 [后台学习指南](references/learning.md)。

用户说“后台”但未指定业务时，用请求中的社区成员/圈子/活动或商城商品/订单判断；仍有歧义时询问目标，同时可只读比较两套工程。不把社区网页误认为预约/闪卡后台，其他后台可能在此仓库之外。

## 实施

在选定工程复用 Vue 3 + TypeScript + Element Plus、现有 router/store/API。先读当前页面再改；表格分页、筛选、编辑弹窗、上传、失败恢复和重复提交需沿用已有模式。

admin 的 Axios 客户端返回原始响应，由 merchant.ts 解包；pet-admin 的拦截器已返回业务 data。不能把两套 response.data.data 逻辑互相照搬。社区 router 有 permission 元数据，宠物 router 当前主要检查是否有 Token；两者都依赖后端授权，不把前端守卫当安全边界。

账号和密码由使用者提供，不预填作者密码。浏览器不能存 OSS/COS 或支付密钥；上传经业务 API，使用 FormData 的 file 字段。保留本地配置、提交脱敏按 AGENTS 执行。

## 验证和部署范围

在目标工程运行 pnpm build（包含 vue-tsc），已有依赖可直接执行 vue-tsc 和 vite。检查登录、筛选分页、表单校验、请求失败、权限拒绝与页面刷新；涉及写入仅在授权测试环境操作。

确认 VITE_API_BASE_URL、Vite base 与路由模式。两个后台当前都使用 /admin/ 构建基础路径，不可据此部署到同一站点目录。社区 History 路由需服务端回退，宠物 Hash 路由行为不同。构建不代表服务器已部署。发布、覆盖站点文件或生产操作需要对应请求授权。

交付说明目标工程、文件、API、构建和浏览器结果；必要时说明后端接口缺口，不虚构后台功能。

起始请求：“使用 $community-admin 带我理解社区成员管理。”或“使用 $community-admin 给宠物后台订单页增加筛选，保持当前响应解包方式。”
