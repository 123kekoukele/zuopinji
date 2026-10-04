# 部署说明（Nginx 反代 + 端口说明）

## 为什么管理端请求会发到 `:8081`？

Axios 的 `baseURL` 为相对路径 `/hadluo-xt` 时，浏览器会使用**当前页面的协议 + 域名 + 端口**拼接接口地址。

若管理端页面在 `http://localhost:8081/` 打开，接口即为 `http://localhost:8081/hadluo-xt/...`。

## 推荐做法

生产环境建议用户端、管理端都通过 **80 端口 Nginx** 访问（如 `/` 与 `/admin/`），并在同一 `server` 块中配置：

```nginx
location /hadluo-xt/ {
    proxy_pass http://127.0.0.1:8080/hadluo-xt/;
    proxy_set_header Host $host;
    client_max_body_size 25m;
}
```

每个 `listen` 端口都需要单独配置 `/hadluo-xt/` 反代，否则会出现 404。

## 前端

`.env.production` 中 `VUE_APP_BASE_API=/hadluo-xt`，`VUE_APP_BASE_API_URL` 留空即可随当前域名自动适配。
