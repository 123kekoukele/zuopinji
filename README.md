# 基于 SpringBoot 的毕业设计选题系统（开源发布包）

高校毕业设计 **选题 + 全流程管理** 系统：学生端 / 管理端 / 后端 API。

## 目录结构

| 目录 | 说明 |
|------|------|
| `source/` | 完整源码（后端 + 双前端 Vue3），不含 node_modules |
| `database/` | MySQL 8 初始化脚本 |
| `windows-deploy/` | Windows 本地部署包（文档 + 脚本 + 可选预编译产物） |
| `docs/` | 功能说明与开发指南 |

## 技术栈

- 后端：Spring Boot 2.2、MyBatis-Plus、MySQL 8、Redis、MinIO
- 前端：Vue 3、Element Plus（用户端 + 管理端）
- 部署：Nginx 反向代理

## 快速开始

1. 导入 `database/hadluo-lvyou.sql`（MySQL 8，root 密码默认 123456）
2. 阅读 `windows-deploy/00-首次安装步骤.md`
3. 开发：进入 `source/`，执行 `windows-deploy/07-开发与构建/1-安装前端依赖.bat`

## 默认账号（演示数据，请在生产环境修改）

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 学生示例 | 110110 | 123456 |

## 配置说明

- 本地开发：`source/Back/hadluo-server/src/main/resources/application.yml`
- 生产/本机部署：`application-prod.yml`
- MinIO 默认：`http://127.0.0.1:9000`，桶 `hadluo-files`
- 通义千问 API（若使用 AI 功能）：设置环境变量 `DASHSCOPE_API_KEY`

## 开源说明

本发布包已移除个人服务器地址、远程运维脚本及第三方 API 密钥。
仅供 **学习、二次开发、毕设参考**，请勿直接未修改提交论文。

## 重新打包

在完整项目仓库中执行：

```bash
node scripts/build-opensource-release-package.js
```
