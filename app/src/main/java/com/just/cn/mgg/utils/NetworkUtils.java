package com.just.cn.mgg.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * 网络工具类
 */
public class NetworkUtils {
    
    /**
     * 检查网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (cm == null) {
            return false;
        }
        
        Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }
    
    /**
     * 检查是否是WiFi网络
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (cm == null) {
            return false;
        }
        
        Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && 
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }
}

