package com.just.cn.mgg;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.just.cn.mgg.ui.auth.LoginActivity;
import com.just.cn.mgg.ui.main.category.CategoryFragment;
import com.just.cn.mgg.ui.main.community.CommunityFragment;
import com.just.cn.mgg.ui.main.home.HomeFragment;
import com.just.cn.mgg.ui.main.profile.ProfileFragment;
import com.just.cn.mgg.utils.SPUtils;

/**
 * 主页面 - 带底部导航
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Fragment currentFragment;

    private HomeFragment homeFragment;
    private CategoryFragment topicFragment;
    private CommunityFragment communityFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SPUtils.isLogin(this)) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_main_new);

        initViews();
        initFragments();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        topicFragment = new CategoryFragment();
        communityFragment = new CommunityFragment();
        profileFragment = new ProfileFragment();
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_explore) {
                switchFragment(communityFragment);
                return true;
            } else if (itemId == R.id.nav_topic) {
                switchFragment(topicFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                return true;
            }

            return false;
        });
    }

    public void openCultureSection() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_topic);
        }
    }

    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == currentFragment) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        if (targetFragment.isAdded()) {
            transaction.show(targetFragment);
        } else {
            transaction.add(R.id.fragmentContainer, targetFragment);
        }

        transaction.commit();
        currentFragment = targetFragment;
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
