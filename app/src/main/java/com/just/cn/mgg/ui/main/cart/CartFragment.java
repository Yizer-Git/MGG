package com.just.cn.mgg.ui.main.cart;

import android.app.AlertDialog; // <-- Import AlertDialog
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.CartItem;
import com.just.cn.mgg.ui.adapter.CartAdapter;
import com.just.cn.mgg.ui.order.OrderConfirmActivity;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.PriceUtils;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车Fragment
 */
public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {

    private static final String TAG = "CartFragment";

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private LocalRepository repository;
    private TextView tvEmptyCart;
    private ProgressBar progressBar;
    private CheckBox cbSelectAll;
    private TextView tvTotalPrice;
    private Button btnSettle;
    private List<CartItem> currentCartItems = new ArrayList<>();
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new LocalRepository(requireContext());
        initViews(view);
        setupRecyclerView();
        setupBottomBarListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData();
    }

    private void initViews(View view) {
        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        progressBar = view.findViewById(R.id.progressBar);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnSettle = view.findViewById(R.id.btnSettle);

        MaterialToolbar toolbar = view.findViewById(R.id.cartToolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v ->
                    requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext());
        cartAdapter.setListener(this);
        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupBottomBarListeners() {
        // 全选/取消全选 (监听器逻辑已移至 updateBottomBar 内部处理)
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // 只有用户手动点击时才触发
                selectAllItems(isChecked);
            }
        });


        // 结算按钮
        btnSettle.setOnClickListener(v -> {
            if (getContext() == null) return;
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : currentCartItems) {
                if (item.getSelected() != null && item.getSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                ToastUtils.show(getContext(), "请选择要结算的商品");
                return;
            }

            Intent intent = new Intent(getContext(), OrderConfirmActivity.class);
            intent.putExtra("selected_items", (Serializable) selectedItems);
            startActivity(intent);
        });
    }

    private void loadCartData() {
        // ... (loadCartData 方法保持不变) ...
        if (getContext() == null) return;

        if (!SPUtils.isLogin(getContext())) {
            ToastUtils.show(getContext(), "请先登录");
            showEmptyView();
            updateBottomBar(new ArrayList<>());
            return;
        }

        userId = SPUtils.getInt(getContext(), Constants.KEY_USER_ID);
        if (userId == 0) {
            ToastUtils.show(getContext(), "请先登录");
            showEmptyView();
            updateBottomBar(new ArrayList<>());
            return;
        }

        showLoading();
        try {
            currentCartItems = repository.getCartItems(userId);
            if (currentCartItems == null || currentCartItems.isEmpty()) {
                showEmptyView();
            } else {
                showCartList(currentCartItems);
                updateBottomBar(currentCartItems);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadCartData: ", e);
            ToastUtils.show(getContext(), "加载购物车失败：" + e.getMessage());
            showEmptyView();
            updateBottomBar(new ArrayList<>());
        } finally {
            hideLoading();
        }
    }


    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvCartItems.setVisibility(View.GONE);
        tvEmptyCart.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        rvCartItems.setVisibility(View.GONE);
        tvEmptyCart.setVisibility(View.VISIBLE);
        currentCartItems.clear(); // 清空内存中的列表
        cartAdapter.setCartItems(new ArrayList<>());
        updateBottomBar(new ArrayList<>()); // 更新底部栏确保状态正确
    }

    private void showCartList(List<CartItem> items) {
        rvCartItems.setVisibility(View.VISIBLE);
        tvEmptyCart.setVisibility(View.GONE);
        cartAdapter.setCartItems(items);
    }


    private void updateBottomBar(List<CartItem> items) {
        // ... (updateBottomBar 方法前半部分不变) ...
        if (items == null) { // 添加空值检查
            items = new ArrayList<>();
        }

        double totalPrice = 0;
        int selectedCount = 0;
        boolean allSelected = !items.isEmpty(); // 初始假设全选（如果列表不为空）

        for (CartItem item : items) {
            if (item == null || item.getProduct() == null) continue;
            if (item.getSelected() != null && item.getSelected()) {
                totalPrice += item.getProduct().getPrice() * item.getQuantity();
                selectedCount++;
            } else {
                allSelected = false;
            }
        }

        tvTotalPrice.setText(PriceUtils.format(totalPrice));

        // 更新全选框状态
        cbSelectAll.setOnCheckedChangeListener(null); // 暂时移除监听器
        cbSelectAll.setChecked(allSelected);
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // 只有用户手动点击时才触发
                selectAllItems(isChecked);
            }
        }); // 重新设置监听器


        // 更新结算按钮状态和文本
        if (selectedCount > 0) {
            btnSettle.setText(getString(R.string.settle) + "(" + selectedCount + ")");
            btnSettle.setEnabled(true);
        } else {
            btnSettle.setText(getString(R.string.settle));
            btnSettle.setEnabled(false);
        }
    }


    private void selectAllItems(boolean select) {
        // ... (selectAllItems 方法不变) ...
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            return;
        }
        boolean changed = false;
        for (CartItem item : currentCartItems) {
            if (item == null) continue;
            if (item.getSelected() == null || item.getSelected() != select) {
                item.setSelected(select);
                saveCartItem(item);
                changed = true;
            }
        }
        if (changed) {
            cartAdapter.notifyDataSetChanged();
        }
        updateBottomBar(currentCartItems);
    }

    // --- CartAdapter.OnCartItemChangeListener 实现 ---

    @Override
    public void onSelectChange(CartItem item, boolean selected, int position) {
        if (item == null) return;
        item.setSelected(selected);
        saveCartItem(item);
        updateBottomBar(currentCartItems);
        // 可选：仅刷新改变的项，提高性能
        // cartAdapter.notifyItemChanged(position);
    }

    @Override
    public void onQuantityChange(CartItem item, int newQuantity, int position) {
        if (item == null || item.getQuantity() == newQuantity) return;
        item.setQuantity(newQuantity);
        saveCartItem(item);
        updateBottomBar(currentCartItems);
        // 更新具体项的UI
        cartAdapter.notifyItemChanged(position);
    }

    // ******** 实现删除功能 ********
    @Override
    public void onDelete(CartItem item, int position) {
        if (getContext() == null || item == null || item.getProductId() == null) return;

        // 弹出确认对话框
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除")
                .setMessage("确定要删除商品 “" + (item.getProduct() != null ? item.getProduct().getProductName() : "") + "” 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 用户确认删除，调用API
                    deleteCartItem(item, position);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    // ****************************

    private void saveCartItem(CartItem item) {
        if (item == null) {
            return;
        }
        try {
            repository.updateCartItem(item);
        } catch (Exception e) {
            Log.e(TAG, "saveCartItem: ", e);
            ToastUtils.show(getContext(), "更新购物车失败：" + e.getMessage());
            loadCartData();
        }
    }

    /**
     * 调用API删除购物车项
     */
    private void deleteCartItem(CartItem item, int position) {
        if (item == null) {
            return;
        }
        try {
            repository.deleteCartItem(item);
            if (position >= 0 && position < currentCartItems.size()) {
                currentCartItems.remove(position);
                cartAdapter.notifyItemRemoved(position);
                if (position < currentCartItems.size()) {
                    cartAdapter.notifyItemRangeChanged(position, currentCartItems.size() - position);
                }
            } else {
                loadCartData();
                return;
            }
            updateBottomBar(currentCartItems);
            if (currentCartItems.isEmpty()) {
                showEmptyView();
            }
            ToastUtils.show(getContext(), "删除成功");
        } catch (Exception e) {
            Log.e(TAG, "deleteCartItem: ", e);
            ToastUtils.show(getContext(), "删除失败：" + e.getMessage());
            loadCartData();
        }
    }
}
