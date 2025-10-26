package com.just.cn.mgg.utils;

import java.text.DecimalFormat;

/**
 * 价格工具类
 */
public class PriceUtils {
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");
    
    /**
     * 格式化价格
     */
    public static String formatPrice(double price) {
        return "¥" + PRICE_FORMAT.format(price);
    }
    
    /**
     * 格式化价格（无货币符号）
     */
    public static String formatPriceWithoutSymbol(double price) {
        return PRICE_FORMAT.format(price);
    }
}

