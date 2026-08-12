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
- `GET /api/v1/users/me/summary`：当前成员已发布帖子数、帖子累计获赞数和已发表评论数。
- `GET /api/v1/users/me/posts`：当前成员公开帖子分页，支持 `page`、`pageSize`。
- `DELETE /api/v1/users/me/posts/{postId}`：删除当前成员自己发布的帖子；不能删除其他成员的帖子。

## 社区成员公开主页

- `GET /api/v1/members/{memberId}`：查询成员公开资料和帖子、获赞统计。
- `GET /api/v1/members/{memberId}/posts`：分页查询该成员发布的公开帖子。
- `POST /api/v1/posts/{postId}/reports`：登录成员举报帖子，重复待处理举报按幂等处理。
- `PUT /api/v1/members/{memberId}/block`：登录成员屏蔽其他成员，重复屏蔽按幂等处理。

## 每日签到

签到接口均要求登录，数据按当前社区和当前成员隔离。同一天重复调用签到接口不会重复增加积分。

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/v1/check-ins/me` | Yes | 返回今日签到状态、连续天数、累计积分、本次积分及最近 7 天记录 |
| POST | `/api/v1/check-ins` | Yes | 完成今日签到；基础奖励 10 积分，连续第 7 天奖励 30 积分 |

## 图片上传

- `POST /api/v1/files/images`：上传单张图片，要求登录，使用 `multipart/form-data`，文件字段名固定为 `file`。
- 响应包含 `url`、`objectName`、`originalName`、`size`。小程序端统一通过 `frontend/api/modules/file.js` 调用，不允许页面直接拼上传地址或自行处理 Token。
- 发帖请求中的 `images` 最多 9 个，上传成功后传入图片 URL；后端按顺序写入 `forum_post_media`，帖子列表和详情的 `images` 字段会返回这些地址。

## 圈子、帖子、评论和点赞

公开查询接口支持匿名访问；请求携带有效 Bearer Token 时，帖子详情会同时返回当前成员的 `isLiked` 状态。写接口必须登录。

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/v1/circles` | Optional | 当前社区启用的圈子 |
| GET | `/api/v1/circles/{circleId}` | Optional | 圈子详情 |
| GET | `/api/v1/posts` | Optional | 公开帖子分页，支持 `circleId`、`keyword`、`sort=RECOMMENDED\|LATEST\|HOT`、`following`、`page`、`pageSize`；`following=true` 要求登录 |
| GET | `/api/v1/posts/{postId}` | Optional | 帖子详情并增加浏览量 |
| POST | `/api/v1/posts` | Yes | 发布帖子或保存草稿 |
| GET | `/api/v1/posts/{postId}/comments` | Optional | 评论分页 |
| POST | `/api/v1/posts/{postId}/comments` | Yes | 发表评论 |
| PUT | `/api/v1/posts/{postId}/like` | Yes | 幂等点赞 |
| DELETE | `/api/v1/posts/{postId}/like` | Yes | 幂等取消点赞 |

帖子分页响应使用统一分页结构：`records`、`total`、`pageSize`、`page`。发帖请求示例：

```json
{
  "circleId": "1",
  "postType": "ARTICLE",
  "title": "第一次分享",
  "content": "这里是帖子正文",
  "images": ["https://cdn.example.com/images/one.jpg"],
  "visibility": "PUBLIC",
  "saveAsDraft": false
}
```

当前 P0 默认直接发布普通帖子；`saveAsDraft=true` 时保存为草稿。审核流、回复、评论点赞、收藏和媒体资源将在后续迭代补齐。

## 圈子发现和活动

- `GET /api/v1/topics`：查询当前时间范围内状态为 `ACTIVE` 的社区话题活动，返回关联圈子、封面、简介及起止时间。
- 发现页的圈子搜索在客户端过滤已有圈子；热榜搜索复用帖子分页的 `keyword`；首页关注信息流使用帖子分页的 `following=true`。

## 用户通知

通知接口均要求登录，只允许读取和修改当前社区成员自己的通知。评论或点赞他人的帖子后，系统会为帖子作者生成通知；自己操作自己的帖子不会生成通知。

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/v1/notifications/summary` | Yes | 未读通知汇总，返回总数和互动、点赞、活动、系统四类数量 |
| GET | `/api/v1/notifications` | Yes | 通知分页，支持 `category=INTERACTION\|LIKE\|ACTIVITY\|SYSTEM`、`page`、`pageSize` |
| PUT | `/api/v1/notifications/{notificationId}/read` | Yes | 将当前成员的一条通知标记为已读 |
| PUT | `/api/v1/notifications/read-all` | Yes | 全部已读；可传 `category` 只处理当前分类 |

分类映射：评论、回复、关注属于 `INTERACTION`，点赞属于 `LIKE`，审核结果属于 `ACTIVITY`，平台通知属于 `SYSTEM`。通知分页继续使用统一的 `records`、`total`、`pageSize`、`page` 响应结构。

## 社区后台

社区后台只管理论坛业务，包括成员、角色、游戏、圈子、板块、帖子、评论、审核、举报、公告和审计。商业 SaaS 控制面不属于本仓库。

所有社区后台接口都要求 `Authorization: Bearer <accessToken>`，并按接口校验业务权限。首位注册用户自动成为 `OWNER`，`OWNER` 拥有当前社区全部业务权限。

### 圈子管理

圈子管理接口要求 `circle.manage` 权限：

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/admin/v1/circles` | 查询全部圈子，包含启用和停用状态 |
| POST | `/api/admin/v1/circles` | 新增圈子 |
| PUT | `/api/admin/v1/circles/{circleId}` | 修改圈子资料、图片、状态和排序 |
| DELETE | `/api/admin/v1/circles/{circleId}` | 删除空圈子；已有帖子时拒绝删除 |

新增和修改请求支持 `circleCode`、`circleName`、`iconUrl`、`coverUrl`、`description`、`joinMode`、`status`、`sortOrder`。后台网页先调用 `/api/v1/files/images` 上传图标或封面，再把响应中的 `url` 保存到圈子。

### 帖子管理

- `DELETE /api/admin/v1/posts/{postId}`：逻辑删除帖子，要求 `content.review` 权限；删除已发布帖子时同步回减所属圈子的帖子数量。

### 活动管理

活动管理复用用户端 `/api/v1/topics` 展示的社区话题活动，接口要求 `announcement.manage` 权限：

- `GET /api/admin/v1/activities`：查询全部活动。
- `POST /api/admin/v1/activities`：新增活动，支持所属圈子、封面、起止时间、状态和排序。
- `DELETE /api/admin/v1/activities/{activityId}`：逻辑删除活动。

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
