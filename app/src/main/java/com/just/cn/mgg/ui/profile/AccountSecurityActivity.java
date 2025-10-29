package com.just.cn.mgg.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.just.cn.mgg.R;
import com.just.cn.mgg.utils.ToastUtils;

/**
 * 账号安全设置
 */
public class AccountSecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        initToolbar();
        initMenu();
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initMenu() {
        View itemChangePassword = findViewById(R.id.itemChangePassword);
        View itemTwoFactor = findViewById(R.id.itemTwoFactor);
        View itemLoginHistory = findViewById(R.id.itemLoginHistory);

        setupMenuItem(itemChangePassword, R.drawable.ic_heritage_artifacts, R.string.account_security_change_password, R.string.feature_in_development);
        setupMenuItem(itemTwoFactor, R.drawable.ic_heritage_inheritor, R.string.account_security_two_factor, R.string.feature_in_development);
        setupMenuItem(itemLoginHistory, R.drawable.ic_chinese_border_global, R.string.account_security_login_history, R.string.feature_in_development);

        View.OnClickListener listener = v -> ToastUtils.show(this, getString(R.string.feature_in_development));
        itemChangePassword.setOnClickListener(listener);
        itemTwoFactor.setOnClickListener(listener);
        itemLoginHistory.setOnClickListener(listener);
    }

    private void setupMenuItem(View itemView, int iconRes, int titleRes, int descRes) {
        ImageView icon = itemView.findViewById(R.id.ivMenuIcon);
        TextView title = itemView.findViewById(R.id.tvMenuTitle);
        TextView desc = itemView.findViewById(R.id.tvMenuDescription);
        icon.setImageResource(iconRes);
        title.setText(titleRes);
        desc.setText(descRes);
        desc.setVisibility(View.VISIBLE);
    }
}
