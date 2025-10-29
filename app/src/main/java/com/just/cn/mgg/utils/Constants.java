package com.just.cn.mgg.utils;

/**
 * 常量定义
 */
public class Constants {
    
    // ========== 产品分类 ==========
    public static final int CATEGORY_CLASSIC = 1;  // 经典复刻
    public static final int CATEGORY_TRENDY = 2;   // 潮流创味
    
    // ========== 订单状态 ==========
    public static final int ORDER_STATUS_PENDING = 0;    // 待付款
    public static final int ORDER_STATUS_PAID = 1;       // 待发货
    public static final int ORDER_STATUS_SHIPPED = 2;    // 待收货
    public static final int ORDER_STATUS_COMPLETED = 3;  // 已完成
    public static final int ORDER_STATUS_CANCELLED = 4;  // 已取消
    
    // ========== SharedPreferences Keys ==========
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
    public static final String KEY_IS_LOGIN = "is_login";
    public static final String KEY_PUSH_NOTIFICATION = "push_notification";
    public static final String KEY_ACTIVITY_PROMOTION = "activity_promotion";
    public static final String KEY_LOCATION_SERVICE = "location_service";
    public static final String KEY_USER_AVATAR = "user_avatar";
    public static final String KEY_USER_GENDER = "user_gender";
    public static final String KEY_USER_BIRTHDAY = "user_birthday";
    
    // ========== 页面请求码 ==========
    public static final int REQUEST_CODE_LOGIN = 1001;
    public static final int REQUEST_CODE_ADDRESS = 1002;
    public static final int REQUEST_CODE_PAYMENT = 1003;
    public static final int REQUEST_CODE_SELECT_ADDRESS = 1004;
    
    // ========== 其他常量 ==========
    public static final int PAGE_SIZE = 20; // 分页大小
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // 品牌信息
    public static final String BRAND_NAME = "米嘎嘎";
    public static final String BRAND_SLOGAN = "嘉州酥韵·国潮新滋味";
}

