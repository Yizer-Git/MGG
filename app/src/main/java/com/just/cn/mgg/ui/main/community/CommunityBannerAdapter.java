package com.just.cn.mgg.ui.main.community;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.HomeRecommendation;

import java.util.ArrayList;
import java.util.List;

class CommunityBannerAdapter extends RecyclerView.Adapter<CommunityBannerAdapter.BannerViewHolder> {

    private final Context context;
    private final List<HomeRecommendation> bannerItems = new ArrayList<>();
    private OnBannerClickListener onBannerClickListener;

    CommunityBannerAdapter(Context context) {
        this.context = context;
    }

    void setOnBannerClickListener(OnBannerClickListener listener) {
        this.onBannerClickListener = listener;
    }

    void setItems(List<HomeRecommendation> banners) {
        bannerItems.clear();
        if (banners != null) {
            bannerItems.addAll(banners);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explore_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        HomeRecommendation banner = bannerItems.get(position);

        if (banner.getCoverRes() != 0) {
            holder.ivBanner.setImageResource(banner.getCoverRes());
        } else if (!TextUtils.isEmpty(banner.getCover())) {
            Glide.with(context)
                    .load(banner.getCover())
                    .placeholder(R.drawable.community_banner_photo)
                    .error(R.drawable.community_banner_photo)
                    .centerCrop()
                    .into(holder.ivBanner);
        } else {
            holder.ivBanner.setImageResource(R.drawable.community_banner_photo);
        }

        if (TextUtils.isEmpty(banner.getTag())) {
            holder.tvTag.setVisibility(View.GONE);
        } else {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(banner.getTag());
        }

        holder.tvTitle.setText(banner.getTitle());
        holder.tvSubtitle.setText(banner.getMeta());

        holder.itemView.setOnClickListener(v -> {
            if (onBannerClickListener != null) {
                onBannerClickListener.onBannerClick(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerItems.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivBanner;
        final TextView tvTag;
        final TextView tvTitle;
        final TextView tvSubtitle;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBannerImage);
            tvTag = itemView.findViewById(R.id.tvBannerTag);
            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvSubtitle = itemView.findViewById(R.id.tvBannerSubtitle);
        }
    }

    interface OnBannerClickListener {
        void onBannerClick(HomeRecommendation banner);
    }
}
