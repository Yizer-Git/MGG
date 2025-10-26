package com.just.cn.mgg.ui.product;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.data.remote.ApiService;
import com.just.cn.mgg.data.remote.RetrofitClient;
import com.just.cn.mgg.data.remote.response.ApiResponse;
import com.just.cn.mgg.utils.PriceUtils;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 产品详情页
 */
public class ProductDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_PRODUCT_ID = "product_id";
    
    private ImageView ivProductImage;
    private TextView tvPrice;
    private TextView tvOriginalPrice;
    private TextView tvSales;
    private TextView tvProductName;
    private TextView tvStock;
    private TextView tvDescription;
    private Button btnAddToCart;
    private Button btnBuyNow;
    
    private ApiService apiService;
    private Product product;
    private int productId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            ToastUtils.show(this, "产品ID无效");
            finish();
            return;
        }
        
        apiService = RetrofitClient.getInstance().getApiService();
        
        initViews();
        loadProductDetail();
    }
    
    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvPrice = findViewById(R.id.tvPrice);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvSales = findViewById(R.id.tvSales);
        tvProductName = findViewById(R.id.tvProductName);
        tvStock = findViewById(R.id.tvStock);
        tvDescription = findViewById(R.id.tvDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        
        // 加入购物车
        btnAddToCart.setOnClickListener(v -> addToCart());
        
        // 立即购买
        btnBuyNow.setOnClickListener(v -> buyNow());
    }
    
    /**
     * 加载产品详情
     */
    private void loadProductDetail() {
        apiService.getProductDetail(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, 
                                 Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                        product = apiResponse.getData();
                        displayProduct();
                    } else {
                        ToastUtils.show(ProductDetailActivity.this, apiResponse.getMessage());
                    }
                } else {
                    ToastUtils.show(ProductDetailActivity.this, "加载失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                ToastUtils.show(ProductDetailActivity.this, "网络错误：" + t.getMessage());
            }
        });
    }
    
    /**
     * 显示产品信息
     */
    private void displayProduct() {
        if (product == null) return;
        
        // 产品名称
        tvProductName.setText(product.getProductName());
        
        // 价格
        tvPrice.setText(PriceUtils.format(product.getPrice()));
        
        // 原价
        if (product.getOriginalPrice() != null && 
            product.getOriginalPrice().compareTo(product.getPrice()) > 0) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText(PriceUtils.format(product.getOriginalPrice()));
            tvOriginalPrice.setPaintFlags(
                tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }
        
        // 销量
        tvSales.setText("已售" + product.getSales());
        
        // 库存
        tvStock.setText("库存：" + product.getStock() + "件");
        
        // 描述
        tvDescription.setText(product.getDescription());
        
        // 产品图片
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().split(",")[0];
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.color.secondary)
                .into(ivProductImage);
        }
    }
    
    /**
     * 加入购物车
     */
    private void addToCart() {
        if (product == null) {
            ToastUtils.show(this, "产品信息加载中");
            return;
        }
        
        String token = SPUtils.getToken(this);
        if (token == null || token.isEmpty()) {
            ToastUtils.show(this, "请先登录");
            return;
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("product_id", product.getProductId());
        params.put("quantity", 1);
        
        apiService.addToCart("Bearer " + token, params).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, 
                                 Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.getCode() == 200) {
                        ToastUtils.show(ProductDetailActivity.this, "已加入购物车");
                    } else {
                        ToastUtils.show(ProductDetailActivity.this, apiResponse.getMessage());
                    }
                } else {
                    ToastUtils.show(ProductDetailActivity.this, "添加失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                ToastUtils.show(ProductDetailActivity.this, "网络错误：" + t.getMessage());
            }
        });
    }
    
    /**
     * 立即购买
     */
    private void buyNow() {
        if (product == null) {
            ToastUtils.show(this, "产品信息加载中");
            return;
        }
        
        // TODO: 跳转到订单确认页
        ToastUtils.show(this, "立即购买功能待实现");
    }
}

