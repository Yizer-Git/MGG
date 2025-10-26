package com.just.cn.mgg.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtils {
    private static Toast toast;
    
    /**
     * 显示短Toast
     */
    public static void showShort(Context context, String message) {
        if (context == null) {
            return;
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    /**
     * 默认显示短Toast，兼容旧调用
     */
    public static void show(Context context, String message) {
        showShort(context, message);
    }
    
    /**
     * 显示长Toast
     */
    public static void showLong(Context context, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
    
    /**
     * 取消Toast
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}

