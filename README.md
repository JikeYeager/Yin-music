# HarmonyStream - 智能音乐流媒体平台

实习期间的大型音乐项目，包含两个角色：用户和管理员

## 🎵 项目简介

HarmonyStream - 智能音乐流媒体平台是一个基于 Spring Boot + Vue.js 的全栈音乐流媒体平台，提供完整的音乐播放、歌单管理、用户社区等功能。项目采用前后端分离架构，包含用户端和管理端两个独立系统。

## 🚀 技术栈

**后端技术**  
Spring Boot 2.6.2 - 核心框架  
MyBatis Plus 3.5.1 - ORM框架  
MySQL 8.0 - 数据库  
Maven - 项目构建  
Java 17 - 开发语言  

**前端技术**  
Vue 3 - 前端框架  
Element Plus - UI组件库  
TypeScript - 类型支持  
Vue Router - 路由管理  
Vuex - 状态管理  
Axios - HTTP客户端  

## 📋 功能特性  

**核心功能**

- 🎶 音乐在线播放与歌词同步  
- 📁 歌单创建与管理  
- 🔍 智能搜索（歌曲、歌手、专辑）  
- 👥 用户注册登录系统  
- 📧 邮箱验证与密码重置  
- ❤️ 歌曲收藏与喜欢功能  
- 💬 评论与互动系统  
- ⭐ 评分系统

**管理功能**

- 🎤 歌手信息管理  
- 🎵 歌曲上传与管理  
- 📋 歌单内容管理  
- 👤 用户权限管理  
- 📊 数据统计与分析  
- 🏗️ 项目结构  

plainText

```
YINYUE-20250616/
├── src/                    # 后端Java源码
│   ├── main/java/com/ly/yin/
│   │   ├── controller/     # 控制器层
│   │   ├── service/        # 服务层
│   │   ├── mapper/         # 数据访问层
│   │   ├── domain/         # 实体类
│   │   ├── vo/            # 视图对象
│   │   ├── common/         # 通用工具
│   │   └── config/         # 配置类
├── UI/                     # 前端项目
│   ├── music-client/       # 用户端Vue项目
│   └── music-manage/       # 管理端Vue项目
├── img/                    # 静态资源
│   ├── avatorImages/       # 用户头像
│   ├── singerPic/          # 歌手图片
│   ├── songListPic/        # 歌单封面
│   └── songPic/            # 歌曲封面
└── lyrics/                 # 歌词文件
```

## 🎯 特色功能

多表关联查询 - 支持复杂的音乐数据关联查询
文件上传管理 - 支持图片、音频文件的上传和存储
邮件服务集成 - 完整的邮箱验证和通知系统
缓存优化 - 使用Caffeine缓存提升性能
跨域支持 - 完善的CORS配置
响应式设计 - 适配多种设备屏幕

## 🔧 快速开始

后端启动

```bash
mvn clean install
mvn spring-boot:run
```

前端启动

```bash
# 用户端
cd UI/music-client
npm install
npm run serve
```

# 管理端  

```
cd UI/music-manage
npm install
npm run serve
```

## 📝 数据库配置

项目使用MySQL数据库，配置信息详见： src/main/resources/application.yml

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (git checkout -b feature/AmazingFeature)
3. 提交更改 (git commit -m 'Add some AmazingFeature')
4. 推送到分支 (git push origin feature/AmazingFeature)
5. 开启Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

## ⭐ 如果这个项目对您有帮助，请给它一个Star！

