# 开源业务版 API

开源版默认以单社区模式运行，不需要客户端传递租户信息。业务接口分为用户端和社区管理端：

- 用户端：`/api/v1/**`
- 社区管理端：`/api/admin/v1/**`

## 启动信息

- `GET /api/v1/bootstrap`：返回社区名称、Logo、版本和功能开关。

## 注册与登录

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`

注册示例：

```json
{
  "username": "player_01",
  "password": "safe-password-123",
  "nickname": "玩家一号",
  "deviceId": "device-uuid",
  "clientType": "WECHAT"
}
```

登录成功后，使用标准请求头：

```http
Authorization: Bearer <accessToken>
```

刷新令牌采用单次轮换，刷新成功后必须保存响应中的新令牌。

## 当前用户

- `GET /api/v1/users/me`：当前账号、社区成员、角色和权限。
- `PUT /api/v1/users/me`：修改 `displayName`、`avatarUrl` 和 `bio`。

## 社区后台

社区后台只管理论坛业务，包括成员、角色、游戏、圈子、板块、帖子、评论、审核、举报、公告和审计。商业 SaaS 控制面不属于本仓库。
