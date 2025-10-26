# API接口文档

## 📡 接口说明

### 基础信息
- **Base URL**: `http://你的IP:8080/`
- **数据格式**: JSON
- **认证方式**: JWT Token

### 统一响应格式
```json
{
  "code": 200,        // 状态码: 200成功, 其他失败
  "message": "成功",   // 提示信息
  "data": {}          // 返回数据
}
```

---

## 🔐 用户认证模块

### 用户登录
**接口地址**: `POST /api/auth/login`

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "user_id": 1,
      "username": "migaga_user",
      "phone": "13800138000",
      "nickname": "米嘎嘎用户"
    }
  }
}
```

### 用户注册
**接口地址**: `POST /api/auth/register`

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456",
  "password": "123456"
}
```

---

## 🛍️ 产品模块

### 获取产品列表
**接口地址**: `GET /api/products`

**请求参数**:
```
Query:
  category_id=1     // 分类ID (可选): 1-经典复刻, 2-潮流创味
  page=1            // 页码，默认1
  page_size=20      // 每页数量，默认20
```

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "product_id": 1,
      "product_name": "经典米花糖",
      "category_id": 1,
      "price": 19.90,
      "original_price": 29.90,
      "stock": 100,
      "sales": 520,
      "description": "传统工艺，经典口味",
      "images": [
        "https://example.com/product1_1.jpg",
        "https://example.com/product1_2.jpg"
      ],
      "is_on_sale": true
    }
  ]
}
```

### 获取产品详情
**接口地址**: `GET /api/products/{id}`

### 搜索产品
**接口地址**: `GET /api/products/search`

**请求参数**:
```
Query:
  keyword=米花糖
  page=1
```

---

## 🛒 购物车模块

### 添加到购物车
**接口地址**: `POST /api/cart/add`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "product_id": 1,
  "quantity": 2
}
```

### 获取购物车列表
**接口地址**: `GET /api/cart/list`

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "product_id": 1,
      "product_name": "经典米花糖",
      "price": 19.90,
      "quantity": 2,
      "images": ["..."],
      "stock": 100,
      "selected": true
    }
  ]
}
```

### 更新购物车商品
**接口地址**: `POST /api/cart/update`

**请求参数**:
```json
{
  "product_id": 1,
  "quantity": 3,
  "selected": true
}
```

### 删除购物车商品
**接口地址**: `POST /api/cart/delete`

**请求参数**:
```
Query: product_id=1
```

---

## 📦 订单模块

### 创建订单
**接口地址**: `POST /api/orders/create`

**请求参数**:
```json
{
  "address_id": 1,
  "items": [
    {
      "product_id": 1,
      "quantity": 2,
      "price": 19.90
    }
  ],
  "total_amount": 39.80,
  "remark": "备注信息"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "order_id": "202501011200001",
    "total_amount": 39.80,
    "status": 1,
    "create_time": "2025-01-01 12:00:00"
  }
}
```

### 获取订单列表
**接口地址**: `GET /api/orders/list`

**请求参数**:
```
Query:
  status=1   // 订单状态 (可选): 1-待付款, 2-待发货, 3-待收货, 4-已完成, 5-已取消
  page=1
```

### 获取订单详情
**接口地址**: `GET /api/orders/{orderId}`

### 取消订单
**接口地址**: `POST /api/orders/cancel`

**请求参数**:
```
Query: order_id=202501011200001
```

---

## 📍 地址管理模块

### 获取地址列表
**接口地址**: `GET /api/address/list`

**响应示例**:
```json
{
  "code": 200,
  "data": [
    {
      "address_id": 1,
      "user_id": 1,
      "receiver_name": "张三",
      "receiver_phone": "13800138000",
      "province": "四川省",
      "city": "乐山市",
      "district": "市中区",
      "detail_address": "XX街道XX号",
      "is_default": true
    }
  ]
}
```

### 添加地址
**接口地址**: `POST /api/address/add`

**请求参数**:
```json
{
  "receiver_name": "张三",
  "receiver_phone": "13800138000",
  "province": "四川省",
  "city": "乐山市",
  "district": "市中区",
  "detail_address": "XX街道XX号",
  "is_default": false
}
```

### 更新地址
**接口地址**: `POST /api/address/update`

### 删除地址
**接口地址**: `POST /api/address/delete`

**请求参数**:
```
Query: address_id=1
```

### 设置默认地址
**接口地址**: `POST /api/address/setDefault`

**请求参数**:
```
Query: address_id=1
```

---

## 🆘 错误码定义

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |
| 1001 | 手机号已注册 |
| 1002 | 验证码错误 |
| 1003 | 验证码已过期 |
| 1004 | 用户名或密码错误 |
| 1005 | Token无效或已过期 |
| 2001 | 产品不存在 |
| 2002 | 库存不足 |
| 3001 | 订单不存在 |
| 3002 | 订单状态异常 |

---

## 🔐 安全建议

1. **密码加密**: 使用BCrypt加密存储
2. **Token过期**: JWT Token建议7天过期
3. **请求限流**: 防止恶意请求
4. **SQL注入防护**: 使用参数化查询
5. **HTTPS**: 生产环境必须使用HTTPS

---

**文档版本**: v1.0  
**更新日期**: 2025-10-25  
**维护者**: 米嘎嘎开发团队