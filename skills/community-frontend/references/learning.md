# 学习与接入

所有源码路径均相对于使用者的 Community 项目根目录。以下示例对应当前源码，变更时以实际文件为准。

## 建议阅读顺序

| 目的 | 文件 | 看什么 |
| --- | --- | --- |
| 路由与组件注册 | frontend/pages.json | 页面路径、首页、easycom 的 tn-* 映射 |
| 启动 | frontend/App.vue | 社区初始化、登录恢复 |
| 请求配置 | frontend/config/env.js | baseURL、communityCode、useMock、forumApiEnabled |
| 响应和鉴权 | frontend/api/http.js | Bearer、401、403、响应解包 |
| 会话 | frontend/utils/session.js | Token、刷新令牌、用户、设备 |
| API 汇总 | frontend/api/index.js | user、community、message、file、checkIn |
| 页面样例 | frontend/pages/post/detail.vue | 页面 → API → 返回数据 → 渲染 |

实际调用链：页面 → api/modules/community.js → api/http.js → 后端 /api/v1/posts → 返回 data → normalizePage → 页面 rows。成功响应 code 为 0 或 200；失败返回拒绝的 Promise，不能当空数据继续使用。

## 本地运行

1. 使用 HBuilderX 打开 frontend。项目没有通用的 pnpm build 用户端流程。
2. 将 config/env.js 的 baseURL 配成自己的测试服务，例如 http://localhost:10003；真机用可达的局域网地址。微信正式环境需要 HTTPS 合法域名。
3. 真接口学习设 useMock=false、forumApiEnabled=true；视觉学习可启用 mock，并明确 mock 不验证后端。
4. 从首页真实点击到目标页面，不只打开孤立组件。AppID、证书、权限由自己的账号提供。
5. H5 构建路径在 manifest.json 的 h5.router 中，当前 hash 模式、base=/h5/；部署示例在 frontend/deploy/。

## 使用已有方法

在页面的 script 中可调用：

```js
import { community, file } from '@/api/index.js'

// 查询：该模块已经把 records 归一化为 rows。
const page = await community.getPostList({ page: 1, pageSize: 10, sort: 'LATEST' })
const rows = page.rows

// 上传会产生对象，仅在获准的测试存储执行。
const image = await file.uploadImage(selectedLocalFilePath)
// 后续发帖使用 image.url，不使用设备临时路径。
```

不要把这些片段直接粘在 Vue 2 script 顶层执行；放入对应 async 方法，配套 loading、catch、finally 和重复点击保护。发帖最多 9 张图片，契约见 docs/api/business-api.md。

## 练习与排错

- 只读练习：找出 posts API 的调用者，说明匿名查询与登录写操作的差异。
- 页面练习：复用已有分页方法增加加载/空状态，不改数据库。
- 401：检查 session 是否过期以及刷新逻辑，不把 Token 硬编码进页面。
- 403：先检查身份/作用域；不能靠修改 communityCode 或用户 ID 绕过权限。
- 上传错误：uni.uploadFile 返回 data 可能是 JSON 字符串，由公共模块处理；检查字段名 file、登录和服务端配置。
- 连不上接口：分辨设备可达性、平台合法域名、CORS 与后端状态，不先切 mock 来掩盖联调问题。
