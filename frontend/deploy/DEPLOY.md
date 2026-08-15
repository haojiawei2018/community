# H5 部署说明

1. 将 H5 压缩包内容解压到站点根目录下的 `h5` 文件夹。
2. 把 `nginx-h5.conf.example` 中的 `root` 改为服务器实际站点根目录。
3. 将 `/h5` 的两个 `location` 配置加入当前站点的 `server` 块。
4. 执行 `nginx -t`，确认通过后重新加载 Nginx。
5. 访问 `http://42.193.104.179/h5/`，并确认页面可以直接请求 `http://42.193.104.179:10003/api/v1/bootstrap`。

H5 直接请求 `http://42.193.104.179:10003/api/v1/**`。后端必须允许来源 `http://42.193.104.179`，并放行 `OPTIONS`、`Authorization`、`X-Tenant-Code` 和 `Content-Type`；当前服务器已验证通过。
