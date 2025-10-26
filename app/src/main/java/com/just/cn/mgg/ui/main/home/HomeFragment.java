package com.just.cn.mgg.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Product;
import com.just.cn.mgg.data.remote.ApiService;
import com.just.cn.mgg.data.remote.RetrofitClient;
import com.just.cn.mgg.data.remote.response.ApiResponse;
import com.just.cn.mgg.ui.adapter.ProductAdapter;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment {
    
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ApiService apiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitClient.getInstance().getApiService();
        initViews(view);
        loadProducts();
    }
    
    private void initViews(View view) {
        rvProducts = view.findViewById(R.id.rvProducts);
        
        // 设置网格布局
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(layoutManager);
        
        // 设置适配器
        productAdapter = new ProductAdapter(getContext());
        productAdapter.setOnItemClickListener(product -> {
            // 跳转到产品详情页
            Intent intent = new Intent(getContext(), com.just.cn.mgg.ui.product.ProductDetailActivity.class);
            intent.putExtra(com.just.cn.mgg.ui.product.ProductDetailActivity.EXTRA_PRODUCT_ID, 
                          product.getProductId());
            startActivity(intent);
        });
        rvProducts.setAdapter(productAdapter);
    }
    
    /**
     * 加载产品列表
     */
    private void loadProducts() {
        apiService.getProducts(null, 1, 20, "sales").enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, 
                                 Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                        productAdapter.setProductList(apiResponse.getData());
                    } else {
                        ToastUtils.show(getContext(), apiResponse.getMessage());
                    }
                } else {
                    ToastUtils.show(getContext(), "加载失败");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                ToastUtils.show(getContext(), "网络错误：" + t.getMessage());
            }
        });
    }
}

