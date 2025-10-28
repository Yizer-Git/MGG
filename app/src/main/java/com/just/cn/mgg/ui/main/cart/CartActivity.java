package com.just.cn.mgg.ui.main.cart;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.just.cn.mgg.R;

/**
 * 独立的购物车页面，便于从任意入口打开。
 */
public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.cartFragmentContainer, new CartFragment())
                    .commit();
        }
    }
}
