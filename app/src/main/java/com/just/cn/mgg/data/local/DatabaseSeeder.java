package com.just.cn.mgg.data.local;

import com.just.cn.mgg.data.local.entity.AddressEntity;
import com.just.cn.mgg.data.local.entity.ArticleEntity;
import com.just.cn.mgg.data.local.entity.CartItemEntity;
import com.just.cn.mgg.data.local.entity.CategoryEntity;
import com.just.cn.mgg.data.local.entity.OrderEntity;
import com.just.cn.mgg.data.local.entity.OrderItemEntity;
import com.just.cn.mgg.data.local.entity.ProductEntity;
import com.just.cn.mgg.data.local.entity.ReviewCommentEntity;
import com.just.cn.mgg.data.local.entity.ReviewEntity;
import com.just.cn.mgg.data.local.entity.ReviewLikeEntity;
import com.just.cn.mgg.data.local.entity.UserEntity;

import java.util.Arrays;
import java.util.List;

final class DatabaseSeeder {

    private DatabaseSeeder() {
    }

    static void seed(AppDatabase db) {
        if (db == null) {
            return;
        }

        seedUsers(db);
        seedCategories(db);
        seedProducts(db);
        seedArticles(db);
        seedAddresses(db);
        seedCartItems(db);
        seedOrders(db);
        seedReviews(db);
    }

    private static void seedUsers(AppDatabase db) {
        db.userDao().clearAll();

        UserEntity u1 = new UserEntity();
        u1.setUserId(6001);
        u1.setPhone("13800001001");
        u1.setPassword("123456");
        u1.setNickname("米小匠");
        u1.setAvatar("https://cdn.mgg.com/avatars/artisan01.svg");
        u1.setEmail("mixiaojiang@mgg.com");
        u1.setGender(2);
        u1.setStatus(1);
        u1.setRegisterTime("2024-02-15 10:05:00");
        u1.setLastLoginTime("2024-03-08 08:46:00");

        UserEntity u2 = new UserEntity();
        u2.setUserId(6002);
        u2.setPhone("13900001002");
        u2.setPassword("123456");
        u2.setNickname("潮玩匠");
        u2.setAvatar("https://cdn.mgg.com/avatars/artisan02.svg");
        u2.setEmail("chaowanjiang@mgg.com");
        u2.setGender(1);
        u2.setStatus(1);
        u2.setRegisterTime("2024-02-12 14:20:00");
        u2.setLastLoginTime("2024-03-07 21:12:00");

        UserEntity u3 = new UserEntity();
        u3.setUserId(6003);
        u3.setPhone("13700001003");
        u3.setPassword("123456");
        u3.setNickname("米花茶客");
        u3.setAvatar("https://cdn.mgg.com/avatars/artisan03.svg");
        u3.setEmail("mihua@mgg.com");
        u3.setGender(2);
        u3.setStatus(1);
        u3.setRegisterTime("2024-02-18 09:18:00");
        u3.setLastLoginTime("2024-03-06 18:56:00");

        db.userDao().insertAll(Arrays.asList(u1, u2, u3));
    }

    private static void seedCategories(AppDatabase db) {
        db.categoryDao().clearAll();

        CategoryEntity c1 = createCategory(101, "经典复刻", "传承非遗手艺，还原米花糖原味记忆", 1, true, "2024-02-01 09:00:00");
        CategoryEntity c2 = createCategory(102, "匠心焕新", "花果茶香与米花糖灵感碰撞", 2, true, "2024-02-08 09:00:00");
        CategoryEntity c3 = createCategory(103, "城市限定", "限量口味，记录城市风味", 3, true, "2024-02-12 09:00:00");
        CategoryEntity c4 = createCategory(104, "茶点伴侣", "下午茶与礼赠的轻盈搭配", 4, true, "2024-02-16 09:00:00");

        db.categoryDao().insertAll(Arrays.asList(c1, c2, c3, c4));
    }

    private static CategoryEntity createCategory(int id, String name, String desc, int sort, boolean active, String createTime) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryId(id);
        entity.setCategoryName(name);
        entity.setCategoryDesc(desc);
        entity.setSortOrder(sort);
        entity.setActive(active);
        entity.setCreateTime(createTime);
        return entity;
    }

    private static void seedProducts(AppDatabase db) {
        db.productDao().clearAll();

        ProductEntity p1 = createProduct(1001, "手作原味米花糖礼盒", 101, 58.00, 68.00, 320, 1680,
                "严选四川糯米与麦芽糖手工膨化，礼盒含12小包。",
                "https://cdn.mgg.com/products/p1001-1.jpg,https://cdn.mgg.com/products/p1001-2.jpg",
                true, "2024-02-01 10:30:00");
        ProductEntity p2 = createProduct(1002, "黑芝麻酥心米花糖", 101, 36.80, 42.00, 280, 940,
                "慢火翻炒黑芝麻与麦芽糖，外层包裹酥心米花。",
                "https://cdn.mgg.com/products/p1002-1.jpg,https://cdn.mgg.com/products/p1002-2.jpg",
                true, "2024-02-05 11:00:00");
        ProductEntity p3 = createProduct(1003, "桂花糯米米花糖", 102, 33.00, 39.00, 260, 812,
                "当季金桂低温冷萃，香甜不腻，搭配热茶最佳。",
                "https://cdn.mgg.com/products/p1003-1.jpg,https://cdn.mgg.com/products/p1003-2.jpg",
                true, "2024-02-10 11:10:00");
        ProductEntity p4 = createProduct(1004, "藤椒麻辣米花糖", 103, 32.50, 38.00, 240, 1326,
                "藤椒与辣椒油锁香，带来咸香麻辣体验。",
                "https://cdn.mgg.com/products/p1004-1.jpg,https://cdn.mgg.com/products/p1004-2.jpg",
                true, "2024-02-16 15:25:00");
        ProductEntity p5 = createProduct(1005, "金桂乌龙米花糖棒", 104, 42.80, 49.00, 210, 688,
                "乌龙茶与桂花糖浆双层包裹，酥脆且回甘。",
                "https://cdn.mgg.com/products/p1005-1.jpg,https://cdn.mgg.com/products/p1005-2.jpg",
                true, "2024-02-20 09:50:00");
        ProductEntity p6 = createProduct(1006, "山茶花海盐米花糖", 102, 34.20, 39.80, 230, 572,
                "峨眉山山茶花瓣与法国海盐的甜咸平衡。",
                "https://cdn.mgg.com/products/p1006-1.jpg,https://cdn.mgg.com/products/p1006-2.jpg",
                true, "2024-02-26 14:18:00");
        ProductEntity p7 = createProduct(1007, "江湖酥辣双拼礼盒", 103, 68.00, 79.00, 180, 486,
                "藤椒麻辣+豆豉酥香双拼组合，聚会分享神器。",
                "https://cdn.mgg.com/products/p1007-1.jpg,https://cdn.mgg.com/products/p1007-2.jpg",
                true, "2024-03-01 08:20:00");
        ProductEntity p8 = createProduct(1008, "樱花白桃米花糖", 104, 29.90, 36.00, 260, 754,
                "春季限定，樱花花瓣与白桃果泥低温冻干。",
                "https://cdn.mgg.com/products/p1008-1.jpg,https://cdn.mgg.com/products/p1008-2.jpg",
                true, "2024-03-05 10:42:00");

        db.productDao().insertAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    private static ProductEntity createProduct(int id, String name, int category, double price,
                                              Double originalPrice, int stock, int sales,
                                              String desc, String images, boolean onSale, String createTime) {
        ProductEntity entity = new ProductEntity();
        entity.setProductId(id);
        entity.setProductName(name);
        entity.setCategoryId(category);
        entity.setPrice(price);
        entity.setOriginalPrice(originalPrice);
        entity.setStock(stock);
        entity.setSales(sales);
        entity.setDescription(desc);
        entity.setImages(images);
        entity.setOnSale(onSale);
        entity.setCreateTime(createTime);
        return entity;
    }

    private static void seedArticles(AppDatabase db) {
        db.articleDao().clearAll();

        ArticleEntity a1 = createArticle(
                201,
                "米嘎嘎·匠心作坊的一天",
                "走进乐山非遗米花糖作坊，感受匠人的手作韵律。",
                "<h2>作坊日常</h2><p>凌晨四点，炊烟与麦芽香气混合在老作坊里。</p>",
                "https://cdn.mgg.com/articles/a201.jpg",
                "品牌故事",
                "唐远",
                362,
                true,
                "2024-03-01 09:30:00",
                "2024-03-06 11:20:00"
        );

        ArticleEntity a2 = createArticle(
                202,
                "第四代传承人的工具箱",
                "年轻匠人带着爷爷留下的工具，守护米花糖核心技艺。",
                "<h2>工具与记忆</h2><p>竹篾筛、铜锅、麻绳，这些看似普通的工具承载着家族味觉密码。</p>",
                "https://cdn.mgg.com/articles/a202.jpg",
                "品牌故事",
                "赵衡",
                248,
                true,
                "2024-02-26 10:15:00",
                "2024-03-03 18:05:00"
        );

        ArticleEntity a3 = createArticle(
                203,
                "古法熬糖的火候暗语",
                "非遗老师傅分享三段火的细腻技巧。",
                "<h2>火候配方</h2><p>第一段猛火为膨化打基础，中火稳色，小火收香。</p>",
                "https://cdn.mgg.com/articles/a203.jpg",
                "非遗技艺",
                "苏明",
                305,
                true,
                "2024-02-20 08:55:00",
                "2024-03-05 09:42:00"
        );

        ArticleEntity a4 = createArticle(
                204,
                "从乐山到全国：米嘎嘎的扩张之路",
                "梳理米嘎嘎走向全国的关键节点与策略。",
                "<h2>门店布局</h2><p>2024年米嘎嘎启动“川渝+双一线”策略，线下体验店数量翻倍。</p><h2>渠道升级</h2><p>联动社区团购与高铁渠道，实现品牌曝光的多场景联动。</p>",
                "https://cdn.mgg.com/articles/a204.jpg",
                "品牌故事",
                "梁珊",
                412,
                true,
                "2024-03-10 10:00:00",
                "2024-03-11 15:32:00"
        );

        ArticleEntity a5 = createArticle(
                205,
                "老字号的年轻化尝试",
                "复盘米嘎嘎在包装、口味与社媒沟通上的创新组合。",
                "<h2>视觉重塑</h2><p>在图案中融入川蜀纹样，包装翻新后年轻客群提升18%。</p><h2>社群互动</h2><p>上线味觉投票小程序，让粉丝参与新品研发。</p>",
                "https://cdn.mgg.com/articles/a205.jpg",
                "品牌故事",
                "顾雯",
                389,
                true,
                "2024-03-08 14:25:00",
                "2024-03-12 09:10:00"
        );

        ArticleEntity a6 = createArticle(
                206,
                "一块米花糖背后的供应链故事",
                "解构原料、仓储与冷链协同保证口感的全过程。",
                "<h2>原料共生</h2><p>与五家糯米种植合作社签订三年共育计划。</p><h2>仓储协同</h2><p>引入智能温控仓，保障成品含水率稳态。</p>",
                "https://cdn.mgg.com/articles/a206.jpg",
                "品牌故事",
                "陈劲",
                436,
                true,
                "2024-02-28 16:40:00",
                "2024-03-06 10:28:00"
        );

        ArticleEntity a7 = createArticle(
                207,
                "米嘎嘎与非遗传承人的合作故事",
                "记录品牌与传承人共建匠心课堂的幕后细节。",
                "<h2>师徒共创</h2><p>传承人每周驻厂指导，保留火候暗语。</p><h2>公众课堂</h2><p>开放周末体验营，带动年轻人走进作坊。</p>",
                "https://cdn.mgg.com/articles/a207.jpg",
                "品牌故事",
                "林知",
                358,
                true,
                "2024-03-05 09:05:00",
                "2024-03-09 19:22:00"
        );

        ArticleEntity a8 = createArticle(
                208,
                "糯米的挑选与处理秘诀",
                "分享作坊挑米和浸泡的关键步骤，让口感更弹软。",
                "<h2>产区甄选</h2><p>首选海拔600米以上的乐山糯米，颗粒更饱满。</p><h2>浸泡曲线</h2><p>分段控温浸泡8小时，保留米香。</p>",
                "https://cdn.mgg.com/articles/a208.jpg",
                "非遗技艺",
                "姚理",
                295,
                true,
                "2024-02-22 07:40:00",
                "2024-03-04 10:10:00"
        );

        ArticleEntity a9 = createArticle(
                209,
                "麦芽糖的古法熬制全程",
                "火候、搅拌与时间的协同掌控指南。",
                "<h2>火候三段</h2><p>猛火起泡、中火稳色、小火收香，每段严格计时。</p><h2>匠人手感</h2><p>通过糖丝拉力判断熬制完成度。</p>",
                "https://cdn.mgg.com/articles/a209.jpg",
                "非遗技艺",
                "罗衡",
                318,
                true,
                "2024-02-24 08:15:00",
                "2024-03-02 13:18:00"
        );

        ArticleEntity a10 = createArticle(
                210,
                "米花膨化的温度控制艺术",
                "揭秘膨化机与手工翻炒协作的黄金温度。",
                "<h2>温度标尺</h2><p>膨化机设定在185℃，手工翻炒时保持火焰蓝心。</p><h2>质感校准</h2><p>反复试样确保膨化均匀不焦苦。</p>",
                "https://cdn.mgg.com/articles/a210.jpg",
                "非遗技艺",
                "程露",
                276,
                true,
                "2024-02-26 06:55:00",
                "2024-03-06 08:32:00"
        );

        ArticleEntity a11 = createArticle(
                211,
                "切块与成型的手工技艺",
                "每一刀的力度与速度决定米花糖的呈现。",
                "<h2>刀路练习</h2><p>匠人需经三个月训练掌握力度。</p><h2>定量模具</h2><p>使用竹质模具确保尺寸统一。</p>",
                "https://cdn.mgg.com/articles/a211.jpg",
                "非遗技艺",
                "彭西",
                254,
                true,
                "2024-02-27 09:12:00",
                "2024-03-07 12:05:00"
        );

        ArticleEntity a12 = createArticle(
                212,
                "不同口味的配方研发历程",
                "从香辛平衡到消费者测试的研发记录。",
                "<h2>香辛平衡</h2><p>研发团队建立风味数据库，快速验证组合。</p><h2>消费者测试</h2><p>线下试吃反馈转化为配方优化指标。</p>",
                "https://cdn.mgg.com/articles/a212.jpg",
                "非遗技艺",
                "沈柔",
                332,
                true,
                "2024-03-01 11:48:00",
                "2024-03-08 16:45:00"
        );

        ArticleEntity a13 = createArticle(
                213,
                "包装设计中的非遗元素运用",
                "花纹、材质与工艺如何讲述传承故事。",
                "<h2>纹样档案</h2><p>采集非遗纹样并建立向量库用于包装延展。</p><h2>工艺创新</h2><p>将烫金与宣纸纹理结合呈现层次。</p>",
                "https://cdn.mgg.com/articles/a213.jpg",
                "非遗技艺",
                "季禾",
                289,
                true,
                "2024-03-04 10:35:00",
                "2024-03-11 09:28:00"
        );

        ArticleEntity a14 = createArticle(
                214,
                "2024国潮美食消费趋势报告",
                "梳理线下体验与线上社媒的最新数据洞察。",
                "<h2>体验热度</h2><p>线下国潮主题店客流同比增长32%。</p><h2>内容玩法</h2><p>短视频互动率提升源于手作实景直播。</p>",
                "https://cdn.mgg.com/articles/a214.jpg",
                "国潮资讯",
                "袁珊",
                501,
                true,
                "2024-03-12 09:00:00",
                "2024-03-13 08:16:00"
        );

        ArticleEntity a15 = createArticle(
                215,
                "Z世代如何重新定义传统零食",
                "探索年轻消费者在传统零食中的文化表达。",
                "<h2>颜值即正义</h2><p>包装风格成为分享动力的首要因素。</p><h2>文化共鸣</h2><p>年轻人以方言和记忆点重构品牌故事。</p>",
                "https://cdn.mgg.com/articles/a215.jpg",
                "国潮资讯",
                "唐遥",
                468,
                true,
                "2024-03-09 15:20:00",
                "2024-03-13 10:05:00"
        );

        ArticleEntity a16 = createArticle(
                216,
                "米花糖的社交媒体营销策略",
                "拆解米嘎嘎在社媒矩阵中的内容运营打法。",
                "<h2>矩阵分工</h2><p>微博负责热点跟进，小红书聚焦口味开箱。</p><h2>私域转化</h2><p>直播间引导粉丝进入会员体系，实现复购。</p>",
                "https://cdn.mgg.com/articles/a216.jpg",
                "国潮资讯",
                "魏畅",
                433,
                true,
                "2024-03-06 13:52:00",
                "2024-03-10 21:14:00"
        );

        ArticleEntity a17 = createArticle(
                217,
                "非遗美食的现代化包装设计",
                "从材质选择到视觉表达的设计方法论。",
                "<h2>材质升级</h2><p>环保纸浆与可降解膜组合兼顾质感与环保。</p><h2>视觉结构</h2><p>以中轴对称让传统元素更显秩序。</p>",
                "https://cdn.mgg.com/articles/a217.jpg",
                "国潮资讯",
                "何筠",
                352,
                true,
                "2024-03-07 08:06:00",
                "2024-03-12 12:40:00"
        );

        ArticleEntity a18 = createArticle(
                218,
                "传统手工艺的数字化保护",
                "盘点数字资产如何帮助非遗技艺再传播。",
                "<h2>数据建模</h2><p>通过3D扫描记录匠人动作，建立教学档案。</p><h2>线上课堂</h2><p>结合AR体验，让用户在线尝试操作步骤。</p>",
                "https://cdn.mgg.com/articles/a218.jpg",
                "国潮资讯",
                "黎书",
                397,
                true,
                "2024-03-02 10:48:00",
                "2024-03-09 17:36:00"
        );

        ArticleEntity a19 = createArticle(
                219,
                "国潮品牌的文化自信之路",
                "复盘品牌通过内容建设打造文化自信的案例。",
                "<h2>叙事框架</h2><p>将地域故事与现代生活场景结合，形成双线叙事。</p><h2>社群力量</h2><p>共创社区让用户成为品牌内容的生产者。</p>",
                "https://cdn.mgg.com/articles/a219.jpg",
                "国潮资讯",
                "章凝",
                441,
                true,
                "2024-03-03 09:22:00",
                "2024-03-11 14:18:00"
        );

        ArticleEntity a20 = createArticle(
                220,
                "米花糖跨界联名案例分享",
                "精选跨界联名的创意亮点与销售表现。",
                "<h2>餐饮联动</h2><p>与国潮茶饮品牌推出联名饮品，首周售出1.2万杯。</p><h2>文创合作</h2><p>与博物馆推出限定礼盒，线上开售即罄。</p>",
                "https://cdn.mgg.com/articles/a220.jpg",
                "国潮资讯",
                "蒋澜",
                523,
                true,
                "2024-03-11 11:05:00",
                "2024-03-13 19:45:00"
        );

        db.articleDao().insertAll(Arrays.asList(
                a1, a2, a3, a4, a5, a6, a7, a8, a9, a10,
                a11, a12, a13, a14, a15, a16, a17, a18, a19, a20
        ));
    }

    private static ArticleEntity createArticle(long id, String title, String summary, String content,
                                              String cover, String category, String author,
                                              int viewCount, boolean published, String created, String updated) {
        ArticleEntity entity = new ArticleEntity();
        entity.setArticleId(id);
        entity.setTitle(title);
        entity.setSummary(summary);
        entity.setContent(content);
        entity.setCoverImage(cover);
        entity.setCategory(category);
        entity.setAuthor(author);
        entity.setViewCount(viewCount);
        entity.setPublished(published);
        entity.setCreatedAt(created);
        entity.setUpdatedAt(updated);
        return entity;
    }

    private static void seedAddresses(AppDatabase db) {
        db.addressDao().clearAll();

        AddressEntity a1 = createAddress(7001, 6001, "李晴", "13800001001",
                "四川省", "成都市", "锦江区", "红星路三段99号国潮里A座18F",
                true, "2024-02-21 10:12:00");

        AddressEntity a2 = createAddress(7002, 6001, "李晴", "13800001001",
                "四川省", "成都市", "武侯区", "科华北路92号国潮体验店",
                false, "2024-02-26 17:45:00");

        AddressEntity a3 = createAddress(7003, 6002, "周宁", "13900001002",
                "北京市", "北京市", "朝阳区", "工体北路8号潮流市集2层",
                true, "2024-02-24 13:28:00");

        AddressEntity a4 = createAddress(7004, 6003, "陈意", "13700001003",
                "上海市", "上海市", "黄浦区", "中山东一路18号观景公寓906",
                true, "2024-02-27 09:58:00");

        db.addressDao().insertAll(Arrays.asList(a1, a2, a3, a4));
    }

    private static AddressEntity createAddress(int id, int userId, String name, String phone,
                                               String province, String city, String district,
                                               String detail, boolean isDefault, String createTime) {
        AddressEntity entity = new AddressEntity();
        entity.setAddressId(id);
        entity.setUserId(userId);
        entity.setReceiverName(name);
        entity.setReceiverPhone(phone);
        entity.setProvince(province);
        entity.setCity(city);
        entity.setDistrict(district);
        entity.setDetailAddress(detail);
        entity.setDefault(isDefault);
        entity.setCreateTime(createTime);
        return entity;
    }

    private static void seedCartItems(AppDatabase db) {
        db.cartItemDao().clearAll();

        CartItemEntity c1 = createCartItem(8001, 6001, 1003, 1, true, "2024-03-09 11:10:00", "2024-03-09 11:10:00");
        CartItemEntity c2 = createCartItem(8002, 6001, 1006, 2, true, "2024-03-09 11:12:00", "2024-03-09 11:12:00");
        CartItemEntity c3 = createCartItem(8003, 6002, 1007, 1, true, "2024-03-08 19:35:00", "2024-03-08 19:35:00");
        CartItemEntity c4 = createCartItem(8004, 6003, 1008, 3, false, "2024-03-07 08:45:00", "2024-03-07 08:45:00");

        db.cartItemDao().insertAll(Arrays.asList(c1, c2, c3, c4));
    }

    private static CartItemEntity createCartItem(int id, int userId, int productId, int quantity,
                                                 boolean selected, String createTime, String updateTime) {
        CartItemEntity entity = new CartItemEntity();
        entity.setCartId(id);
        entity.setUserId(userId);
        entity.setProductId(productId);
        entity.setQuantity(quantity);
        entity.setSelected(selected);
        entity.setCreateTime(createTime);
        entity.setUpdateTime(updateTime);
        return entity;
    }

    private static void seedOrders(AppDatabase db) {
        db.orderDao().clearAll();
        db.orderItemDao().clearAll();

        OrderEntity o1 = createOrder("MGG202403010001", 6001, 158.80, 7001,
                "李晴", "13800001001", "四川省成都市锦江区红星路三段99号国潮里A座18F",
                2, "春季尝鲜组合", "2024-03-01 10:28:00", null, null, "2024-03-01 10:20:00");

        OrderEntity o2 = createOrder("MGG202403070015", 6002, 165.50, 7003,
                "周宁", "13900001002", "北京市朝阳区工体北路8号潮流市集2层",
                3, null, "2024-03-07 15:42:00", "2024-03-07 16:15:00", null, "2024-03-07 15:35:00");

        OrderEntity o3 = createOrder("MGG202402221008", 6003, 129.60, 7004,
                "陈意", "13700001003", "上海市黄浦区中山东一路18号观景公寓906",
                4, "礼盒送朋友", "2024-02-22 13:05:00", "2024-02-23 09:20:00", "2024-02-27 18:32:00", "2024-02-22 12:58:00");

        db.orderDao().insertAll(Arrays.asList(o1, o2, o3));

        List<OrderItemEntity> items = Arrays.asList(
                createOrderItem(9001, "MGG202403010001", 1001, "手作原味米花糖礼盒",
                        "https://cdn.mgg.com/products/p1001-1.jpg", 58.00, 2),
                createOrderItem(9002, "MGG202403010001", 1005, "金桂乌龙米花糖棒",
                        "https://cdn.mgg.com/products/p1005-1.jpg", 42.80, 1),
                createOrderItem(9003, "MGG202403070015", 1004, "藤椒麻辣米花糖",
                        "https://cdn.mgg.com/products/p1004-1.jpg", 32.50, 3),
                createOrderItem(9004, "MGG202403070015", 1007, "江湖酥辣双拼礼盒",
                        "https://cdn.mgg.com/products/p1007-1.jpg", 68.00, 1),
                createOrderItem(9005, "MGG202402221008", 1002, "黑芝麻酥心米花糖",
                        "https://cdn.mgg.com/products/p1002-1.jpg", 36.80, 1),
                createOrderItem(9006, "MGG202402221008", 1003, "桂花糯米米花糖",
                        "https://cdn.mgg.com/products/p1003-1.jpg", 33.00, 1),
                createOrderItem(9007, "MGG202402221008", 1008, "樱花白桃米花糖",
                        "https://cdn.mgg.com/products/p1008-1.jpg", 29.90, 2)
        );

        db.orderItemDao().insertAll(items);
    }

    private static OrderEntity createOrder(String orderId, int userId, double totalAmount, Integer addressId,
                                           String receiverName, String receiverPhone, String receiverAddress,
                                           int status, String remark, String payTime, String shipTime,
                                           String finishTime, String createTime) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(orderId);
        entity.setUserId(userId);
        entity.setTotalAmount(totalAmount);
        entity.setAddressId(addressId);
        entity.setReceiverName(receiverName);
        entity.setReceiverPhone(receiverPhone);
        entity.setReceiverAddress(receiverAddress);
        entity.setStatus(status);
        entity.setRemark(remark);
        entity.setPayTime(payTime);
        entity.setShipTime(shipTime);
        entity.setFinishTime(finishTime);
        entity.setCreateTime(createTime);
        return entity;
    }

    private static OrderItemEntity createOrderItem(int itemId, String orderId, int productId,
                                                   String productName, String productImage,
                                                   double price, int quantity) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setItemId(itemId);
        entity.setOrderId(orderId);
        entity.setProductId(productId);
        entity.setProductName(productName);
        entity.setProductImage(productImage);
        entity.setPrice(price);
        entity.setQuantity(quantity);
        return entity;
    }

    private static void seedReviews(AppDatabase db) {
        db.reviewDao().clearAll();
        db.reviewCommentDao().clearAll();
        db.reviewLikeDao().clearAll();

        ReviewEntity r1 = createReview(3001, 6003, 1002, "MGG202402221008", 5,
                "黑芝麻颗粒很香，甜度刚好，搭配热茶特别舒服。包装也很好看！",
                "[\"https://cdn.mgg.com/reviews/r3001-1.jpg\",\"https://cdn.mgg.com/reviews/r3001-2.jpg\"]",
                6, 2, "2024-02-28 09:15:00");

        ReviewEntity r2 = createReview(3002, 6002, 1004, "MGG202403070015", 4,
                "藤椒麻味很带劲，晚上追剧来一包完全停不下来，就是有点上火要配茶。",
                "[\"https://cdn.mgg.com/reviews/r3002-1.jpg\"]",
                9, 1, "2024-03-08 21:10:00");

        ReviewEntity r3 = createReview(3003, 6001, 1003, "MGG202403010001", 5,
                "桂花香味非常自然，口感酥脆不粘牙，老人小孩都喜欢。",
                "[]",
                3, 0, "2024-03-05 11:22:00");

        db.reviewDao().insertAll(Arrays.asList(r1, r2, r3));

        List<ReviewCommentEntity> comments = Arrays.asList(
                createReviewComment(4001, 3001, 6002, "确实好吃，我都当早餐零食了！", "2024-02-28 11:05:00"),
                createReviewComment(4002, 3001, 6001, "谢谢推荐，下次买礼盒送朋友~", "2024-02-28 11:32:00"),
                createReviewComment(4003, 3002, 6003, "配凉茶刚好，晚上别吃太多哈哈。", "2024-03-08 22:08:00")
        );

        db.reviewCommentDao().insertAll(comments);

        List<ReviewLikeEntity> likes = Arrays.asList(
                createReviewLike(5001, 3001, 6001, "2024-02-28 10:12:00"),
                createReviewLike(5002, 3001, 6002, "2024-02-28 10:15:00"),
                createReviewLike(5003, 3001, 6003, "2024-02-28 10:16:00"),
                createReviewLike(5004, 3002, 6001, "2024-03-08 21:20:00"),
                createReviewLike(5005, 3002, 6002, "2024-03-08 21:25:00"),
                createReviewLike(5006, 3002, 6003, "2024-03-08 21:28:00")
        );

        db.reviewLikeDao().insertAll(likes);
    }

    private static ReviewEntity createReview(long id, int userId, int productId, String orderId,
                                             int rating, String content, String images,
                                             int likeCount, int commentCount, String createdAt) {
        ReviewEntity entity = new ReviewEntity();
        entity.setReviewId(id);
        entity.setUserId(userId);
        entity.setProductId(productId);
        entity.setOrderId(orderId);
        entity.setRating(rating);
        entity.setContent(content);
        entity.setImages(images);
        entity.setLikeCount(likeCount);
        entity.setCommentCount(commentCount);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(createdAt);
        return entity;
    }

    private static ReviewCommentEntity createReviewComment(long id, long reviewId, int userId,
                                                           String content, String createdAt) {
        ReviewCommentEntity entity = new ReviewCommentEntity();
        entity.setCommentId(id);
        entity.setReviewId(reviewId);
        entity.setUserId(userId);
        entity.setContent(content);
        entity.setCreatedAt(createdAt);
        return entity;
    }

    private static ReviewLikeEntity createReviewLike(long id, long reviewId, int userId, String createdAt) {
        ReviewLikeEntity entity = new ReviewLikeEntity();
        entity.setLikeId(id);
        entity.setReviewId(reviewId);
        entity.setUserId(userId);
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
