# Community Mobile Frontend

基于 uni-app、Vue 2 和图鸟 UI 的小程序、H5、Android、iOS 用户端。

## 本地接口配置

编辑 `config/env.js`：

- `baseURL`：后端地址。本机 H5 可使用 `http://localhost:10003`；真机或微信开发者工具需要改为电脑局域网地址或已配置的 HTTPS 域名。
- `communityCode`：SaaS 租户编码；开源单社区版默认使用 `default`。
- `useMock`：后端暂不可用时可切换为 `true`，登录和社区初始化也会返回符合真实接口结构的 mock 数据。

## 接口和登录态

- 用户端接口统一使用 `/api/v1/**`。
- 请求自动携带 `Authorization: Bearer <accessToken>` 和 `X-Tenant-Code`。
- Token、刷新令牌、当前用户、社区信息和设备编号统一由 `utils/session.js` 管理。
- App 启动时静默加载社区信息，并在访问令牌过期前使用刷新令牌恢复会话。

当前已接入社区初始化、注册、登录、刷新令牌、退出登录和当前用户接口。帖子、圈子、评论接口应在对应后端 P0 接口完成后逐步替换页面静态数据。
