# 配置与部署说明

## 1. 基础环境

- 后端：JDK 8、Maven 3.6+、MySQL 5.7/8.0、Redis
- 用户端：HBuilderX 5.x（uni-app Vue 2）
- 管理后台：Node.js 20.19+ 或 22.12+、pnpm

## 2. 数据库初始化

新安装推荐创建空数据库 `community`，然后一次性执行：

```text
sql/community_business_full.sql
```

该文件是当前完整结构。`V*.sql` 是从旧版本逐步升级使用的增量脚本，`R*.sql` 是对应回滚脚本；新数据库不要在完整脚本后重复执行同内容的增量脚本。

## 3. 后端环境变量

开发配置位于 `backend/hope-api/src/main/resources/application-dev.yml`，生产配置位于 `application-prod.yml`。不要把密码或密钥写入 Git。

| 环境变量 | 必填 | 说明 |
| --- | --- | --- |
| `DB_URL` | 是 | MySQL JDBC 地址 |
| `DB_USERNAME` | 是 | 数据库账号 |
| `DB_PASSWORD` | 是 | 数据库密码 |
| `REDIS_HOST` | 是 | Redis 地址，默认 `localhost` |
| `REDIS_PORT` | 否 | Redis 端口，默认 `6379` |
| `REDIS_PASSWORD` | 按环境 | Redis 密码 |
| `REDIS_DATABASE` | 否 | Redis DB，默认 `0` |
| `COMMUNITY_ID` | 否 | 单社区 ID，默认 `1` |
| `COMMUNITY_CODE` | 否 | 社区编码，默认 `default` |
| `COMMUNITY_NAME` | 否 | 社区名称 |
| `COMMUNITY_LOGO_URL` | 否 | 社区 Logo 地址 |
| `COMMUNITY_DEFAULT_AVATAR_URL` | 否 | 默认头像地址 |
| `COMMUNITY_ACCESS_TOKEN_SECRET` | 生产必填 | JWT 密钥，至少使用 32 位随机值 |

PowerShell 开发示例：

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/community?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='请填写本机数据库密码'
$env:COMMUNITY_ACCESS_TOKEN_SECRET='请替换为随机长密钥'
```

## 4. 阿里云 OSS

图片上传接口为 `POST /api/v1/files/images`。需要创建 OSS Bucket，并配置 Bucket 跨域规则、读写权限或自定义 CDN 域名。

| 环境变量 | 说明 |
| --- | --- |
| `ALIYUN_OSS_ENDPOINT` | 例如 `oss-cn-chengdu.aliyuncs.com` |
| `ALIYUN_OSS_ACCESS_KEY_ID` | RAM 用户 AccessKey ID |
| `ALIYUN_OSS_ACCESS_KEY_SECRET` | RAM 用户 AccessKey Secret |
| `ALIYUN_OSS_BUCKET_NAME` | Bucket 名称 |
| `ALIYUN_OSS_PUBLIC_DOMAIN` | 可选，自定义 CDN/公开访问域名 |
| `ALIYUN_OSS_OBJECT_PREFIX` | 对象目录，默认 `images` |
| `ALIYUN_OSS_MAX_IMAGE_SIZE` | 单图上限，默认 `10MB` |

生产环境建议使用最小权限 RAM 用户或 ECS/容器实例角色，不要使用主账号 AccessKey。已经暴露过的 AccessKey 必须在阿里云控制台禁用并轮换。

## 5. 用户端配置

编辑 `frontend/config/env.js`：

- `baseURL`：当前测试服务器为 `http://42.193.104.179:10003`；正式发布应替换为后端 HTTPS 域名
- `communityCode`：开源单社区版保持 `default`
- `useMock`：正常联调保持 `false`
- `forumApiEnabled`：正常运行保持 `true`

微信小程序还需在 `frontend/manifest.json` 配置 AppID，并在微信公众平台配置 request/uploadFile/downloadFile 合法域名。正式域名必须使用 HTTPS。

H5 部署在 `/h5/`，由 Nginx 托管静态资源，并直接请求 `http://42.193.104.179:10003`；后端需放行 H5 来源和 CORS 预检，静态站点配置见 `frontend/deploy/nginx-h5.conf.example`。APP 发行时在 HBuilderX 中配置 Android 包名、iOS Bundle ID、签名证书、图标、启动图和隐私权限。

## 6. 管理后台配置

开发环境通过 `admin/vite.config.ts` 的代理连接后端，默认目标为 `http://42.193.104.179:10003`，可用 `VITE_API_TARGET` 覆盖：

```powershell
$env:VITE_API_TARGET='http://42.193.104.179:10003'
pnpm dev
```

生产包默认直接请求 `http://42.193.104.179:10003`，可在构建前通过 `VITE_API_BASE_URL` 覆盖。正式环境建议由 Nginx 将后台 `/api` 反向代理到后端，并启用 HTTPS。

## 7. 启动与检查

```bash
cd backend
mvn -pl hope-api -am test
mvn -pl hope-api -am spring-boot:run -Dspring-boot.run.profiles=dev
```

```bash
cd admin
pnpm install
pnpm build
```

用户端使用 HBuilderX 打开 `frontend`，分别运行或发行到 H5、微信小程序、Android、iOS。
