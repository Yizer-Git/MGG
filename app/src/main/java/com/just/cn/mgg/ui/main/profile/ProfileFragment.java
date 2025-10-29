package com.just.cn.mgg.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.just.cn.mgg.R;
import com.just.cn.mgg.ui.address.AddressListActivity; // Import
import com.just.cn.mgg.ui.auth.LoginActivity;
import com.just.cn.mgg.ui.profile.AccountSecurityActivity;
import com.just.cn.mgg.ui.profile.FeedbackActivity;
import com.just.cn.mgg.ui.profile.InvoiceActivity;
import com.just.cn.mgg.ui.profile.PersonalInfoActivity;
import com.just.cn.mgg.ui.profile.PhoneBindingActivity;
import com.just.cn.mgg.ui.order.OrderListActivity; // Import
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils; // Import
import com.just.cn.mgg.utils.ToastUtils; // Import
import com.google.android.material.imageview.ShapeableImageView;

/**
 * 个人中心Fragment
 */
public class ProfileFragment extends Fragment {

    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvPhone;
    private LinearLayout llMyOrders, llAddressManagement;
    private LinearLayout llOrderStatusPending, llOrderStatusShipped, llOrderStatusDone;
    private View itemPersonalInfo, itemAccountSecurity, itemPhoneBinding;
    private View itemPushNotification, itemActivityPromotion, itemLocationService;
    private View itemInvoice, itemFeedback;
    private SwitchCompat swPushNotification, swActivityPromotion, swLocationService;
    private CompoundButton.OnCheckedChangeListener pushSwitchListener;
    private CompoundButton.OnCheckedChangeListener activitySwitchListener;
    private CompoundButton.OnCheckedChangeListener locationSwitchListener;
    private View btnLogout;

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

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvPhone = view.findViewById(R.id.tvPhone);

        // 订单入口
        llMyOrders = view.findViewById(R.id.llMyOrders);
        llOrderStatusPending = view.findViewById(R.id.llOrderStatusPending);
        llOrderStatusShipped = view.findViewById(R.id.llOrderStatusShipped);
        llOrderStatusDone = view.findViewById(R.id.llOrderStatusDone);

        // 地址管理
        llAddressManagement = view.findViewById(R.id.llAddressManagement);
        btnLogout = view.findViewById(R.id.btnLogout);

        itemPersonalInfo = view.findViewById(R.id.itemPersonalInfo);
        itemAccountSecurity = view.findViewById(R.id.itemAccountSecurity);
        itemPhoneBinding = view.findViewById(R.id.itemPhoneBinding);
        itemPushNotification = view.findViewById(R.id.itemPushNotification);
        itemActivityPromotion = view.findViewById(R.id.itemActivityPromotion);
        itemLocationService = view.findViewById(R.id.itemLocationService);
        itemInvoice = view.findViewById(R.id.itemInvoice);
        itemFeedback = view.findViewById(R.id.itemFeedback);

        if (itemPushNotification != null) {
            swPushNotification = itemPushNotification.findViewById(R.id.switchMenu);
        }
        if (itemActivityPromotion != null) {
            swActivityPromotion = itemActivityPromotion.findViewById(R.id.switchMenu);
        }
        if (itemLocationService != null) {
            swLocationService = itemLocationService.findViewById(R.id.switchMenu);
        }

        setupMenuContent();
        initSwitchHandlers();
        // 设置用户名 (或 "点击登录")
        setupUsername();

        // 设置点击事件
        setupClickListeners();
        refreshSwitchStates();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次返回页面时检查登录状态
        setupUsername();
        refreshSwitchStates();
    }

    private void setupUsername() {
        if (getContext() == null) return;
        if (!SPUtils.isLogin(getContext())) {
            tvUsername.setText(getString(R.string.profile_click_login));
            if (tvPhone != null) {
                tvPhone.setText(getString(R.string.profile_subtitle));
            }
            if (ivAvatar != null) {
                ivAvatar.setImageResource(R.drawable.profile_header_photo);
            }
            View.OnClickListener loginListener = v -> {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            };
            tvUsername.setOnClickListener(loginListener);
            if (ivAvatar != null) {
                ivAvatar.setOnClickListener(loginListener);
            }
            return;
        }

        String username = SPUtils.getUsername(getContext());
        if (TextUtils.isEmpty(username)) {
            tvUsername.setText(getString(R.string.profile_guest));
        } else {
            tvUsername.setText(username);
        }
        tvUsername.setOnClickListener(v -> openPersonalInfo());

        if (tvPhone != null) {
            String phone = SPUtils.getString(getContext(), Constants.KEY_PHONE);
            if (TextUtils.isEmpty(phone)) {
                tvPhone.setText(getString(R.string.profile_subtitle));
            } else {
                tvPhone.setText(phone);
            }
        }

        if (ivAvatar != null) {
            String avatar = SPUtils.getUserAvatar(getContext());
            if (TextUtils.isEmpty(avatar)) {
                avatar = SPUtils.getString(getContext(), Constants.KEY_AVATAR);
            }
            if (TextUtils.isEmpty(avatar)) {
                ivAvatar.setImageResource(R.drawable.profile_header_photo);
            } else {
                ivAvatar.setImageURI(Uri.parse(avatar));
            }
            ivAvatar.setOnClickListener(v -> openPersonalInfo());
        }
    }

    private void openPersonalInfo() {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), PersonalInfoActivity.class);
        startActivity(intent);
    }

    private void setupMenuContent() {
        configureMenuItem(itemPersonalInfo, R.drawable.ic_ancient_desk,
                R.string.profile_item_personal_info, R.string.profile_item_personal_info_desc);
        configureMenuItem(itemAccountSecurity, R.drawable.ic_heritage_artifacts,
                R.string.profile_item_account_security, R.string.profile_item_account_security_desc);
        configureMenuItem(itemPhoneBinding, R.drawable.ic_temple,
                R.string.profile_item_phone_binding, R.string.profile_item_phone_binding_desc);
        configureSwitchItem(itemPushNotification, R.drawable.ic_sky_lantern,
                R.string.profile_item_push_notifications, R.string.profile_item_push_notifications_desc);
        configureSwitchItem(itemActivityPromotion, R.drawable.ic_bamboo,
                R.string.profile_item_activity_promotions, R.string.profile_item_activity_promotions_desc);
        configureSwitchItem(itemLocationService, R.drawable.ic_heritage_entrance,
                R.string.profile_item_location_service, R.string.profile_item_location_service_desc);
        configureMenuItem(itemInvoice, R.drawable.ic_ancient_book,
                R.string.profile_item_invoice, R.string.profile_item_invoice_desc);
        configureMenuItem(itemFeedback, R.drawable.ic_culture,
                R.string.profile_item_feedback, R.string.profile_item_feedback_desc);
    }

    private void configureMenuItem(View itemView, int iconRes, int titleRes, int descRes) {
        if (itemView == null) {
            return;
        }
        ImageView icon = itemView.findViewById(R.id.ivMenuIcon);
        TextView title = itemView.findViewById(R.id.tvMenuTitle);
        TextView desc = itemView.findViewById(R.id.tvMenuDescription);
        View arrow = itemView.findViewById(R.id.ivArrow);
        if (icon != null) {
            icon.setImageResource(iconRes);
        }
        if (title != null) {
            title.setText(titleRes);
        }
        if (desc != null) {
            desc.setText(descRes);
            desc.setVisibility(View.VISIBLE);
        }
        if (arrow != null) {
            arrow.setVisibility(View.VISIBLE);
        }
    }

    private void configureSwitchItem(View itemView, int iconRes, int titleRes, int descRes) {
        if (itemView == null) {
            return;
        }
        ImageView icon = itemView.findViewById(R.id.ivMenuIcon);
        TextView title = itemView.findViewById(R.id.tvMenuTitle);
        TextView desc = itemView.findViewById(R.id.tvMenuDescription);
        if (icon != null) {
            icon.setImageResource(iconRes);
        }
        if (title != null) {
            title.setText(titleRes);
        }
        if (desc != null) {
            desc.setText(descRes);
            desc.setVisibility(View.VISIBLE);
        }
    }

    private void initSwitchHandlers() {
        if (swPushNotification != null) {
            pushSwitchListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    handleSwitchChange(swPushNotification, isChecked, this, () -> {
                        if (getContext() != null) {
                            SPUtils.setPushNotificationEnabled(getContext(), isChecked);
                        }
                    });
                }
            };
            swPushNotification.setOnCheckedChangeListener(pushSwitchListener);
        }

        if (swActivityPromotion != null) {
            activitySwitchListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    handleSwitchChange(swActivityPromotion, isChecked, this, () -> {
                        if (getContext() != null) {
                            SPUtils.setActivityPromotionEnabled(getContext(), isChecked);
                        }
                    });
                }
            };
            swActivityPromotion.setOnCheckedChangeListener(activitySwitchListener);
        }

        if (swLocationService != null) {
            locationSwitchListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    handleSwitchChange(swLocationService, isChecked, this, () -> {
                        if (getContext() != null) {
                            SPUtils.setLocationServiceEnabled(getContext(), isChecked);
                        }
                    });
                }
            };
            swLocationService.setOnCheckedChangeListener(locationSwitchListener);
        }
    }

    private void handleSwitchChange(SwitchCompat switchCompat, boolean isChecked,
                                    CompoundButton.OnCheckedChangeListener listener,
                                    Runnable persistAction) {
        if (getContext() == null || switchCompat == null) {
            return;
        }
        if (!SPUtils.isLogin(getContext())) {
            ToastUtils.show(getContext(), getString(R.string.common_please_login));
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            switchCompat.setOnCheckedChangeListener(null);
            switchCompat.setChecked(!isChecked);
            switchCompat.setOnCheckedChangeListener(listener);
            return;
        }
        persistAction.run();
    }

    private void refreshSwitchStates() {
        if (getContext() == null) {
            return;
        }
        if (swPushNotification != null) {
            swPushNotification.setOnCheckedChangeListener(null);
            swPushNotification.setChecked(SPUtils.isPushNotificationEnabled(getContext()));
            if (pushSwitchListener != null) {
                swPushNotification.setOnCheckedChangeListener(pushSwitchListener);
            }
        }
        if (swActivityPromotion != null) {
            swActivityPromotion.setOnCheckedChangeListener(null);
            swActivityPromotion.setChecked(SPUtils.isActivityPromotionEnabled(getContext()));
            if (activitySwitchListener != null) {
                swActivityPromotion.setOnCheckedChangeListener(activitySwitchListener);
            }
        }
        if (swLocationService != null) {
            swLocationService.setOnCheckedChangeListener(null);
            swLocationService.setChecked(SPUtils.isLocationServiceEnabled(getContext()));
            if (locationSwitchListener != null) {
                swLocationService.setOnCheckedChangeListener(locationSwitchListener);
            }
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

        if (itemPersonalInfo != null) {
            itemPersonalInfo.setOnClickListener(v -> {
                if (checkLogin()) {
                    openPersonalInfo();
                }
            });
        }

        if (itemAccountSecurity != null) {
            itemAccountSecurity.setOnClickListener(v -> {
                if (checkLogin()) {
                    Intent intent = new Intent(getContext(), AccountSecurityActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (itemPhoneBinding != null) {
            itemPhoneBinding.setOnClickListener(v -> {
                if (checkLogin()) {
                    Intent intent = new Intent(getContext(), PhoneBindingActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (itemPushNotification != null && swPushNotification != null) {
            itemPushNotification.setOnClickListener(v -> {
                if (!checkLogin()) {
                    return;
                }
                swPushNotification.toggle();
            });
        }

        if (itemActivityPromotion != null && swActivityPromotion != null) {
            itemActivityPromotion.setOnClickListener(v -> {
                if (!checkLogin()) {
                    return;
                }
                swActivityPromotion.toggle();
            });
        }

        if (itemLocationService != null && swLocationService != null) {
            itemLocationService.setOnClickListener(v -> {
                if (!checkLogin()) {
                    return;
                }
                swLocationService.toggle();
            });
        }

        if (itemInvoice != null) {
            itemInvoice.setOnClickListener(v -> {
                if (checkLogin()) {
                    Intent intent = new Intent(getContext(), InvoiceActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (itemFeedback != null) {
            itemFeedback.setOnClickListener(v -> {
                if (checkLogin()) {
                    Intent intent = new Intent(getContext(), FeedbackActivity.class);
                    startActivity(intent);
                }
            });
        }

        btnLogout.setOnClickListener(v -> {
            if (checkLogin()) {
                performLogout();
            }
        });
    }

    /**
     * 检查是否登录，未登录则提示
     * @return true if logged in, false otherwise
     */
    private boolean checkLogin() {
        if (getContext() == null) return false;
        if (!SPUtils.isLogin(getContext())) {
            ToastUtils.show(getContext(), getString(R.string.common_please_login));
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    // 新增具体退出处理
    private void performLogout() {
        if (getContext() == null) return;
        SPUtils.clear(getContext());
        ToastUtils.show(getContext(), getString(R.string.logout_success));
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
