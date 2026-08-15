# Community Mobile Frontend

基于 uni-app、Vue 2 和图鸟 UI 的小程序、H5、Android、iOS 用户端。

## 接口配置

编辑 `config/env.js`：

- `baseURL`：小程序、H5 和 APP 统一配置为 `http://42.193.104.179:10003`；后端已放行 H5 来源、请求头和 `OPTIONS` 预检。
- `communityCode`：SaaS 租户编码；开源单社区版默认使用 `default`。
- `useMock`：后端暂不可用时可切换为 `true`，登录和社区初始化也会返回符合真实接口结构的 mock 数据。

## 接口和登录态

- 用户端接口统一使用 `/api/v1/**`。
- 请求自动携带 `Authorization: Bearer <accessToken>` 和 `X-Tenant-Code`。
- Token、刷新令牌、当前用户、社区信息和设备编号统一由 `utils/session.js` 管理。
- App 启动时静默加载社区信息，并在访问令牌过期前使用刷新令牌恢复会话。

当前已接入社区初始化、注册登录、用户资料、圈子、活动、帖子信息流、帖子详情、发布与删除、评论、点赞、图片上传、举报屏蔽、消息、签到和用户主页。`forumApiEnabled` 默认开启，仅在后端不可用的视觉调试场景临时关闭。

完整功能、小程序/H5/APP 差异和界面预览见 [`../README.md`](../README.md)，数据库、OSS 和发行配置见 [`../docs/configuration.md`](../docs/configuration.md)。

## H5 部署

H5 固定部署在 `/h5/`，生产包入口和静态资源均使用该基础路径。将构建产物解压到服务器站点的 `h5` 目录，并参考 [`deploy/nginx-h5.conf.example`](deploy/nginx-h5.conf.example) 配置静态资源回退。接口直接请求 `http://42.193.104.179:10003`，不依赖站点 `/api` 代理。
