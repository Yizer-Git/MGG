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
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.remote.response.LoginResponse;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

/**
 * 本地数据库登录页
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private View tvRegister;
    private View tvForgetPassword;
    private View tvCodeLogin;

    private LocalRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new LocalRepository(this);

        initViews();
        initListeners();
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
        btnLogin.setOnClickListener(v -> login());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgetPassword.setOnClickListener(v ->
                ToastUtils.showShort(this, "功能开发中..."));

        tvCodeLogin.setOnClickListener(v ->
                ToastUtils.showShort(this, "功能开发中..."));
    }

    private void checkLoginStatus() {
        if (SPUtils.isLogin(this)) {
            goToMain();
        }
    }

    private void login() {
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (!validateInput(phone, password)) {
            return;
        }

        setLoginButtonState(false, "登录中...");
        try {
            LoginResponse response = repository.login(phone, password);
            setLoginButtonState(true, getString(R.string.login));
            handleLoginSuccess(response);
        } catch (IllegalArgumentException ex) {
            setLoginButtonState(true, getString(R.string.login));
            ToastUtils.showShort(this, ex.getMessage());
        } catch (Exception ex) {
            setLoginButtonState(true, getString(R.string.login));
            ToastUtils.showShort(this, "登录失败：" + ex.getMessage());
        }
    }

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

    private void handleLoginSuccess(LoginResponse response) {
        SPUtils.put(this, Constants.KEY_TOKEN, response.getToken());
        SPUtils.put(this, Constants.KEY_IS_LOGIN, true);
        SPUtils.put(this, Constants.KEY_USER_ID, response.getUser().getUserId());
        SPUtils.put(this, Constants.KEY_PHONE, response.getUser().getPhone());
        if (response.getUser().getNickname() != null) {
            SPUtils.put(this, Constants.KEY_NICKNAME, response.getUser().getNickname());
        }
        ToastUtils.showShort(this, "登录成功");
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoginButtonState(boolean enabled, String text) {
        btnLogin.setEnabled(enabled);
        btnLogin.setText(text);
    }
}
