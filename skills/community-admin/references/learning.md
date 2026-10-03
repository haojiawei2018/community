# 后台学习指南

所有路径相对于 Community 根目录。以当前源码为准；不要使用生产账号进行学习写操作。

## 两个工程的差异

| 项目 | 社区后台 | 宠物后台 |
| --- | --- | --- |
| 目录 | admin | pet-admin |
| 页面 | src/views/*View.vue | src/views/*.vue |
| API | src/api/http.ts、merchant.ts、types.ts | src/api.ts |
| 会话 | src/stores/session.ts | src/stores/session.ts |
| 路由 | src/router/index.ts，History | src/router.ts，Hash |
| 登录 API | /api/v1/auth/login，clientType=ADMIN | /api/admin/v1/pet-snack/login |
| 业务 API | /api/admin/v1/*，部分公开读接口 /api/v1/* | /api/admin/v1/pet-snack/* |
| 响应解包 | API 方法取 response.data.data | 响应拦截器已返回业务 data |
| 路由权限 | meta.permission + hasPermission | 主要检查 Token 存在，服务端仍需授权 |
| 开发端口 | vite.config.ts 当前 3000 | vite.config.ts 当前 5175 |
| 当前 base | /admin/ | /admin/ |

## 本地启动

1. 后端用自己的测试数据与凭据运行；网页不会启动 Java。
2. 在目标工程的本地 .env 配置 VITE_API_BASE_URL=http://localhost:10003。它是公开 API 地址，不是密钥，仍按本地环境文件忽略规则管理。
3. 进入 admin 或 pet-admin：pnpm install、pnpm dev、pnpm build。
4. 社区开发代理可用 VITE_API_TARGET，但如果客户端 baseURL 是绝对地址，请求直接到该地址，不会走 Vite 的 /api 代理。检查实际网络 URL 再判断 CORS。
5. 在浏览器打开终端显示的 URL，用自己的管理员登录；不要复制代码或文档中的默认密码。

## 页面与接口调用链

社区成员管理：
src/views/MembersView.vue → src/api/merchant.ts.getMembers → apiClient → /api/admin/v1/members → 后端权限 member.read → data.records。

宠物订单：
src/views/Orders.vue → src/api.ts.api.orders → http → /api/admin/v1/pet-snack/orders → 拦截器 data 解包 → 页面。

增加筛选时先确认后端接受的字段，再统一 query 参数与分页；筛选改变通常重置第一页。新增社区页面需要 router、布局菜单和权限相匹配，不只创建 Vue 文件。

## 图片和权限

社区上传方法 merchant.ts.uploadImage 使用 /api/v1/files/images；宠物 api.upload 使用 /api/admin/v1/pet-snack/images。两个方法都发送 FormData(file)，返回结构以各自 API 包装为准。将返回 URL 保存到业务对象，不将 File/本地临时路径直接提交成图片地址。

社区 401 会清会话并跳登录，403 显示无权限；宠物 API 当前主要把错误包装成 Error，并非完整的自动登录恢复。遇到问题先说明当前行为，再在用户请求范围内修复，不能假定两套机制相同。

## 建议练习与交付

- 学习：说明一个菜单到后台 API 的完整链路和权限码。
- 练习：给现有列表增加空状态和刷新功能，使用测试数据。
- 联调：检查错误密码、无登录、无权限、请求失败及上传失败；涉及写操作在获准测试环境执行。
- 构建：pnpm build，报告 TypeScript 检查与 Vite 结果。
- 部署：社区参考 admin/deploy；History 刷新要回退到 /admin/index.html。宠物 Hash URL 保留 # 路由。两套 /admin/ 路径部署到各自站点。
