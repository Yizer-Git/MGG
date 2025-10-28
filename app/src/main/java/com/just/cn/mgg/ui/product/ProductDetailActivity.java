package com.just.cn.mgg.ui.product;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.CartItem;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.PriceUtils;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.ArrayList;

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
    
    private LocalRepository repository;
    private Product product;
    private int productId;
    private int userId;
    
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
        
        repository = new LocalRepository(this);
        userId = SPUtils.getInt(this, com.just.cn.mgg.utils.Constants.KEY_USER_ID);

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
        product = repository.getProductDetail(productId);
        if (product == null) {
            ToastUtils.show(this, "未找到商品信息");
            finish();
        } else {
            displayProduct();
        }
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
        if (product.hasDiscount()) {
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
        String imageUrl = product.getMainImage();
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.color.color_surface_elevated)
                .into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.color.color_surface_elevated);
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
        
        if (!SPUtils.isLogin(this)) {
            ToastUtils.show(this, "请先登录");
            return;
        }

        if (userId == 0) {
            userId = SPUtils.getInt(this, Constants.KEY_USER_ID);
        }
        if (userId == 0) {
            ToastUtils.show(this, "请先登录");
            return;
        }

        try {
            repository.addToCart(userId, product.getProductId(), 1);
            ToastUtils.show(this, "已加入购物车");
        } catch (Exception e) {
            ToastUtils.show(this, "添加失败：" + e.getMessage());
        }
    }
    
    /**
     * 立即购买
     */
    private void buyNow() {
        if (product == null) {
            ToastUtils.show(this, "产品信息加载中");
            return;
        }
        if (!SPUtils.isLogin(this)) {
            ToastUtils.show(this, "请先登录");
            return;
        }
        if (userId == 0) {
            userId = SPUtils.getInt(this, Constants.KEY_USER_ID);
        }
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(product.getProductId());
        item.setProduct(product);
        item.setQuantity(1);
        item.setSelected(true);

        ArrayList<CartItem> list = new ArrayList<>();
        list.add(item);

        Intent intent = new Intent(this, com.just.cn.mgg.ui.order.OrderConfirmActivity.class);
        intent.putExtra(com.just.cn.mgg.ui.order.OrderConfirmActivity.EXTRA_SELECTED_ITEMS, list);
        startActivity(intent);
    }
}
