# 商户管理后台

开源单社区的独立商户后台前端，不包含 SaaS 平台、租户开通、套餐、订阅或计费功能。Java 后端由开发者自行启动，网页通过 Vite 代理连接当前开源商户服务。

```bash
pnpm install
pnpm dev
pnpm build
```

- 网站：`http://localhost:3000`
- 接口代理：`http://localhost:10003`
- 默认开发账号：`merchant_admin`
- 默认开发密码：`Merchant@123456`

网页不负责启动 Java 服务。如果后端端口不同，可通过 `VITE_API_TARGET` 修改代理目标。
