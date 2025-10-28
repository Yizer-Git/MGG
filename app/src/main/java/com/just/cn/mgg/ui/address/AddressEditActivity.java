package com.just.cn.mgg.ui.address;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Address;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

public class AddressEditActivity extends AppCompatActivity {

    private static final String TAG = "AddressEditActivity";
    public static final String EXTRA_ADDRESS_TO_EDIT = "address_to_edit";

    private MaterialToolbar toolbar;
    private TextInputLayout tilReceiverName, tilReceiverPhone, tilRegion, tilDetailAddress;
    private TextInputEditText etReceiverName, etReceiverPhone, etRegion, etDetailAddress;
    private SwitchMaterial switchSetDefault;
    private Button btnSaveAddress, btnDeleteAddress;

    private LocalRepository repository;
    private Address existingAddress; // 用于存储正在编辑的地址
    private boolean isEditMode = false;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        repository = new LocalRepository(this);
        userId = SPUtils.getInt(this, Constants.KEY_USER_ID, 0);
        if (!SPUtils.isLogin(this) || userId == 0) {
            ToastUtils.show(this, "请先登录");
            finish();
            return;
        }

        existingAddress = (Address) getIntent().getSerializableExtra(EXTRA_ADDRESS_TO_EDIT);
        if (existingAddress != null) {
            existingAddress.setUserId(userId);
        }
        isEditMode = (existingAddress != null);

        initViews();
        setupToolbar();
        setupListeners();

        if (isEditMode) {
            populateAddressData();
        } else {
            // 新增模式
            btnDeleteAddress.setVisibility(View.GONE);
            // 可以在这里设置默认地区，例如 "四川省 乐山市 市中区"
            etRegion.setText("四川省 乐山市 市中区"); // 假设固定地区
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilReceiverName = findViewById(R.id.tilReceiverName);
        etReceiverName = findViewById(R.id.etReceiverName);
        tilReceiverPhone = findViewById(R.id.tilReceiverPhone);
        etReceiverPhone = findViewById(R.id.etReceiverPhone);
        tilRegion = findViewById(R.id.tilRegion);
        etRegion = findViewById(R.id.etRegion);
        tilDetailAddress = findViewById(R.id.tilDetailAddress);
        etDetailAddress = findViewById(R.id.etDetailAddress);
        switchSetDefault = findViewById(R.id.switchSetDefault);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        btnDeleteAddress = findViewById(R.id.btnDeleteAddress);

        // TODO: 实现地区选择器
        etRegion.setOnClickListener(v -> ToastUtils.show(this, "地区选择器待实现"));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        if (isEditMode) {
            toolbar.setTitle("编辑地址");
        } else {
            toolbar.setTitle("新增地址");
        }
    }

    /**
     * 如果是编辑模式，填充现有数据
     */
    private void populateAddressData() {
        if (existingAddress == null) return;
        etReceiverName.setText(existingAddress.getReceiverName());
        etReceiverPhone.setText(existingAddress.getReceiverPhone());
        // 假设地区信息存储在 region 字段
        etRegion.setText(existingAddress.getRegion());
        etDetailAddress.setText(existingAddress.getDetailAddress());
        switchSetDefault.setChecked(existingAddress.isDefault());
        btnDeleteAddress.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        btnSaveAddress.setOnClickListener(v -> saveAddress());
        btnDeleteAddress.setOnClickListener(v -> showDeleteConfirmation());
    }

    private boolean validateInput() {
        tilReceiverName.setError(null);
        tilReceiverPhone.setError(null);
        tilDetailAddress.setError(null);

        if (TextUtils.isEmpty(etReceiverName.getText())) {
            tilReceiverName.setError("请输入收货人姓名");
            return false;
        }
        if (TextUtils.isEmpty(etReceiverPhone.getText())) {
            tilReceiverPhone.setError("请输入手机号码");
            return false;
        }
        if (etReceiverPhone.getText().length() != 11) {
            tilReceiverPhone.setError("请输入11位手机号码");
            return false;
        }
        if (TextUtils.isEmpty(etRegion.getText())) {
            tilRegion.setError("请选择地区"); // 假设地区是必填的
            return false;
        }
        if (TextUtils.isEmpty(etDetailAddress.getText())) {
            tilDetailAddress.setError("请输入详细地址");
            return false;
        }
        return true;
    }

    private void saveAddress() {
        if (!validateInput()) {
            return;
        }

        setLoadingState(true);

        Address addressToSave = isEditMode ? existingAddress : new Address();
        addressToSave.setUserId(userId);
        addressToSave.setReceiverName(etReceiverName.getText().toString().trim());
        addressToSave.setReceiverPhone(etReceiverPhone.getText().toString().trim());
        addressToSave.setRegion(etRegion.getText().toString().trim());
        addressToSave.setDetailAddress(etDetailAddress.getText().toString().trim());
        addressToSave.setDefault(switchSetDefault.isChecked());

        try {
            Address saved = isEditMode
                    ? repository.updateAddress(addressToSave)
                    : repository.insertAddress(addressToSave);
            existingAddress = saved;
            ToastUtils.show(this, "保存成功");
            setResult(Activity.RESULT_OK);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save address", e);
            ToastUtils.show(this, "保存失败：" + e.getMessage());
            setLoadingState(false);
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("您确定要删除这个地址吗？")
                .setPositiveButton("删除", (dialog, which) -> deleteAddress())
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteAddress() {
        if (!isEditMode || existingAddress == null) {
            return;
        }

        setLoadingState(true);
        try {
            repository.deleteAddress(existingAddress);
            ToastUtils.show(this, "删除成功");
            setResult(Activity.RESULT_OK);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete address", e);
            ToastUtils.show(this, "删除失败：" + e.getMessage());
            setLoadingState(false);
        }
    }

    private void setLoadingState(boolean isLoading) {
        btnSaveAddress.setEnabled(!isLoading);
        btnDeleteAddress.setEnabled(!isLoading);
        btnSaveAddress.setText(isLoading ? "保存中..." : "保存");
    }
}
