package com.just.cn.mgg.ui.order;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Address;
import com.just.cn.mgg.data.model.Order;
import com.just.cn.mgg.data.model.OrderItem;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.PriceUtils;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailActivity";
    public static final String EXTRA_ORDER_ID = "order_id";

    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private LinearLayout bottomActionContainer;
    private Button btnOrderAction1, btnOrderAction2;

    // 页面控件
    private TextView tvOrderStatus, tvOrderStatusInfo;
    private TextView tvReceiverNamePhone, tvFullAddress;
    private LinearLayout llOrderItems;
    private TextView tvOrderId, tvCreateTime, tvPayTime, tvRemark;
    private LinearLayout rowPayTime, rowRemark;
    private TextView tvItemsTotalAmount, tvShippingFee, tvFinalTotalAmount;

    private LocalRepository repository;
    private int userId;
    private String orderId;
    private Order currentOrder;
    private boolean orderStatusChanged = false; // 标记订单状态是否发生变化

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (TextUtils.isEmpty(orderId)) {
            ToastUtils.show(this, "无效的订单ID");
            finish();
            return;
        }

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

        initViews();
        setupToolbar();
        loadOrderDetail();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        bottomActionContainer = findViewById(R.id.bottomActionContainer);
        btnOrderAction1 = findViewById(R.id.btnOrderAction1);
        btnOrderAction2 = findViewById(R.id.btnOrderAction2);

        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderStatusInfo = findViewById(R.id.tvOrderStatusInfo);
        tvReceiverNamePhone = findViewById(R.id.tvReceiverNamePhone);
        tvFullAddress = findViewById(R.id.tvFullAddress);
        llOrderItems = findViewById(R.id.llOrderItems);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCreateTime = findViewById(R.id.tvCreateTime);
        tvPayTime = findViewById(R.id.tvPayTime);
        tvRemark = findViewById(R.id.tvRemark);
        rowPayTime = findViewById(R.id.rowPayTime);
        rowRemark = findViewById(R.id.rowRemark);
        tvItemsTotalAmount = findViewById(R.id.tvItemsTotalAmount);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvFinalTotalAmount = findViewById(R.id.tvFinalTotalAmount);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finishWithResult());
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    private void finishWithResult() {
        if (orderStatusChanged) {
            setResult(RESULT_OK); // 通知列表页刷新
        }
        finish();
    }


    private void loadOrderDetail() {
        progressBar.setVisibility(View.VISIBLE);
        setLoadingState(true);

        try {
            currentOrder = repository.getOrderDetail(orderId);
            if (currentOrder != null) {
                populateOrderDetails();
            } else {
                ToastUtils.show(this, "未找到订单详情");
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadOrderDetail: ", e);
            ToastUtils.show(this, "加载失败：" + e.getMessage());
            finish();
        } finally {
            progressBar.setVisibility(View.GONE);
            setLoadingState(false);
        }
    }

    private void populateOrderDetails() {
        if (currentOrder == null) return;

        // 1. 状态信息
        tvOrderStatus.setText(currentOrder.getStatusText());
        tvOrderStatusInfo.setText(currentOrder.getStatusDescription()); // 假设模型中有描述

        // 2. 地址信息
        Address address = currentOrder.getAddress(); // 假设Order对象中有关联的Address
        if (address != null) {
            tvReceiverNamePhone.setText(address.getReceiverName() + " " + address.getReceiverPhone());
            tvFullAddress.setText(address.getFullAddress());
        }

        // 3. 商品列表
        llOrderItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        if (currentOrder.getOrderItems() != null) {
            for (OrderItem item : currentOrder.getOrderItems()) {
                // 复用订单确认页的item布局
                View itemView = inflater.inflate(R.layout.item_order_confirm_product, llOrderItems, false);
                ImageView ivProduct = itemView.findViewById(R.id.ivOrderItemImage);
                TextView tvName = itemView.findViewById(R.id.tvOrderItemName);
                TextView tvPrice = itemView.findViewById(R.id.tvOrderItemPrice);
                TextView tvQuantity = itemView.findViewById(R.id.tvOrderItemQuantity);
                TextView tvItemTotal = itemView.findViewById(R.id.tvOrderItemTotal); // 这个布局可能没有小计

                tvName.setText(item.getProductName());
                Glide.with(this)
                        .load(item.getProductImage())
                        .placeholder(R.color.color_surface_elevated)
                        .error(R.color.color_surface_elevated)
                        .into(ivProduct);
                tvPrice.setText(PriceUtils.format(item.getPrice())); // 使用订单项中的价格
                tvQuantity.setText("x" + item.getQuantity());
                if (tvItemTotal != null) {
                    tvItemTotal.setText(PriceUtils.format(item.getPrice() * item.getQuantity()));
                }

                llOrderItems.addView(itemView);
            }
        }

        // 4. 订单信息
        tvOrderId.setText(currentOrder.getOrderId());
        tvCreateTime.setText(currentOrder.getCreateTime()); // 假设是格式化好的字符串

        if (!TextUtils.isEmpty(currentOrder.getPayTime())) {
            tvPayTime.setText(currentOrder.getPayTime());
            rowPayTime.setVisibility(View.VISIBLE);
        } else {
            rowPayTime.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(currentOrder.getRemark())) {
            tvRemark.setText(currentOrder.getRemark());
            rowRemark.setVisibility(View.VISIBLE);
        } else {
            rowRemark.setVisibility(View.GONE);
        }

        // 5. 金额信息
        // TODO: 后端 OrderService.getOrderById 似乎没有计算 itemsTotalAmount
        // 暂时手动计算
        double itemsTotal = 0.0;
        if (currentOrder.getOrderItems() != null) {
            for (OrderItem item : currentOrder.getOrderItems()) {
                itemsTotal += item.getPrice() * item.getQuantity();
            }
        }
        tvItemsTotalAmount.setText(PriceUtils.format(itemsTotal));
        tvShippingFee.setText(PriceUtils.format(currentOrder.getShippingFee()));
        tvFinalTotalAmount.setText(PriceUtils.format(currentOrder.getTotalAmount()));


        // 6. 底部操作按钮
        setupActionButtons();
    }

    private void setupActionButtons() {
        if (currentOrder == null) return;

        bottomActionContainer.setVisibility(View.VISIBLE);
        btnOrderAction1.setVisibility(View.GONE);
        btnOrderAction2.setVisibility(View.GONE);

        switch (currentOrder.getStatus()) {
            case Constants.ORDER_STATUS_PENDING: // 待付款
                btnOrderAction1.setText("取消订单");
                btnOrderAction1.setVisibility(View.VISIBLE);
                btnOrderAction2.setText("去支付");
                btnOrderAction2.setVisibility(View.VISIBLE);

                btnOrderAction1.setOnClickListener(v -> showCancelConfirmation());
                btnOrderAction2.setOnClickListener(v -> showPayConfirmation());
                break;
            case Constants.ORDER_STATUS_SHIPPED: // 待收货
                btnOrderAction2.setText("确认收货");
                btnOrderAction2.setVisibility(View.VISIBLE);
                btnOrderAction2.setOnClickListener(v -> showConfirmReceiptConfirmation());
                break;
            default:
                bottomActionContainer.setVisibility(View.GONE); // 其他状态无操作
                break;
        }
    }

    private void showPayConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("支付订单")
                .setMessage("确认支付该订单吗？")
                .setPositiveButton("确定", (dialog, which) -> payOrder())
                .setNegativeButton("取消", null)
                .show();
    }

    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("取消订单")
                .setMessage("您确定要取消这个订单吗？")
                .setPositiveButton("确定", (dialog, which) -> cancelOrder())
                .setNegativeButton("取消", null)
                .show();
    }

    private void showConfirmReceiptConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("确认收货")
                .setMessage("请确认您已收到商品")
                .setPositiveButton("确定", (dialog, which) -> confirmReceipt())
                .setNegativeButton("取消", null)
                .show();
    }

    private void cancelOrder() {
        setLoadingState(true);
        try {
            repository.cancelOrder(currentOrder.getOrderId());
            ToastUtils.show(this, "订单已取消");
            orderStatusChanged = true;
            loadOrderDetail();
        } catch (Exception e) {
            ToastUtils.show(this, "操作失败：" + e.getMessage());
        } finally {
            setLoadingState(false);
        }
    }

    private void confirmReceipt() {
        setLoadingState(true);
        try {
            repository.confirmOrder(currentOrder.getOrderId());
            ToastUtils.show(this, "确认收货成功");
            orderStatusChanged = true;
            loadOrderDetail();
        } catch (Exception e) {
            ToastUtils.show(this, "操作失败：" + e.getMessage());
        } finally {
            setLoadingState(false);
        }
    }

    private void payOrder() {
        setLoadingState(true);
        try {
            repository.payOrder(currentOrder.getOrderId());
            ToastUtils.show(this, "支付成功");
            orderStatusChanged = true;
            loadOrderDetail();
        } catch (IllegalStateException | IllegalArgumentException e) {
            ToastUtils.show(this, e.getMessage());
        } catch (Exception e) {
            ToastUtils.show(this, "支付失败：" + e.getMessage());
        } finally {
            setLoadingState(false);
        }
    }

    private void setLoadingState(boolean isLoading) {
        btnOrderAction1.setEnabled(!isLoading);
        btnOrderAction2.setEnabled(!isLoading);
    }
}
