# 米嘎嘎 - 非遗米花糖国潮化电商APP

<div align="center">

![Android](https://img.shields.io/badge/Android-24%2B-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)

**嘉州酥韵·国潮新滋味**

非遗米花糖国潮化品牌电商APP

</div>

---

## 📱 项目简介

米嘎嘎是一款专注于乐山非遗米花糖的**国潮电商APP**，致力于：
- 🎨 传统非遗技艺的现代化表达
- 🛒 打造年轻人喜爱的零食品牌  
- 🌾 产业反哺，助力乡村振兴

**本项目为"2025-2026学年挑战杯大学生创业计划竞赛"参赛作品**

---

## ✨ 核心特色

### 双系列产品
- **经典复刻**: 传承传统工艺，经典口味
- **潮流创味**: 创新口味（藤椒钵钵鸡、白桃乌龙等）

### 三位一体价值
- **文化价值**: 非遗技艺数字化传承
- **商业价值**: 全渠道品牌建设
- **社会价值**: 订单农业助农增收

---

## 🚀 快速开始

### 环境要求
- ✅ Android Studio Hedgehog 2023.1.1+
- ✅ JDK 17
- ✅ MySQL 8.0+
- ✅ Gradle 8.0+

### 5分钟快速运行

#### 第一步：启动后端 (3分钟)

**1. 创建数据库**
```bash
mysql -u root -p
CREATE DATABASE migaga_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

**2. 导入数据**
```bash
mysql -u root -p migaga_db < backend/src/main/resources/db/schema.sql
```

**3. 配置数据库**
编辑 `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/migaga_db
    username: root          # 改成你的数据库用户名
    password: 123456        # 改成你的数据库密码
```

**4. 启动后端**
```bash
cd backend
./gradlew bootRun
```

✅ 后端运行在: http://localhost:8080

#### 第二步：运行前端 (2分钟)

**1. 获取本机IP地址**
```bash
# Windows
ipconfig
# Mac/Linux  
ifconfig
```

**2. 配置API地址**
编辑 `app/build.gradle.kts`，找到第32行左右：
```kotlin
buildConfigField("String", "BASE_URL", "\"http://你的IP:8080/\"")
```

**3. 同步Gradle**
在Android Studio中点击 "Sync Project with Gradle Files"

**4. 运行APP**
连接设备或启动模拟器，点击 Run 按钮

#### 第三步：测试功能

**测试账号**:
- 手机号: `13800138000`
- 密码: `123456`

---

## 📦 当前进度

### ✅ 已完成（v1.2.0）
- [x] 项目架构搭建
- [x] 登录注册界面
- [x] 主界面框架（含4个Tab）
- [x] 网络层封装（Retrofit）
- [x] 数据模型定义
- [x] 国潮UI主题
- [x] **后端全栈开发**（购物车、订单、地址API）
- [x] **产品列表页**（网格布局、API调用）
- [x] **产品详情页**（信息展示、加入购物车）
- [x] **购物车布局和适配器**

### 🔨 开发中
- [ ] 购物车功能完善
- [ ] 订单确认页
- [ ] 订单列表页

### 📋 计划中
- [ ] 地址管理完善
- [ ] 非遗文化专区
- [ ] 支付功能
- [ ] 后台管理系统

**完成度**: 基础架构 100% | 后端API 100% | 核心功能 65%

---

## 🛠️ 技术栈

### 前端（Android）
- **语言**: Java 17
- **架构**: MVVM
- **网络**: Retrofit + OkHttp
- **图片**: Glide
- **UI**: Material Design 3
- **数据库**: Room (本地缓存)

### 后端
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **认证**: Spring Security + JWT
- **构建**: Gradle 8.0

---

## 🎯 核心功能

### ✅ 已实现

1. **用户认证**
   - 登录/注册
   - Token管理

2. **产品浏览**
   - 产品列表（网格布局）
   - 产品详情
   - 分类筛选
   - 搜索功能

3. **购物车（部分）**
   - 加入购物车API
   - 购物车UI

4. **完整的后端API**
   - 产品API
   - 购物车API
   - 订单API
   - 地址API

---

## 📡 API接口

### 基础信息
- **Base URL**: `http://你的IP:8080/`
- **数据格式**: JSON
- **认证方式**: JWT Token

### 主要接口

#### 用户认证
```http
POST /api/auth/login
{
  "phone": "13800138000",
  "password": "123456"
}
```

#### 产品管理
```http
GET /api/products?category_id=1&page=1&page_size=20
GET /api/products/{id}
GET /api/products/search?keyword=米花糖
```

#### 购物车
```http
POST /api/cart/add
GET /api/cart/list
POST /api/cart/update
POST /api/cart/delete
```

#### 订单管理
```http
POST /api/orders/create
GET /api/orders/list
GET /api/orders/{orderId}
POST /api/orders/cancel
```

#### 地址管理
```http
GET /api/address/list
POST /api/address/add
POST /api/address/update
POST /api/address/delete
```

---

## 📊 项目统计

- **代码行数**: ~5500行
- **Java文件**: 37个
- **Layout文件**: 8个
- **API接口**: 35+个
- **开发时间**: 2.5天
- **后端**: 15个新增文件
- **前端**: 6个新增文件

---

## 🐛 常见问题

### Q1: 后端启动失败
**A**: 检查MySQL是否启动，数据库配置是否正确

### Q2: APP无法连接后端
**A**: 
1. 确认后端已启动
2. 检查BASE_URL配置的IP是否正确
3. 确保手机和电脑在同一个WiFi网络
4. 关闭电脑防火墙试试

### Q3: Gradle同步失败
**A**: 
```bash
./gradlew clean build --refresh-dependencies
```

---

## 📚 测试数据

数据库已预置以下测试数据：

### 产品 (10个)
- **经典复刻系列** (5个): 经典原味、传统芝麻、古法花生、红糖、核桃
- **潮流创味系列** (5个): 藤椒钵钵鸡、白桃乌龙、抹茶红豆、海盐焦糖、玫瑰荔枝

### 用户 (2个)
| 手机号 | 密码 | 昵称 |
|--------|------|------|
| 13800138000 | 123456 | 米嘎嘎用户 |
| 13800138001 | 123456 | 非遗爱好者 |

---

## ⏳ 待完成工作

### 优先级 1 - 核心流程 (1-2天)
1. 完善购物车Fragment的API调用
2. 创建OrderConfirmActivity（订单确认页）
3. 创建AddressListActivity和AddressEditActivity

### 优先级 2 - 增强功能 (1周)
4. 完成订单列表和详情页
5. 完善个人中心功能
6. 添加搜索功能UI

### 优先级 3 - 特色功能 (2-3周)
7. 集成支付功能
8. 开发非遗文化专区
9. 性能优化和测试

---

## 🎓 学习要点

### 后端开发者
1. Spring Boot项目结构
2. JPA实体映射和关联
3. Service层事务管理
4. RESTful API设计规范

### 前端开发者
1. RecyclerView的使用
2. Retrofit网络请求
3. Glide图片加载
4. Fragment生命周期管理

---

## 👥 团队分工

| 角色 | 职责 |
|------|------|
| 产品经理 | 需求整理、项目协调 |
| Android开发 | APP开发、UI实现 |
| 后端开发 | API接口、数据库设计 |
| UI设计师 | 界面设计、品牌VI |
| 运营人员 | 内容运营、用户反馈 |

---

## 📄 License

本项目为"挑战杯"大学生创业计划竞赛项目，版权归团队所有。

---

## 🌟 致谢

感谢所有为"米嘎嘎"项目贡献力量的团队成员！

---

<div align="center">

**让非遗文化在国潮中焕发新生** 🎋

Made with ❤️ by 米嘎嘎团队

**版本**: v1.2.0  
**完成度**: 65%  
**最后更新**: 2025-10-25

</div>