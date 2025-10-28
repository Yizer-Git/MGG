package com.just.cn.mgg.ui.order;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Order;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.List;

public class OrderListActivity extends AppCompatActivity implements OrderAdapter.OnOrderActionListener {

    private static final String TAG = "OrderListActivity";
    public static final String EXTRA_INITIAL_STATUS = "initial_status";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private TextView tvNoOrders;
    private ProgressBar progressBar;

    private LocalRepository repository;
    private int userId;
    private Integer currentStatus = null; // null for "全部"
    private int currentPage = 1;
    // TODO: 实现分页加载逻辑

    // 用于接收从详情页返回的结果（例如订单状态改变）
    private ActivityResultLauncher<Intent> orderDetailLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        repository = new LocalRepository(this);
        if (!SPUtils.isLogin(this)) {
            ToastUtils.show(this, "请先登录");
            finish();
            return;
        }
        userId = SPUtils.getInt(this, Constants.KEY_USER_ID);
        if (userId == 0) {
            ToastUtils.show(this, "用户信息异常，请重新登录");
            finish();
            return;
        }

        currentStatus = (Integer) getIntent().getSerializableExtra(EXTRA_INITIAL_STATUS);

        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();
        setupOrderDetailLauncher();

        // 根据传入的状态或默认状态加载数据
        loadOrders(currentStatus, currentPage);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        rvOrders = findViewById(R.id.rvOrders);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("全部").setTag(null));
        tabLayout.addTab(tabLayout.newTab().setText("待付款").setTag(0));
        tabLayout.addTab(tabLayout.newTab().setText("待发货").setTag(1));
        tabLayout.addTab(tabLayout.newTab().setText("待收货").setTag(2));
        tabLayout.addTab(tabLayout.newTab().setText("已完成").setTag(3));
        // 已取消(4) 和 已关闭(5) 也可以添加

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentStatus = (Integer) tab.getTag();
                currentPage = 1; // 重置页码
                loadOrders(currentStatus, currentPage);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 选中传入的状态
        if (currentStatus != null) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if (currentStatus.equals(tabLayout.getTabAt(i).getTag())) {
                    tabLayout.getTabAt(i).select();
                    break;
                }
            }
        }
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this);
        orderAdapter.setListener(this);
        rvOrders.setAdapter(orderAdapter);
        // TODO: 添加上拉加载更多监听
    }

    private void setupOrderDetailLauncher() {
        orderDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 如果订单详情页有状态变更，返回时刷新列表
                    if (result.getResultCode() == RESULT_OK) {
                        loadOrders(currentStatus, currentPage);
                    }
                });
    }


    private void loadOrders(Integer status, int page) {
        showLoading();
        try {
            List<Order> orders = (status == null)
                    ? repository.getOrders(userId)
                    : repository.getOrders(userId, status);
            if (orders == null || orders.isEmpty()) {
                showEmptyView();
            } else {
                showOrderList(orders);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadOrders: ", e);
            ToastUtils.show(this, "加载失败：" + e.getMessage());
            showEmptyView();
        } finally {
            hideLoading();
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvOrders.setVisibility(View.GONE);
        tvNoOrders.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        rvOrders.setVisibility(View.GONE);
        tvNoOrders.setVisibility(View.VISIBLE);
        orderAdapter.setOrderList(null); // 清空适配器
    }

    private void showOrderList(List<Order> orders) {
        rvOrders.setVisibility(View.VISIBLE);
        tvNoOrders.setVisibility(View.GONE);
        orderAdapter.setOrderList(orders);
    }


    // --- OrderAdapter.OnOrderActionListener 实现 ---

    @Override
    public void onOrderItemClick(Order order, int position) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
        orderDetailLauncher.launch(intent);
    }

    @Override
    public void onAction1Click(Order order, int position) {
        // "取消订单" (状态 0)
        if (order.getStatus() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("取消订单")
                    .setMessage("您确定要取消这个订单吗？")
                    .setPositiveButton("确定", (dialog, which) -> cancelOrder(order, position))
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    @Override
    public void onAction2Click(Order order, int position) {
        if (order.getStatus() == 0) {
            // "去支付"
            ToastUtils.show(this, "支付功能待实现");
            // TODO: 跳转支付
        } else if (order.getStatus() == 2) {
            // "确认收货"
            new AlertDialog.Builder(this)
                    .setTitle("确认收货")
                    .setMessage("请确认您已收到商品")
                    .setPositiveButton("确定", (dialog, which) -> confirmReceipt(order, position))
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    // --- API 调用 ---

    private void cancelOrder(Order order, int position) {
        setLoadingState(true);
        try {
            repository.cancelOrder(order.getOrderId());
            ToastUtils.show(this, "订单已取消");
            loadOrders(currentStatus, currentPage);
        } catch (Exception e) {
            ToastUtils.show(this, "操作失败：" + e.getMessage());
        } finally {
            setLoadingState(false);
        }
    }

    private void confirmReceipt(Order order, int position) {
        setLoadingState(true);
        try {
            repository.confirmOrder(order.getOrderId());
            ToastUtils.show(this, "确认收货成功");
            loadOrders(currentStatus, currentPage);
        } catch (Exception e) {
            ToastUtils.show(this, "操作失败：" + e.getMessage());
        } finally {
            setLoadingState(false);
        }
    }

    private void setLoadingState(boolean isLoading) {
        // 在列表页，最好使用下拉刷新或在特定item上显示加载
        // 暂时使用全局 ProgressBar
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rvOrders.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            rvOrders.setEnabled(true);
        }
    }
}
