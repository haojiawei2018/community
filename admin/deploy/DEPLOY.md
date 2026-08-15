# 部署说明

1. 将压缩包中的 `index.html`、`assets` 解压到网站根目录下的 `admin` 文件夹。
2. 参考 `nginx-site.conf.example` 配置站点，并把 `root` 修改为包含 `admin` 文件夹的网站根目录。
3. `/api/` 已配置从 Nginx 转发到本机 Java 服务 `http://127.0.0.1:10003`，后端接口路径不变。
4. 执行 `nginx -t` 检查配置后重载 Nginx。

前端固定部署路径为 `/admin/`，并使用 History 路由，必须保留 `try_files $uri $uri/ /admin/index.html;`。
