---
name: community-frontend
description: 学习、运行或修改 Community 项目的 uni-app 用户端，包括页面、接口接入、登录态和跨端图片上传。适用于 frontend 目录，不用于 Vue 3 管理后台或其他 uni-app 项目。
---

# Community 用户端前端

帮助使用者理解页面如何连接后端，并完成可验证的用户端改动。

## 定位项目

在当前工作区向上寻找同时具有 `AGENTS.md`、`frontend/pages.json` 和 `backend/pom.xml` 的项目根目录；技能安装位置不是项目根目录。找不到时请使用者提供 Community 仓库路径，不自动克隆或改动其他项目。先读项目 `AGENTS.md`、`README.md` 和 `frontend/README.md`，检查 Git 状态。

## 按任务读取

- 初次学习、启动、梳理页面或 API 调用：读 [学习与接入](references/learning.md)。
- 页面改动：检查 `frontend/pages.json`、对应 Vue 页面、调用的 `api/modules/` 方法；需要登录时再读 `utils/session.js`。
- 图片上传：读 `api/modules/file.js` 和对应页面；后端契约以 `docs/api/business-api.md` 及当前 Controller 为准。
- 新功能先确认接口已存在，缺失时说明前后端工作边界，不编造接口响应。

## 实施约束

本项目使用 Vue 2 与 uni-app，不把 Vue 3/浏览器专用写法直接搬入小程序。请求统一经 `api/http.js`；新增业务方法放 `api/modules/` 并从 `api/index.js` 导出。页面拿到的通常已是业务 data；帖子分页经模块归一化后为 rows，不能再按原始 Axios 响应解包。

登录态由 `utils/session.js` 管理，刷新后保留新令牌。图片走 `uploadImage`，字段名为 file；页面不得持有 OSS/COS 密钥。沿用 `tuniao-ui`、`components/` 和现有布局。小程序 AppID 由使用者配置，不沿用作者账号。

本地地址、密钥及生产配置遵守项目 AGENTS：提交脱敏，本地打包配置保留。不要把默认线上服务用于练习发帖、上传或删除；优先本地测试服务。

## 验证和交付

使用 HBuilderX 打开 frontend 并运行所需平台；明确 useMock / forumApiEnabled 状态。验证页面实际导航、加载/空/失败状态、登录过期与分页；上传在测试存储验证。H5 正常不等于小程序/APP 正常，分平台报告结果。只解释时不修改；要求修改时给出文件、结果和未验证项。

可用起始请求：“使用 $community-frontend 带我理解帖子列表调用链，先不修改。”或“使用 $community-frontend 接入已有的签到接口并验证 H5。”
