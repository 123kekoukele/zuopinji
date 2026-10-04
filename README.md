# zuopinji

作品集仓库，存放一些自己写的项目。

## 项目列表

| 项目 | 说明 | 技术栈 |
|------|------|--------|
| [thesis-topic-system](./thesis-topic-system) | 基于 SpringBoot 的毕业设计选题系统（开源发布包）<br>高校毕业设计 **选题 + 全流程管理**：学生端 / 管理端 / 后端 API | Spring Boot 2.2、MyBatis-Plus、MySQL 8、Redis、MinIO、Vue 3（Element Plus）、Nginx |

## 目录约定

每个项目放在独立子目录中，各自带自己的 `README.md` 与 `.gitignore`：

```
zuopinji/
├── README.md              <- 本文件（作品集索引）
├── .gitignore             <- 通用忽略规则
└── thesis-topic-system/   <- 项目一
    ├── README.md
    ├── source/            <- 后端 + 双前端完整源码
    ├── database/          <- MySQL 初始化脚本
    ├── windows-deploy/    <- Windows 本地部署脚本与文档
    ├── docs/              <- 功能说明与开发指南
    └── tools/             <- 打包工具说明
```

## 说明

各项目仅用于 **学习、二次开发、毕设参考**。