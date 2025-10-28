package com.just.cn.mgg.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.just.cn.mgg.R;
import com.just.cn.mgg.ui.address.AddressListActivity; // Import
import com.just.cn.mgg.ui.auth.LoginActivity;
import com.just.cn.mgg.ui.order.OrderListActivity; // Import
import com.just.cn.mgg.utils.SPUtils; // Import
import com.just.cn.mgg.utils.ToastUtils; // Import

/**
 * 个人中心Fragment
 */
public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private LinearLayout llMyOrders, llAddressManagement;
    private LinearLayout llOrderStatusPending, llOrderStatusShipped, llOrderStatusDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);

        // 订单入口
        llMyOrders = view.findViewById(R.id.llMyOrders);
        llOrderStatusPending = view.findViewById(R.id.llOrderStatusPending);
        llOrderStatusShipped = view.findViewById(R.id.llOrderStatusShipped);
        llOrderStatusDone = view.findViewById(R.id.llOrderStatusDone);

        // 地址管理
        llAddressManagement = view.findViewById(R.id.llAddressManagement);

        // 设置用户名 (或 "点击登录")
        setupUsername();

        // 设置点击事件
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次返回页面时检查登录状态
        setupUsername();
    }

    private void setupUsername() {
        if (getContext() == null) return;
        String token = SPUtils.getAuthToken(getContext());
        if (token.isEmpty()) {
            tvUsername.setText("点击登录");
            tvUsername.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            });
        } else {
            // TODO: 调用 getUserInfo 接口获取真实用户名
            String username = SPUtils.getUsername(getContext()); // 假设SP中保存了用户名
            if (username.isEmpty()) {
                tvUsername.setText("尊贵的用户");
            } else {
                tvUsername.setText(username);
            }
            tvUsername.setOnClickListener(null); // 已登录，移除点击登录事件
        }
    }

    private void setupClickListeners() {
        if (getContext() == null) return;

        // "我的订单" (查看全部)
        llMyOrders.setOnClickListener(v -> {
            if (checkLogin()) {
                Intent intent = new Intent(getContext(), OrderListActivity.class);
                intent.putExtra(OrderListActivity.EXTRA_INITIAL_STATUS, (Integer) null); // 全部
                startActivity(intent);
            }
        });

        // "待付款"
        llOrderStatusPending.setOnClickListener(v -> {
            if (checkLogin()) {
                Intent intent = new Intent(getContext(), OrderListActivity.class);
                intent.putExtra(OrderListActivity.EXTRA_INITIAL_STATUS, 0); // 待付款
                startActivity(intent);
            }
        });

        // "待收货" (包含了 待发货(1) 和 已发货(2))
        // 这里我们跳转到 "待发货"
        llOrderStatusShipped.setOnClickListener(v -> {
            if (checkLogin()) {
                Intent intent = new Intent(getContext(), OrderListActivity.class);
                intent.putExtra(OrderListActivity.EXTRA_INITIAL_STATUS, 1); // 待发货
                startActivity(intent);
            }
        });

        // "已完成"
        llOrderStatusDone.setOnClickListener(v -> {
            if (checkLogin()) {
                Intent intent = new Intent(getContext(), OrderListActivity.class);
                intent.putExtra(OrderListActivity.EXTRA_INITIAL_STATUS, 3); // 已完成
                startActivity(intent);
            }
        });


        // "地址管理"
        llAddressManagement.setOnClickListener(v -> {
            if (checkLogin()) {
                Intent intent = new Intent(getContext(), AddressListActivity.class);
                intent.putExtra(AddressListActivity.EXTRA_IS_SELECTING, false); // 非选择模式
                startActivity(intent);
            }
        });

        // TODO: 其他入口，如 "客服咨询", "退出登录"
    }

    /**
     * 检查是否登录，未登录则提示
     * @return true if logged in, false otherwise
     */
    private boolean checkLogin() {
        if (getContext() == null) return false;
        String token = SPUtils.getAuthToken(getContext());
        if (token.isEmpty()) {
            ToastUtils.show(getContext(), "请先登录");
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }
}
