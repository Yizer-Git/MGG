/* ==============================
   重新初始化数据库
   ============================== */
DROP DATABASE IF EXISTS migaga_db;
CREATE DATABASE migaga_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE migaga_db;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

/* ---------- 1. 用户 ---------- */
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    phone            VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    password         VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    nickname         VARCHAR(50) COMMENT '昵称',
    avatar           VARCHAR(255) COMMENT '头像URL',
    email            VARCHAR(100) COMMENT '邮箱',
    gender           TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    status           TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    register_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_login_time  DATETIME DEFAULT NULL COMMENT '最后登录时间',
    login_attempts   INT DEFAULT 0 COMMENT '登录尝试次数',
    locked_until     DATETIME DEFAULT NULL COMMENT '锁定到期时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/* ---------- 2. 分类 ---------- */
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_desc VARCHAR(200) COMMENT '分类描述',
    sort_order    INT DEFAULT 0 COMMENT '排序权重',
    is_active     TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类';

/* ---------- 3. 产品 ---------- */
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    product_id     INT AUTO_INCREMENT PRIMARY KEY COMMENT '产品ID',
    product_name   VARCHAR(100) NOT NULL COMMENT '产品名称',
    category_id    INT NOT NULL COMMENT '所属分类',
    price          DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    stock          INT DEFAULT 0 COMMENT '库存',
    sales          INT DEFAULT 0 COMMENT '销量',
    description    TEXT COMMENT '产品介绍',
    images         TEXT COMMENT '图片URL（逗号分隔）',
    is_on_sale     TINYINT(1) DEFAULT 1 COMMENT '是否在售',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_category (category_id),
    KEY idx_sales (sales),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品';

/* ---------- 4. 地址 ---------- */
DROP TABLE IF EXISTS addresses;
CREATE TABLE addresses (
    address_id      INT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id         INT NOT NULL COMMENT '用户ID',
    receiver_name   VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone  VARCHAR(11) NOT NULL COMMENT '手机号',
    province        VARCHAR(50) COMMENT '省份',
    city            VARCHAR(50) COMMENT '城市',
    district        VARCHAR(50) COMMENT '区/县',
    detail_address  VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default      TINYINT(1) DEFAULT 0 COMMENT '是否默认地址',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_address_user (user_id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址';

/* ---------- 5. 购物车 ---------- */
DROP TABLE IF EXISTS cart_items;
CREATE TABLE cart_items (
    cart_id     INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车记录ID',
    user_id     INT NOT NULL COMMENT '用户ID',
    product_id  INT NOT NULL COMMENT '产品ID',
    quantity    INT NOT NULL DEFAULT 1 COMMENT '数量',
    selected    TINYINT(1) DEFAULT 1 COMMENT '是否选中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_cart_user_product (user_id, product_id),
    CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users(user_id),
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

/* ---------- 6. 订单 ---------- */
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    order_id         VARCHAR(32) PRIMARY KEY COMMENT '订单ID',
    user_id          INT NOT NULL COMMENT '用户ID',
    total_amount     DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    address_id       INT DEFAULT NULL COMMENT '地址ID',
    receiver_name    VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    receiver_phone   VARCHAR(11) DEFAULT NULL COMMENT '收货人手机',
    receiver_address VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
    status           TINYINT DEFAULT 1 COMMENT '状态：1待付款 2待发货 3待收货 4已完成 5已取消',
    remark           VARCHAR(255) DEFAULT NULL COMMENT '备注',
    pay_time         DATETIME DEFAULT NULL COMMENT '支付时间',
    ship_time        DATETIME DEFAULT NULL COMMENT '发货时间',
    finish_time      DATETIME DEFAULT NULL COMMENT '完成时间',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_order_user   (user_id),
    KEY idx_order_status (status),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

/* ---------- 7. 订单详情 ---------- */
DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
    item_id       INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单商品ID',
    order_id      VARCHAR(32) NOT NULL COMMENT '订单ID',
    product_id    INT NOT NULL COMMENT '产品ID',
    product_name  VARCHAR(100) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    price         DECIMAL(10,2) NOT NULL COMMENT '成交价',
    quantity      INT NOT NULL COMMENT '数量',
    KEY idx_item_order (order_id),
    CONSTRAINT fk_item_order   FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品';

/* ---------- 8. 文化文章 ---------- */
DROP TABLE IF EXISTS articles;
CREATE TABLE articles (
    article_id   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title        VARCHAR(150) NOT NULL COMMENT '标题',
    summary      VARCHAR(300) DEFAULT NULL COMMENT '摘要',
    content      TEXT COMMENT '正文',
    cover_image  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    category     VARCHAR(50) DEFAULT NULL COMMENT '分类',
    author       VARCHAR(50) DEFAULT NULL COMMENT '作者',
    view_count   INT DEFAULT 0 COMMENT '浏览量',
    is_published TINYINT(1) DEFAULT 1 COMMENT '是否发布',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文化文章';

/* ---------- 9. 评价体系新增表 ---------- */
DROP TABLE IF EXISTS reviews;
CREATE TABLE reviews (
    review_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    user_id       INT NOT NULL COMMENT '用户ID',
    product_id    INT NOT NULL COMMENT '商品ID',
    order_id      VARCHAR(32) NOT NULL COMMENT '订单ID',
    rating        TINYINT NOT NULL COMMENT '评分1-5',
    content       TEXT COMMENT '评价内容',
    images        TEXT COMMENT '图片URL数组(JSON字符串)',
    like_count    INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_review_product (product_id),
    KEY idx_review_order   (order_id),
    CONSTRAINT fk_review_user    FOREIGN KEY (user_id)    REFERENCES users(user_id),
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(product_id),
    CONSTRAINT fk_review_order   FOREIGN KEY (order_id)   REFERENCES orders(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价';

DROP TABLE IF EXISTS review_comments;
CREATE TABLE review_comments (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    review_id  BIGINT NOT NULL COMMENT '评价ID',
    user_id    INT NOT NULL COMMENT '评论用户ID',
    content    VARCHAR(500) NOT NULL COMMENT '评论内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    KEY idx_comment_review (review_id),
    CONSTRAINT fk_comment_review FOREIGN KEY (review_id) REFERENCES reviews(review_id),
    CONSTRAINT fk_comment_user   FOREIGN KEY (user_id)   REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价评论';

DROP TABLE IF EXISTS review_likes;
CREATE TABLE review_likes (
    like_id    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    review_id  BIGINT NOT NULL COMMENT '评价ID',
    user_id    INT NOT NULL COMMENT '点赞用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_review_user (review_id, user_id),
    CONSTRAINT fk_like_review FOREIGN KEY (review_id) REFERENCES reviews(review_id),
    CONSTRAINT fk_like_user   FOREIGN KEY (user_id)   REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价点赞';

SET FOREIGN_KEY_CHECKS = 1;

/* ==============================
   初始化示例数据
   ============================== */

/* 用户（明文均为 123456） */
INSERT INTO users (user_id, phone, password, nickname, avatar, email, gender, status, register_time, last_login_time) VALUES
  (6001, '13800001001', '$2b$10$RyKA8Myr7t0qkjg1Rqh6..joWRVkuIb9xVq/bhCtniEC3WuaOE2ZW', '米小匠',   'https://cdn.mgg.com/avatars/artisan01.svg', 'mixiaojiang@mgg.com', 2, 1, '2024-02-15 10:05:00', '2024-03-08 08:46:00'),
  (6002, '13900001002', '$2b$10$mssKKDHCbyyGpNNcuZO57O4x3s2lqVdemWKZfW6tWXxEQk54bFjly', '糖纸策展人', 'https://cdn.mgg.com/avatars/artisan02.svg', 'curator@mgg.com',       1, 1, '2024-02-18 12:20:00', '2024-03-09 21:15:00'),
  (6003, '13700001003', '$2b$10$TInWgGPcALWn9Sx61tSVieVkPGqLqJmRSdETJUtCm1sA1hwRl.SOG', '国潮收藏家', 'https://cdn.mgg.com/avatars/artisan03.svg', 'collector@mgg.com',     0, 1, '2024-02-22 09:33:00', '2024-03-07 19:38:00');

/* 分类 */
INSERT INTO categories (category_id, category_name, category_desc, sort_order, is_active, create_time) VALUES
  (101, '经典复刻', '传承非遗手艺，还原米花糖原味记忆', 1, 1, '2024-02-01 09:00:00'),
  (102, '匠心焕新', '花果茶香与米花糖灵感碰撞',       2, 1, '2024-02-08 09:00:00'),
  (103, '城市限定', '限量口味，记录城市风味',           3, 1, '2024-02-12 09:00:00'),
  (104, '茶点伴侣', '下午茶与礼赠的轻盈搭配',           4, 1, '2024-02-16 09:00:00');

/* 产品 */
INSERT INTO products (product_id, product_name, category_id, price, original_price, stock, sales, description, images, is_on_sale, create_time) VALUES
  (1001, '手作原味米花糖礼盒', 101, 58.00, 68.00, 320, 1680, '严选四川糯米与麦芽糖手工膨化，礼盒含12小包。', 'https://cdn.mgg.com/products/p1001-1.jpg,https://cdn.mgg.com/products/p1001-2.jpg', 1, '2024-02-01 10:30:00'),
  (1002, '黑芝麻酥心米花糖',     101, 36.80, 42.00, 280, 940, '慢火翻炒黑芝麻与麦芽糖，外层包裹酥心米花。', 'https://cdn.mgg.com/products/p1002-1.jpg,https://cdn.mgg.com/products/p1002-2.jpg', 1, '2024-02-05 11:00:00'),
  (1003, '桂花糯米米花糖',       102, 33.00, 39.00, 260, 812, '当季金桂低温冷萃，香甜不腻，搭配热茶最佳。', 'https://cdn.mgg.com/products/p1003-1.jpg,https://cdn.mgg.com/products/p1003-2.jpg', 1, '2024-02-10 11:10:00'),
  (1004, '藤椒麻辣米花糖',       103, 32.50, 38.00, 240, 1326,'藤椒与辣椒油锁香，带来咸香麻辣体验。',         'https://cdn.mgg.com/products/p1004-1.jpg,https://cdn.mgg.com/products/p1004-2.jpg', 1, '2024-02-16 15:25:00'),
  (1005, '金桂乌龙米花糖棒',     104, 42.80, 49.00, 210, 688, '乌龙茶与桂花糖浆双层包裹，酥脆且回甘。',       'https://cdn.mgg.com/products/p1005-1.jpg,https://cdn.mgg.com/products/p1005-2.jpg', 1, '2024-02-20 09:50:00'),
  (1006, '山茶花海盐米花糖',     102, 34.20, 39.80, 230, 572, '峨眉山山茶花瓣与法国海盐的甜咸平衡。',         'https://cdn.mgg.com/products/p1006-1.jpg,https://cdn.mgg.com/products/p1006-2.jpg', 1, '2024-02-26 14:18:00'),
  (1007, '江湖酥辣双拼礼盒',     103, 68.00, 79.00, 180, 486, '藤椒麻辣+豆豉酥香双拼组合，聚会分享神器。',   'https://cdn.mgg.com/products/p1007-1.jpg,https://cdn.mgg.com/products/p1007-2.jpg', 1, '2024-03-01 08:20:00'),
  (1008, '樱花白桃米花糖',       104, 29.90, 36.00, 260, 754, '春季限定，樱花花瓣与白桃果泥低温冻干。',       'https://cdn.mgg.com/products/p1008-1.jpg,https://cdn.mgg.com/products/p1008-2.jpg', 1, '2024-03-05 10:42:00');

/* 地址 */
INSERT INTO addresses (address_id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, create_time) VALUES
  (7001, 6001, '李晴', '13800001001', '四川省', '成都市', '锦江区', '红星路三段99号国潮里A座18F', 1, '2024-02-21 10:12:00'),
  (7002, 6001, '李晴', '13800001001', '四川省', '成都市', '武侯区', '科华北路92号国潮体验店',      0, '2024-02-26 17:45:00'),
  (7003, 6002, '周宁', '13900001002', '北京市', '朝阳区', '三里屯街道', '工体北路8号潮流市集2层',      1, '2024-02-24 13:28:00'),
  (7004, 6003, '陈意', '13700001003', '上海市', '黄浦区', '外滩街道', '中山东一路18号观景公寓906',    1, '2024-02-27 09:58:00');

/* 购物车 */
INSERT INTO cart_items (cart_id, user_id, product_id, quantity, selected, create_time, update_time) VALUES
  (8001, 6001, 1003, 1, 1, '2024-03-09 11:10:00', '2024-03-09 11:10:00'),
  (8002, 6001, 1006, 2, 1, '2024-03-09 11:12:00', '2024-03-09 11:12:00'),
  (8003, 6002, 1007, 1, 1, '2024-03-08 19:35:00', '2024-03-08 19:35:00'),
  (8004, 6003, 1008, 3, 0, '2024-03-07 08:45:00', '2024-03-07 08:45:00');

/* 订单 */
INSERT INTO orders (order_id, user_id, total_amount, address_id, receiver_name, receiver_phone, receiver_address, status, remark, pay_time, ship_time, finish_time, create_time) VALUES
  ('MGG202403010001', 6001, 158.80, 7001, '李晴', '13800001001', '四川省成都市锦江区红星路三段99号国潮里A座18F', 2, '春季尝鲜组合', '2024-03-01 10:28:00', NULL, NULL, '2024-03-01 10:20:00'),
  ('MGG202403070015', 6002, 165.50, 7003, '周宁', '13900001002', '北京市朝阳区工体北路8号潮流市集2层',            3, NULL,           '2024-03-07 15:42:00', '2024-03-07 16:15:00', NULL, '2024-03-07 15:35:00'),
  ('MGG202402221008', 6003, 129.60, 7004, '陈意', '13700001003', '上海市黄浦区中山东一路18号观景公寓906',            4, '礼盒送朋友',   '2024-02-22 13:05:00', '2024-02-23 09:20:00', '2024-02-27 18:32:00', '2024-02-22 12:58:00');

/* 订单详情 */
INSERT INTO order_items (item_id, order_id, product_id, product_name, product_image, price, quantity) VALUES
  (9001, 'MGG202403010001', 1001, '手作原味米花糖礼盒', 'https://cdn.mgg.com/products/p1001-1.jpg', 58.00, 2),
  (9002, 'MGG202403010001', 1005, '金桂乌龙米花糖棒',   'https://cdn.mgg.com/products/p1005-1.jpg', 42.80, 1),
  (9003, 'MGG202403070015', 1004, '藤椒麻辣米花糖',     'https://cdn.mgg.com/products/p1004-1.jpg', 32.50, 3),
  (9004, 'MGG202403070015', 1007, '江湖酥辣双拼礼盒',   'https://cdn.mgg.com/products/p1007-1.jpg', 68.00, 1),
  (9005, 'MGG202402221008', 1002, '黑芝麻酥心米花糖',   'https://cdn.mgg.com/products/p1002-1.jpg', 36.80, 1),
  (9006, 'MGG202402221008', 1003, '桂花糯米米花糖',     'https://cdn.mgg.com/products/p1003-1.jpg', 33.00, 1),
  (9007, 'MGG202402221008', 1008, '樱花白桃米花糖',     'https://cdn.mgg.com/products/p1008-1.jpg', 29.90, 2);

/* 文章 */
INSERT INTO articles (article_id, title, summary, content, cover_image, category, author, view_count, is_published, created_at, updated_at) VALUES
  (201, '米嘎嘎·匠心作坊的一天', '走进乐山非遗米花糖作坊，感受匠人的手作韵律。', '<h2>作坊日常</h2><p>凌晨四点，炊烟与麦芽香气混合在老作坊里。</p>', 'https://cdn.mgg.com/articles/a201.jpg', '品牌故事', '唐远', 362, 1, '2024-03-01 09:30:00', '2024-03-06 11:20:00'),
  (202, '第四代传承人的工具箱',   '年轻匠人带着爷爷留下的工具，守护米花糖核心技艺。', '<h2>工具与记忆</h2><p>竹篾筛、铜锅、麻绳，这些看似普通的工具承载着家族味觉密码。</p>', 'https://cdn.mgg.com/articles/a202.jpg', '品牌故事', '赵衡', 248, 1, '2024-02-26 10:15:00', '2024-03-03 18:05:00'),
  (203, '古法熬糖的火候暗语',     '非遗老师傅分享三段火的细腻技巧。', '<h2>火候配方</h2><p>第一段猛火为膨化打基础，中火稳色，小火收香。</p>', 'https://cdn.mgg.com/articles/a203.jpg', '非遗技艺', '苏明', 305, 1, '2024-02-20 08:55:00', '2024-03-05 09:42:00');

/* 评价（示例晒单） */
INSERT INTO reviews (review_id, user_id, product_id, order_id, rating, content, images, like_count, comment_count, created_at) VALUES
  (3001, 6003, 1002, 'MGG202402221008', 5,
   '黑芝麻颗粒很香，甜度刚好，搭配热茶特别舒服。包装也很好看！',
   '["https://cdn.mgg.com/reviews/r3001-1.jpg","https://cdn.mgg.com/reviews/r3001-2.jpg"]',
   6, 2, '2024-02-28 09:15:00'),
  (3002, 6002, 1004, 'MGG202403070015', 4,
   '藤椒麻味很带劲，晚上追剧来一包完全停不下来，就是有点上火要配茶。',
   '["https://cdn.mgg.com/reviews/r3002-1.jpg"]',
   9, 1, '2024-03-08 21:10:00'),
  (3003, 6001, 1003, 'MGG202403010001', 5,
   '桂花香味非常自然，口感酥脆不粘牙，老人小孩都喜欢。',
   '[]', 3, 0, '2024-03-05 11:22:00');

/* 评价评论 */
INSERT INTO review_comments (comment_id, review_id, user_id, content, created_at) VALUES
  (4001, 3001, 6002, '确实好吃，我都当早餐零食了！', '2024-02-28 11:05:00'),
  (4002, 3001, 6001, '谢谢推荐，下次买礼盒送朋友~',   '2024-02-28 11:32:00'),
  (4003, 3002, 6003, '配凉茶刚好，晚上别吃太多哈哈。', '2024-03-08 22:08:00');

/* 评价点赞 */
INSERT INTO review_likes (like_id, review_id, user_id, created_at) VALUES
  (5001, 3001, 6001, '2024-02-28 10:12:00'),
  (5002, 3001, 6002, '2024-02-28 10:15:00'),
  (5003, 3001, 6003, '2024-02-28 10:16:00'),
  (5004, 3002, 6001, '2024-03-08 21:20:00'),
  (5005, 3002, 6002, '2024-03-08 21:25:00'),
  (5006, 3002, 6003, '2024-03-08 21:28:00');

ALTER TABLE users      
