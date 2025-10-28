package com.just.cn.mgg.ui.main.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.cn.mgg.R;

import java.util.List;

/**
 * 首页横幅轮播适配器，使用本地精选图增强沉浸感。
 */
class HomeBannerAdapter extends RecyclerView.Adapter<HomeBannerAdapter.BannerViewHolder> {

    private final List<Integer> bannerResources;

    HomeBannerAdapter(List<Integer> bannerResources) {
        this.bannerResources = bannerResources;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.ivBannerImage.setImageResource(bannerResources.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerResources.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivBannerImage;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
        }
    }
}

