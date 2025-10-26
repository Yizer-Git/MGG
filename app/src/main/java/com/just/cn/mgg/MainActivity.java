package com.just.cn.mgg;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.just.cn.mgg.ui.auth.LoginActivity;
import com.just.cn.mgg.ui.main.cart.CartFragment;
import com.just.cn.mgg.ui.main.category.CategoryFragment;
import com.just.cn.mgg.ui.main.home.HomeFragment;
import com.just.cn.mgg.ui.main.profile.ProfileFragment;
import com.just.cn.mgg.utils.SPUtils;

/**
 * 主界面 - 包含底部导航
 */
public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigation;
    private Fragment currentFragment;
    
    // Fragments
    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private CartFragment cartFragment;
    private ProfileFragment profileFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 检查登录状态
        if (!SPUtils.isLogin(this)) {
            goToLogin();
            return;
        }
        
        setContentView(R.layout.activity_main_new);
        
        initViews();
        initFragments();
        setupBottomNavigation();
        
        // 默认显示首页
        if (savedInstanceState == null) {
            switchFragment(homeFragment);
        }
    }
    
    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }
    
    private void initFragments() {
        homeFragment = new HomeFragment();
        categoryFragment = new CategoryFragment();
        cartFragment = new CartFragment();
        profileFragment = new ProfileFragment();
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_category) {
                switchFragment(categoryFragment);
                return true;
            } else if (itemId == R.id.nav_cart) {
                switchFragment(cartFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                return true;
            }
            
            return false;
        });
    }
    
    /**
     * 切换Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == currentFragment) {
            return;
        }
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        
        // 隐藏当前Fragment
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        
        // 显示目标Fragment
        if (targetFragment.isAdded()) {
            transaction.show(targetFragment);
        } else {
            transaction.add(R.id.fragmentContainer, targetFragment);
        }
        
        transaction.commit();
        currentFragment = targetFragment;
    }
    
    /**
     * 跳转到登录界面
     */
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
