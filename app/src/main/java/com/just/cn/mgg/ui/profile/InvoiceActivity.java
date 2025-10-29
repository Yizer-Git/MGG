package com.just.cn.mgg.ui.profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.just.cn.mgg.R;
import com.just.cn.mgg.utils.ToastUtils;

/**
 * 发票与报销页面
 */
public class InvoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        initToolbar();
        initActions();
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initActions() {
        MaterialButton btnAddHeader = findViewById(R.id.btnAddInvoiceHeader);
        btnAddHeader.setOnClickListener(v ->
                ToastUtils.show(this, getString(R.string.feature_in_development)));
    }
}
