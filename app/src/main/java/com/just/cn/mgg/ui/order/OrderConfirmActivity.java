package com.just.cn.mgg.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Address;
import com.just.cn.mgg.data.model.CartItem;
import com.just.cn.mgg.data.model.Order;
import com.just.cn.mgg.data.model.OrderItem;
import com.just.cn.mgg.ui.address.AddressListActivity;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.PriceUtils;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmActivity extends AppCompatActivity {

    private static final String TAG = "OrderConfirmActivity";
    public static final String EXTRA_SELECTED_ITEMS = "selected_items";

    private MaterialToolbar toolbar;
    private MaterialCardView addressCard;
    private TextView tvNoAddress, tvReceiverNamePhone, tvFullAddress, tvDefaultTag;
    private LinearLayout llOrderItems;
    private TextView tvItemsTotalAmount, tvShippingFee, tvFinalTotalAmount;
    private TextInputEditText etRemark;
    private Button btnSubmitOrder;

    private LocalRepository repository;
    private List<CartItem> selectedItems;
    private Address selectedAddress;
    private double itemsTotalAmount = 0.0;
    private double shippingFee = 0.0; // 假设运费为0
    private int userId;

    // 用于接收从地址列表选择地址的结果
    private ActivityResultLauncher<Intent> addressSelectLauncher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

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

        // 获取传递过来的选中商品
        selectedItems = (List<CartItem>) getIntent().getSerializableExtra(EXTRA_SELECTED_ITEMS);
        if (selectedItems == null || selectedItems.isEmpty()) {
            ToastUtils.show(this, "没有选中的商品");
            finish();
            return;
        }

        for (CartItem item : selectedItems) {
            if (item != null) {
                item.setUserId(userId);
                if (item.getProduct() == null) {
                    item.setProduct(repository.getProductDetail(item.getProductId()));
                }
            }
        }

        initViews();
        setupToolbar();
        setupAddressSelection();
        loadDefaultAddress();
        displayOrderItems();
        calculateAndDisplayTotal();
        setupSubmitButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        addressCard = findViewById(R.id.addressCard);
        tvNoAddress = findViewById(R.id.tvNoAddress);
        tvReceiverNamePhone = findViewById(R.id.tvReceiverNamePhone);
        tvFullAddress = findViewById(R.id.tvFullAddress);
        tvDefaultTag = findViewById(R.id.tvDefaultTag);
        llOrderItems = findViewById(R.id.llOrderItems);
        tvItemsTotalAmount = findViewById(R.id.tvItemsTotalAmount);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvFinalTotalAmount = findViewById(R.id.tvFinalTotalAmount);
        etRemark = findViewById(R.id.etRemark);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示返回按钮
        toolbar.setNavigationOnClickListener(v -> finish()); // 返回按钮事件
    }

        private void setupAddressSelection() {
            // 初始化 ActivityResultLauncher
            addressSelectLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            // 从 AddressListActivity 返回选中的 Address 对象
                            Address address = (Address) result.getData().getSerializableExtra(AddressListActivity.EXTRA_SELECTED_ADDRESS);
                            if (address != null) {
                                selectedAddress = address;
                                displayAddress(); // 更新显示的地址
                            }
                        }
                    });

            addressCard.setOnClickListener(v -> {
                // 跳转到地址列表 Activity (AddressListActivity)，并标记为选择模式
                Intent intent = new Intent(this, AddressListActivity.class);
                intent.putExtra(AddressListActivity.EXTRA_IS_SELECTING, true); // 标记为选择模式
                addressSelectLauncher.launch(intent); // 启动 Activity 并期望返回结果
            });
        }





    /**
     * 加载默认收货地址
     */
    private void loadDefaultAddress() {
        try {
            selectedAddress = repository.getDefaultAddress(userId);
            if (selectedAddress != null) {
                displayAddress();
            } else {
                displayNoAddress();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadDefaultAddress: ", e);
            displayNoAddress();
            ToastUtils.show(this, "获取地址失败：" + e.getMessage());
        }
    }

    /**
     * 显示收货地址信息
     */
    private void displayAddress() {
        if (selectedAddress != null) {
            tvNoAddress.setVisibility(View.GONE);
            tvReceiverNamePhone.setVisibility(View.VISIBLE);
            tvFullAddress.setVisibility(View.VISIBLE);

            tvReceiverNamePhone.setText(selectedAddress.getReceiverName() + " " + selectedAddress.getReceiverPhone());
            tvFullAddress.setText(selectedAddress.getFullAddress()); // 使用 Address 模型中的 getFullAddress 方法

            if (selectedAddress.isDefault()) {
                tvDefaultTag.setVisibility(View.VISIBLE);
            } else {
                tvDefaultTag.setVisibility(View.GONE);
            }
        } else {
            displayNoAddress();
        }
    }

    /**
     * 显示无地址或选择地址的提示
     */
    private void displayNoAddress() {
        tvNoAddress.setVisibility(View.VISIBLE);
        tvReceiverNamePhone.setVisibility(View.GONE);
        tvFullAddress.setVisibility(View.GONE);
        tvDefaultTag.setVisibility(View.GONE);
    }


    /**
     * 显示订单中的商品项
     */
    private void displayOrderItems() {
        llOrderItems.removeAllViews(); // 清空旧视图
        itemsTotalAmount = 0.0; // 重置总价

        LayoutInflater inflater = LayoutInflater.from(this);
        for (CartItem item : selectedItems) {
            if (item == null || item.getProduct() == null) continue;

            // 为每个商品项创建一个简单的视图
            View itemView = inflater.inflate(R.layout.item_order_confirm_product, llOrderItems, false); // 创建一个简单的item布局

            ImageView ivProduct = itemView.findViewById(R.id.ivOrderItemImage); // ID需要和item布局一致
            TextView tvName = itemView.findViewById(R.id.tvOrderItemName);
            TextView tvPrice = itemView.findViewById(R.id.tvOrderItemPrice);
            TextView tvQuantity = itemView.findViewById(R.id.tvOrderItemQuantity);
            TextView tvItemTotal = itemView.findViewById(R.id.tvOrderItemTotal);

            tvName.setText(item.getProduct().getProductName());
            tvPrice.setText(PriceUtils.format(item.getProduct().getPrice()));
            tvQuantity.setText("x" + item.getQuantity());
            double itemTotal = item.getProduct().getPrice() * item.getQuantity();
            tvItemTotal.setText(PriceUtils.format(itemTotal));

            Glide.with(this)
                    .load(item.getProduct().getMainImage())
                    .placeholder(R.color.color_surface_elevated)
                    .error(R.color.color_surface_elevated)
                    .into(ivProduct);

            llOrderItems.addView(itemView);
            itemsTotalAmount += itemTotal;
        }
    }

    /**
     * 计算并显示总金额
     */
    private void calculateAndDisplayTotal() {
        tvItemsTotalAmount.setText(PriceUtils.format(itemsTotalAmount));
        tvShippingFee.setText(PriceUtils.format(shippingFee)); // 显示运费
        double finalTotal = itemsTotalAmount + shippingFee;
        tvFinalTotalAmount.setText(PriceUtils.format(finalTotal));
    }

    private void setupSubmitButton() {
        btnSubmitOrder.setOnClickListener(v -> submitOrder());
    }

    /**
     * 提交订单
     */
    private void submitOrder() {
        if (selectedAddress == null) {
            ToastUtils.show(this, "请选择收货地址");
            return;
        }

        btnSubmitOrder.setEnabled(false);
        btnSubmitOrder.setText("提交中...");

        try {
            Order order = repository.createOrder(
                    userId,
                    selectedAddress,
                    selectedItems,
                    etRemark.getText() != null ? etRemark.getText().toString().trim() : "",
                    shippingFee
            );
            ToastUtils.show(this, "订单创建成功！");
            Intent intent = new Intent(this, OrderListActivity.class);
            intent.putExtra(OrderListActivity.EXTRA_INITIAL_STATUS, Constants.ORDER_STATUS_PENDING);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "submitOrder: ", e);
            ToastUtils.show(this, "订单创建失败：" + e.getMessage());
            btnSubmitOrder.setEnabled(true);
            btnSubmitOrder.setText("提交订单");
        }
    }

    /**
     * 需要在 AndroidManifest.xml 中注册此 Activity
     * <activity android:name=".ui.order.OrderConfirmActivity" android:screenOrientation="portrait"/>
     */

    /**
     * 需要创建一个 item 布局文件: res/layout/item_order_confirm_product.xml
     * 内容大致如下：
     * <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" ...>
     * <ImageView android:id="@+id/ivOrderItemImage" .../>
     * <LinearLayout orientation="vertical" ...>
     * <TextView android:id="@+id/tvOrderItemName" .../>
     * <TextView android:id="@+id/tvOrderItemPrice" .../>
     * </LinearLayout>
     * <TextView android:id="@+id/tvOrderItemQuantity" .../>
     * <TextView android:id="@+id/tvOrderItemTotal" .../>
     * </LinearLayout>
     */
}
