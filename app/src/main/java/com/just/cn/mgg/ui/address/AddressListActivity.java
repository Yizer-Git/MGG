package com.just.cn.mgg.ui.address;

import android.app.Activity;
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
import com.google.android.material.button.MaterialButton;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.Address;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.List;

public class AddressListActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionListener {

    private static final String TAG = "AddressListActivity";
    public static final String EXTRA_IS_SELECTING = "is_selecting";
    public static final String EXTRA_SELECTED_ADDRESS = "selected_address";

    private MaterialToolbar toolbar;
    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;
    private MaterialButton btnAddAddress;
    private TextView tvNoAddresses;
    private ProgressBar progressBar;

    private LocalRepository repository;
    private int userId;
    private boolean isSelectingMode = false;

    // 用于接收从编辑/添加页面返回的结果
    private ActivityResultLauncher<Intent> addressEditLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        isSelectingMode = getIntent().getBooleanExtra(EXTRA_IS_SELECTING, false);
        repository = new LocalRepository(this);
        userId = SPUtils.getInt(this, Constants.KEY_USER_ID, 0);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupAddButton();
        setupAddressEditLauncher();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses(); // 每次返回都刷新列表
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvAddresses = findViewById(R.id.rvAddresses);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        tvNoAddresses = findViewById(R.id.tvNoAddresses);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        if (isSelectingMode) {
            toolbar.setTitle("选择收货地址");
        }
    }

    private void setupRecyclerView() {
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(this, isSelectingMode);
        addressAdapter.setListener(this);
        rvAddresses.setAdapter(addressAdapter);
    }

    private void setupAddButton() {
        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressEditActivity.class);
            // 不传递 address 表示是新增
            addressEditLauncher.launch(intent);
        });
    }

    private void setupAddressEditLauncher() {
        addressEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 不论是添加还是编辑成功，都刷新列表
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadAddresses();
                    }
                });
    }

    private void loadAddresses() {
        userId = SPUtils.getInt(this, Constants.KEY_USER_ID, 0);
        if (!SPUtils.isLogin(this) || userId == 0) {
            ToastUtils.show(this, "请先登录");
            finish();
            return;
        }

        showLoading();
        try {
            List<Address> addresses = repository.getAddresses(userId);
            if (addresses == null || addresses.isEmpty()) {
                showEmptyView();
            } else {
                showAddressList(addresses);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load addresses", e);
            ToastUtils.show(this, "加载失败：" + e.getMessage());
            showEmptyView();
        } finally {
            hideLoading();
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvAddresses.setVisibility(View.GONE);
        tvNoAddresses.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        rvAddresses.setVisibility(View.GONE);
        tvNoAddresses.setVisibility(View.VISIBLE);
        addressAdapter.setAddressList(null); // 清空适配器
    }

    private void showAddressList(List<Address> addresses) {
        rvAddresses.setVisibility(View.VISIBLE);
        tvNoAddresses.setVisibility(View.GONE);
        addressAdapter.setAddressList(addresses);
    }


    // --- AddressAdapter.OnAddressActionListener 实现 ---

    @Override
    public void onEditClick(Address address, int position) {
        Intent intent = new Intent(this, AddressEditActivity.class);
        intent.putExtra(AddressEditActivity.EXTRA_ADDRESS_TO_EDIT, address); // 传递地址对象进行编辑
        addressEditLauncher.launch(intent);
    }

    @Override
    public void onItemClick(Address address, int position) {
        if (isSelectingMode) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_ADDRESS, address); // 返回选中的地址
            setResult(Activity.RESULT_OK, resultIntent);
            finish(); // 关闭当前页面
        }
        // 非选择模式下可以考虑进入地址详情等
    }
}
