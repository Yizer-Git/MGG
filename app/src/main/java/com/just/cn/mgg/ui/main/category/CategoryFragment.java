package com.just.cn.mgg.ui.main.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.just.cn.mgg.R;

/**
 * 分类Fragment
 */
public class CategoryFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText("分类页面 - 开发中");
        textView.setTextSize(18);
        textView.setGravity(android.view.Gravity.CENTER);
        return view;
    }
}

