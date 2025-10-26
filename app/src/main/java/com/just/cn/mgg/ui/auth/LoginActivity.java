package com.just.cn.mgg.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.just.cn.mgg.MainActivity;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.remote.ApiService;
import com.just.cn.mgg.data.remote.RetrofitClient;
import com.just.cn.mgg.data.remote.response.ApiResponse;
import com.just.cn.mgg.data.remote.response.LoginResponse;
import com.just.cn.mgg.data.model.User;

import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.NetworkUtils;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity {
    
    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private View tvRegister, tvForgetPassword, tvCodeLogin;
    
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        initListeners();
        
        apiService = RetrofitClient.getInstance().getApiService();
        
        // 检查是否已登录
        checkLoginStatus();
    }
    
    private void initViews() {
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvCodeLogin = findViewById(R.id.tvCodeLogin);
    }
    
    private void initListeners() {
        // 登录按钮
        btnLogin.setOnClickListener(v -> login());
        
        // 注册
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        
        // 忘记密码
        tvForgetPassword.setOnClickListener(v -> {
            ToastUtils.showShort(this, "功能开发中...");
        });
        
        // 验证码登录
        tvCodeLogin.setOnClickListener(v -> {
            ToastUtils.showShort(this, "功能开发中...");
        });
    }
    
    /**
     * 检查登录状态
     */
    private void checkLoginStatus() {
        if (SPUtils.isLogin(this)) {
            // 已登录，跳转到主界面
            goToMain();
        }
    }
    
    /**
     * 登录
     */
    private void login() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // 输入验证
        if (!validateInput(phone, password)) {
            return;
        }
        
        // 显示加载状态
        setLoginButtonState(false, "登录中...");
        
        // 模拟登录逻辑（用于测试UI，无需后端服务器）
        if (isMockLogin(phone, password)) {
            // 模拟网络延迟
            new android.os.Handler().postDelayed(() -> {
                setLoginButtonState(true, getString(R.string.login));
                
                // 创建模拟登录响应
                LoginResponse mockResponse = createMockLoginResponse(phone);
                handleLoginSuccess(mockResponse);
            }, 1000); // 1秒延迟，模拟网络请求
        } else {
            // 模拟网络延迟
            new android.os.Handler().postDelayed(() -> {
                setLoginButtonState(true, getString(R.string.login));
                ToastUtils.showShort(this, "用户名或密码错误");
            }, 1000);
        }
        
        // 真实网络登录代码（已注释，需要后端服务器时取消注释）
        /*
        // 检查网络
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showShort(this, getString(R.string.network_error));
            return;
        }
        
        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", password);
        
        // 发起网络请求
        apiService.login(params).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, 
                                 Response<ApiResponse<LoginResponse>> response) {
                setLoginButtonState(true, getString(R.string.login));
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        // 登录成功
                        handleLoginSuccess(apiResponse.getData());
                    } else {
                        // 登录失败
                        ToastUtils.showShort(LoginActivity.this, apiResponse.getMessage());
                    }
                } else {
                    ToastUtils.showShort(LoginActivity.this, getString(R.string.server_error));
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                setLoginButtonState(true, getString(R.string.login));
                ToastUtils.showShort(LoginActivity.this, 
                    getString(R.string.network_error) + ": " + t.getMessage());
            }
        });
        */
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(this, "请输入手机号");
            etPhone.requestFocus();
            return false;
        }
        
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            ToastUtils.showShort(this, "手机号格式不正确");
            etPhone.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showShort(this, "请输入密码");
            etPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            ToastUtils.showShort(this, "密码长度不能少于6位");
            etPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 处理登录成功
     */
    private void handleLoginSuccess(LoginResponse loginResponse) {
        // 保存用户信息和Token
        SPUtils.put(this, Constants.KEY_TOKEN, loginResponse.getToken());
        SPUtils.put(this, Constants.KEY_IS_LOGIN, true);
        SPUtils.put(this, Constants.KEY_USER_ID, loginResponse.getUser().getUserId());
        SPUtils.put(this, Constants.KEY_PHONE, loginResponse.getUser().getPhone());
        
        if (loginResponse.getUser().getNickname() != null) {
            SPUtils.put(this, Constants.KEY_NICKNAME, loginResponse.getUser().getNickname());
        }
        
        ToastUtils.showShort(this, "登录成功");
        
        // 跳转到主界面
        goToMain();
    }
    
    /**
     * 跳转到主界面
     */
    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * 设置登录按钮状态
     */
    private void setLoginButtonState(boolean enabled, String text) {
        btnLogin.setEnabled(enabled);
        btnLogin.setText(text);
    }
    
    /**
     * 检查是否为模拟登录
     */
    private boolean isMockLogin(String phone, String password) {
        // 模拟登录账号：手机号13800138000，密码123456
        return "13800138000".equals(phone) && "123456".equals(password);
    }
    
    /**
     * 创建模拟登录响应
     */
    private LoginResponse createMockLoginResponse(String phone) {
        LoginResponse response = new LoginResponse();
        
        // 设置模拟Token
        response.setToken("mock_token_" + System.currentTimeMillis());
        
        // 创建模拟用户信息
        User user = new User();
        user.setUserId(1);
        user.setPhone(phone);
        user.setNickname("米嘎嘎用户");
        user.setAvatar("https://example.com/avatar.jpg");
        user.setRegisterTime("2025-01-01 12:00:00");
        
        response.setUser(user);
        
        return response;
    }
}

