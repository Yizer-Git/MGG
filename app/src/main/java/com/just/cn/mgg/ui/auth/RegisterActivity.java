package com.just.cn.mgg.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.User;
import com.just.cn.mgg.data.remote.ApiService;
import com.just.cn.mgg.data.remote.RetrofitClient;
import com.just.cn.mgg.data.remote.response.ApiResponse;
import com.just.cn.mgg.utils.NetworkUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册界面
 */
public class RegisterActivity extends AppCompatActivity {
    
    private TextInputEditText etPhone, etCode, etPassword, etConfirmPassword;
    private MaterialButton btnGetCode, btnRegister;
    
    private ApiService apiService;
    private CountDownTimer countDownTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        initListeners();
        
        apiService = RetrofitClient.getInstance().getApiService();
    }
    
    private void initViews() {
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnGetCode = findViewById(R.id.btnGetCode);
        btnRegister = findViewById(R.id.btnRegister);
    }
    
    private void initListeners() {
        // 获取验证码
        btnGetCode.setOnClickListener(v -> getVerifyCode());
        
        // 注册
        btnRegister.setOnClickListener(v -> register());
        
        // 返回登录
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
    }
    
    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        String phone = etPhone.getText().toString().trim();
        
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(this, "请输入手机号");
            return;
        }
        
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            ToastUtils.showShort(this, "手机号格式不正确");
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showShort(this, getString(R.string.network_error));
            return;
        }
        
        btnGetCode.setEnabled(false);
        
        apiService.sendVerifyCode(phone).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, 
                                 Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        ToastUtils.showShort(RegisterActivity.this, "验证码已发送");
                        startCountDown();
                    } else {
                        btnGetCode.setEnabled(true);
                        ToastUtils.showShort(RegisterActivity.this, apiResponse.getMessage());
                    }
                } else {
                    btnGetCode.setEnabled(true);
                    ToastUtils.showShort(RegisterActivity.this, getString(R.string.server_error));
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                btnGetCode.setEnabled(true);
                ToastUtils.showShort(RegisterActivity.this, getString(R.string.network_error));
            }
        });
    }
    
    /**
     * 开始倒计时
     */
    private void startCountDown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnGetCode.setText(String.format("%ds", millisUntilFinished / 1000));
            }
            
            @Override
            public void onFinish() {
                btnGetCode.setEnabled(true);
                btnGetCode.setText(getString(R.string.get_code));
            }
        }.start();
    }
    
    /**
     * 注册
     */
    private void register() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        if (!validateInput(phone, code, password, confirmPassword)) {
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showShort(this, getString(R.string.network_error));
            return;
        }
        
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");
        
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("password", password);
        
        apiService.register(params).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, 
                                 Response<ApiResponse<User>> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText(getString(R.string.register));
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        ToastUtils.showShort(RegisterActivity.this, "注册成功，请登录");
                        finish();
                    } else {
                        ToastUtils.showShort(RegisterActivity.this, apiResponse.getMessage());
                    }
                } else {
                    ToastUtils.showShort(RegisterActivity.this, getString(R.string.server_error));
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText(getString(R.string.register));
                ToastUtils.showShort(RegisterActivity.this, getString(R.string.network_error));
            }
        });
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput(String phone, String code, String password, String confirmPassword) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(this, "请输入手机号");
            return false;
        }
        
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            ToastUtils.showShort(this, "手机号格式不正确");
            return false;
        }
        
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showShort(this, "请输入验证码");
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showShort(this, "请输入密码");
            return false;
        }
        
        if (password.length() < 6) {
            ToastUtils.showShort(this, "密码长度不能少于6位");
            return false;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            ToastUtils.showShort(this, "请确认密码");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            ToastUtils.showShort(this, "两次密码输入不一致");
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

