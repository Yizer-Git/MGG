-- ================================================
-- 米嘎嘎APP数据库设计
-- ================================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    phone VARCHAR(11) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    register_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_attempts INT DEFAULT 0 COMMENT '登录尝试次数',
    locked_until DATETIME COMMENT '锁定到期时间',
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 分类表
CREATE TABLE IF NOT EXISTS categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类表';

-- 3. 产品表
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    category_id INT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    description TEXT COMMENT '产品描述',
    images TEXT COMMENT '产品图片（多张用逗号分隔）',
    is_on_sale BOOLEAN DEFAULT TRUE COMMENT '是否上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_category (category_id),
    INDEX idx_sales (sales),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 4. 购物车表
CREATE TABLE IF NOT EXISTS cart_items (
    cart_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    user_id INT NOT NULL COMMENT '用户ID',
    product_id INT NOT NULL COMMENT '产品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    selected BOOLEAN DEFAULT TRUE COMMENT '是否选中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_product (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 5. 收货地址表
CREATE TABLE IF NOT EXISTS addresses (
    address_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    user_id INT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(11) NOT NULL COMMENT '收货人手机',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否默认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 6. 订单表
CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(32) PRIMARY KEY COMMENT '订单ID',
    user_id INT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    address_id INT COMMENT '收货地址ID',
    receiver_name VARCHAR(50) COMMENT '收货人姓名',
    receiver_phone VARCHAR(11) COMMENT '收货人手机',
    receiver_address VARCHAR(500) COMMENT '收货地址',
    status TINYINT DEFAULT 1 COMMENT '订单状态：1-待付款，2-待发货，3-待收货，4-已完成，5-已取消',
    remark VARCHAR(255) COMMENT '备注',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    finish_time DATETIME COMMENT '完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 7. 订单详情表
CREATE TABLE IF NOT EXISTS order_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单详情ID',
    order_id VARCHAR(32) NOT NULL COMMENT '订单ID',
    product_id INT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_image VARCHAR(255) COMMENT '产品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '购买价格',
    quantity INT NOT NULL COMMENT '购买数量',
    INDEX idx_order (order_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- ================================================
-- 初始化数据
-- ================================================

-- 插入分类数据
INSERT INTO categories (category_id, category_name, description, sort_order) VALUES
(1, '经典复刻', '传承传统工艺，经典口味', 1),
(2, '潮流创味', '创新口味，国潮新滋味', 2);

-- 插入测试产品数据
INSERT INTO products (product_name, category_id, price, original_price, stock, sales, description, images, is_on_sale) VALUES
-- 经典复刻系列
('经典原味米花糖', 1, 19.90, 29.90, 500, 1520, '传统手工工艺，保留乐山非遗米花糖的经典味道。精选优质糯米，经过十几道工序纯手工制作，口感香脆，甜而不腻。', 'https://picsum.photos/400/400?random=1', TRUE),
('传统芝麻米花糖', 1, 22.90, 32.90, 450, 980, '添加香浓黑芝麻，营养又美味。芝麻与糯米的完美结合，传统配方，非遗传承。', 'https://picsum.photos/400/400?random=2', TRUE),
('古法花生米花糖', 1, 24.90, 34.90, 400, 756, '精选颗粒饱满的花生仁，采用古法炒制，香气扑鼻。糯米与花生的黄金比例，口感层次丰富。', 'https://picsum.photos/400/400?random=3', TRUE),
('红糖米花糖', 1, 21.90, 31.90, 380, 642, '使用古法红糖熬制，营养健康。红糖的甘甜与糯米的清香完美融合，暖心暖胃。', 'https://picsum.photos/400/400?random=4', TRUE),
('核桃米花糖', 1, 26.90, 36.90, 350, 523, '精选优质核桃仁，营养价值高。核桃的香味与米花糖的甜蜜相得益彰，健脑益智。', 'https://picsum.photos/400/400?random=5', TRUE),

-- 潮流创味系列
('藤椒钵钵鸡味米花糖', 2, 28.90, 38.90, 300, 1876, '创新川味！将四川特色藤椒钵钵鸡的麻辣鲜香与米花糖完美结合，咸香微辣，让你体验前所未有的味蕾冲击。', 'https://picsum.photos/400/400?random=6', TRUE),
('白桃乌龙米花糖', 2, 29.90, 39.90, 280, 1234, '清新茶香遇见香甜水果。精选白桃果肉与乌龙茶叶，茶香悠长，果味清甜，给你夏日般的清爽体验。', 'https://picsum.photos/400/400?random=7', TRUE),
('抹茶红豆米花糖', 2, 27.90, 37.90, 320, 1089, '日式风味国潮演绎。京都抹茶的清苦遇见蜜红豆的香甜，绿白红三色交织，视觉味觉双重享受。', 'https://picsum.photos/400/400?random=8', TRUE),
('海盐焦糖米花糖', 2, 26.90, 36.90, 340, 876, '甜咸平衡的完美艺术。法式海盐与焦糖的经典组合，每一口都是甜蜜与咸香的碰撞。', 'https://picsum.photos/400/400?random=9', TRUE),
('玫瑰荔枝米花糖', 2, 30.90, 40.90, 260, 654, '浪漫花香与岭南佳果。食用玫瑰花瓣与新鲜荔枝肉，香气馥郁，甜蜜浪漫，少女心满满。', 'https://picsum.photos/400/400?random=10', TRUE);

-- 插入测试用户（密码为：123456，使用BCrypt加密）
INSERT INTO users (phone, password, nickname, avatar, status) VALUES
('13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EzHIDJeN6cBTwjOdfZkdWO', '米嘎嘎用户', 'https://api.dicebear.com/7.x/avataaars/svg?seed=1', 1),
('13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EzHIDJeN6cBTwjOdfZkdWO', '非遗爱好者', 'https://api.dicebear.com/7.x/avataaars/svg?seed=2', 1);

-- 插入测试地址
INSERT INTO addresses (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(1, '张三', '13800138000', '四川省', '乐山市', '市中区', '嘉州大道123号', TRUE),
(1, '李四', '13900139000', '四川省', '成都市', '武侯区', '天府大道456号', FALSE);

