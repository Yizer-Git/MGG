package com.just.cn.mgg.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.just.cn.mgg.R;
import com.just.cn.mgg.ui.auth.LoginActivity;
import com.just.cn.mgg.utils.Constants;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

/**
 * 我的Fragment
 */
public class ProfileFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }
    
    private void initViews(View view) {
        // 退出登录
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }
    
    /**
     * 退出登录
     */
    private void logout() {
        SPUtils.clear(requireContext());
        ToastUtils.showShort(requireContext(), "已退出登录");
        
        // 跳转到登录界面
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}

