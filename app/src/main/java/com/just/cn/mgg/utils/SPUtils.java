package com.just.cn.mgg.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 */
public class SPUtils {
    private static final String SP_NAME = "migaga_sp";
    
    /**
     * 保存数据
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        
        editor.apply();
    }
    
    /**
     * 获取String
     */
    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }
    
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
    
    /**
     * 获取Int
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }
    
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }
    
    /**
     * 获取Boolean
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }
    
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
    
    /**
     * 获取Float
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, 0f);
    }
    
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }
    
    /**
     * 获取Long
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, 0L);
    }
    
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }
    
    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
    
    /**
     * 删除指定key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }
    
    /**
     * 检查是否登录
     */
    public static boolean isLogin(Context context) {
        String token = getString(context, Constants.KEY_TOKEN);
        return token != null && !token.isEmpty();
    }
    
    /**
     * 获取Token
     */
    public static String getToken(Context context) {
        return getString(context, Constants.KEY_TOKEN);
    }
    
    /**
     * 获取带Bearer前缀的Token
     */
    public static String getAuthToken(Context context) {
        String token = getToken(context);
        return token.isEmpty() ? "" : "Bearer " + token;
    }

    /**
     * 存储用户昵称
     */
    public static void saveUsername(Context context, String username) {
        put(context, Constants.KEY_NICKNAME, username);
    }

    /**
     * 获取用户昵称
     */
    public static String getUsername(Context context) {
        return getString(context, Constants.KEY_NICKNAME, "");
    }
}

