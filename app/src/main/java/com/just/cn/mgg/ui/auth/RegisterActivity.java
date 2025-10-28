package com.just.cn.mgg.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.local.LocalRepository;
import com.just.cn.mgg.data.model.User;
import com.just.cn.mgg.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册界面
 */
public class RegisterActivity extends AppCompatActivity {
    
    private TextInputEditText etPhone, etCode, etPassword, etConfirmPassword;
    private MaterialButton btnGetCode, btnRegister;
    
    private LocalRepository repository;
    private CountDownTimer countDownTimer;
    private String lastVerifyCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        initListeners();
        
        repository = new LocalRepository(this);
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
        
        btnGetCode.setEnabled(false);
        lastVerifyCode = generateVerifyCode();
        ToastUtils.showShort(this, "验证码已发送：" + lastVerifyCode + "（本地调试）");
        startCountDown();
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
        
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");

        try {
            User user = repository.registerUser(phone, password, null);
            ToastUtils.showShort(this, "注册成功，请登录");
            finish();
        } catch (IllegalArgumentException ex) {
            ToastUtils.showShort(this, ex.getMessage());
            btnRegister.setEnabled(true);
            btnRegister.setText(getString(R.string.register));
        } catch (Exception ex) {
            ToastUtils.showShort(this, "注册失败：" + ex.getMessage());
            btnRegister.setEnabled(true);
            btnRegister.setText(getString(R.string.register));
        }
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

        if (lastVerifyCode == null || !lastVerifyCode.equals(code)) {
            ToastUtils.showShort(this, "验证码不正确，请重新获取");
            return false;
        }
        
        return true;
    }

    private String generateVerifyCode() {
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        return String.valueOf(code);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

