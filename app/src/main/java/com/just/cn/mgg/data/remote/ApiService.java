package com.just.cn.mgg.data.remote;

import com.just.cn.mgg.data.model.Address;
import com.just.cn.mgg.data.model.Order;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.data.model.User;
import com.just.cn.mgg.data.remote.response.ApiResponse;
import com.just.cn.mgg.data.remote.response.LoginResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * API接口定义
 */
public interface ApiService {
    
    // ========== 用户认证 ==========
    
    /**
     * 用户登录
     */
    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body Map<String, String> params);
    
    /**
     * 用户注册
     */
    @POST("api/auth/register")
    Call<ApiResponse<User>> register(@Body Map<String, String> params);
    
    /**
     * 发送验证码
     */
    @POST("api/auth/sendCode")
    Call<ApiResponse<String>> sendVerifyCode(@Query("phone") String phone);
    
    /**
     * 验证码登录
     */
    @POST("api/auth/loginByCode")
    Call<ApiResponse<LoginResponse>> loginByCode(@Body Map<String, String> params);
    
    /**
     * 获取用户信息
     */
    @GET("api/user/info")
    Call<ApiResponse<User>> getUserInfo(@Header("Authorization") String token);
    
    /**
     * 更新用户信息
     */
    @POST("api/user/update")
    Call<ApiResponse<User>> updateUserInfo(
        @Header("Authorization") String token,
        @Body Map<String, String> params
    );
    
    // ========== 产品相关 ==========
    
    /**
     * 获取产品列表
     */
    @GET("api/products")
    Call<ApiResponse<List<Product>>> getProducts(
        @Query("category_id") Integer categoryId,
        @Query("page") int page,
        @Query("page_size") int pageSize
    );
    
    /**
     * 获取产品列表（带排序）
     */
    @GET("api/products")
    Call<ApiResponse<List<Product>>> getProducts(
        @Query("category_id") Integer categoryId,
        @Query("page") int page,
        @Query("page_size") int pageSize,
        @Query("sort_by") String sortBy
    );
    
    /**
     * 获取产品详情
     */
    @GET("api/products/{id}")
    Call<ApiResponse<Product>> getProductDetail(@Path("id") int productId);
    
    /**
     * 搜索产品
     */
    @GET("api/products/search")
    Call<ApiResponse<List<Product>>> searchProducts(
        @Query("keyword") String keyword,
        @Query("page") int page
    );
    
    /**
     * 获取热销产品
     */
    @GET("api/products/hot")
    Call<ApiResponse<List<Product>>> getHotProducts(@Query("limit") int limit);
    
    // ========== 购物车 ==========
    
    /**
     * 添加到购物车
     */
    @POST("api/cart/add")
    Call<ApiResponse<String>> addToCart(
        @Header("Authorization") String token,
        @Body Map<String, Object> params
    );
    
    /**
     * 获取购物车列表
     */
    @GET("api/cart/list")
    Call<ApiResponse<List<Product>>> getCartList(
        @Header("Authorization") String token
    );
    
    /**
     * 更新购物车商品数量
     */
    @POST("api/cart/update")
    Call<ApiResponse<String>> updateCartItem(
        @Header("Authorization") String token,
        @Body Map<String, Object> params
    );
    
    /**
     * 删除购物车商品
     */
    @POST("api/cart/delete")
    Call<ApiResponse<String>> deleteCartItem(
        @Header("Authorization") String token,
        @Query("product_id") int productId
    );
    
    // ========== 订单 ==========
    
    /**
     * 创建订单
     */
    @POST("api/orders/create")
    Call<ApiResponse<Order>> createOrder(
        @Header("Authorization") String token,
        @Body Map<String, Object> params
    );
    
    /**
     * 获取订单列表
     */
    @GET("api/orders/list")
    Call<ApiResponse<List<Order>>> getOrderList(
        @Header("Authorization") String token,
        @Query("status") Integer status,
        @Query("page") int page
    );
    
    /**
     * 获取订单详情
     */
    @GET("api/orders/{orderId}")
    Call<ApiResponse<Order>> getOrderDetail(
        @Header("Authorization") String token,
        @Path("orderId") String orderId
    );
    
    /**
     * 取消订单
     */
    @POST("api/orders/cancel")
    Call<ApiResponse<String>> cancelOrder(
        @Header("Authorization") String token,
        @Query("order_id") String orderId
    );
    
    /**
     * 确认收货
     */
    @POST("api/orders/confirm")
    Call<ApiResponse<String>> confirmOrder(
        @Header("Authorization") String token,
        @Query("order_id") String orderId
    );
    
    // ========== 地址管理 ==========
    
    /**
     * 获取地址列表
     */
    @GET("api/address/list")
    Call<ApiResponse<List<Address>>> getAddressList(
        @Header("Authorization") String token
    );
    
    /**
     * 添加地址
     */
    @POST("api/address/add")
    Call<ApiResponse<Address>> addAddress(
        @Header("Authorization") String token,
        @Body Address address
    );
    
    /**
     * 更新地址
     */
    @POST("api/address/update")
    Call<ApiResponse<Address>> updateAddress(
        @Header("Authorization") String token,
        @Body Address address
    );
    
    /**
     * 删除地址
     */
    @POST("api/address/delete")
    Call<ApiResponse<String>> deleteAddress(
        @Header("Authorization") String token,
        @Query("address_id") int addressId
    );
    
    /**
     * 设置默认地址
     */
    @POST("api/address/setDefault")
    Call<ApiResponse<String>> setDefaultAddress(
        @Header("Authorization") String token,
        @Query("address_id") int addressId
    );
}

