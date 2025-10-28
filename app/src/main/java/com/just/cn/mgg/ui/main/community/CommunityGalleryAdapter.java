package com.just.cn.mgg.ui.main.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.cn.mgg.R;

import java.util.List;

/**
 * 社区页顶部灵感图库，展示来自非遗作坊的不同瞬间。
 */
class CommunityGalleryAdapter extends RecyclerView.Adapter<CommunityGalleryAdapter.GalleryViewHolder> {

    private final List<Integer> galleryImages;

    CommunityGalleryAdapter(List<Integer> galleryImages) {
        this.galleryImages = galleryImages;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_gallery, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.ivGallery.setImageResource(galleryImages.get(position));
    }

    @Override
    public int getItemCount() {
        return galleryImages.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivGallery;

        GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = itemView.findViewById(R.id.ivGallery);
        }
    }
}

