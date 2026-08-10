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

所有社区后台接口都要求 `Authorization: Bearer <accessToken>`，并按接口校验业务权限。首位注册用户自动成为 `OWNER`，`OWNER` 拥有当前社区全部业务权限。

### 成员分页

- `GET /api/admin/v1/members`
- 权限：`member.read`
- 查询参数：`page`、`pageSize`、`keyword`、`status`
- `pageSize` 最大为 100；`keyword` 当前匹配社区昵称；`status` 支持 `ACTIVE`、`MUTED`、`BANNED`、`LEFT`。

响应 `data`：

```json
{
  "records": [
    {
      "memberId": 1,
      "userId": 1,
      "username": "player_01",
      "displayName": "玩家一号",
      "avatarUrl": null,
      "status": "ACTIVE",
      "muteUntil": null,
      "joinedAt": "2026-08-10T10:00:00.000+00:00",
      "roles": ["OWNER"]
    }
  ],
  "total": 1,
  "pageSize": 10,
  "page": 1
}
```

### 修改成员状态

- `PUT /api/admin/v1/members/{memberId}/status`
- 权限：`member.status.write`
- `MUTED` 必须提供晚于当前时间的 `muteUntil`；切换到其他状态会清空禁言时间。
- 不允许把当前登录成员自己设置为 `BANNED` 或 `LEFT`。

```json
{
  "status": "MUTED",
  "muteUntil": "2026-08-11T10:00:00.000+00:00"
}
```

### 查询角色

- `GET /api/admin/v1/roles`
- 权限：`member.role.write`
- 返回当前社区角色及每个角色绑定的权限编码。

### 覆盖设置成员角色

- `PUT /api/admin/v1/members/{memberId}/roles`
- 权限：`member.role.write`
- `roleIds` 至少包含一个当前社区角色；该操作会覆盖成员原有角色。
- 只有 `OWNER` 可以授予 `OWNER` 角色。

```json
{
  "roleIds": [2, 3]
}
```
