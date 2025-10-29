package com.just.cn.mgg.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.just.cn.mgg.R;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

/**
 * 绑定手机号页面
 */
public class PhoneBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_binding);

        initToolbar();
        initViews();
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        TextView tvCurrentPhone = findViewById(R.id.tvCurrentPhone);
        MaterialButton btnChangePhone = findViewById(R.id.btnChangePhone);

        String phone = SPUtils.getString(this, Constants.KEY_PHONE);
        if (TextUtils.isEmpty(phone)) {
            tvCurrentPhone.setText(getString(R.string.phone_binding_not_bound));
        } else {
            tvCurrentPhone.setText(getString(R.string.phone_binding_current) + "：" + phone);
        }

        btnChangePhone.setOnClickListener(v ->
                ToastUtils.show(this, getString(R.string.phone_binding_change_todo)));
    }
}
