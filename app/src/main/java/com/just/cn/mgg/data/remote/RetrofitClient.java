package com.just.cn.mgg.data.remote;

import com.just.cn.mgg.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Retrofit单例客户端
 */
public class RetrofitClient {
    private static volatile RetrofitClient instance;
    private final ApiService apiService;
    
    private RetrofitClient() {
        // 日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? 
            HttpLoggingInterceptor.Level.BODY : 
            HttpLoggingInterceptor.Level.NONE);
        
        // OkHttp配置
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build();
        
        // Retrofit配置
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        apiService = retrofit.create(ApiService.class);
    }
    
    /**
     * 获取单例实例
     */
    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }
    
    /**
     * 获取API服务
     */
    public ApiService getApiService() {
        return apiService;
    }
}

