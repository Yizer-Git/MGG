package com.just.cn.mgg.ui.review;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价图片九宫格适配器
 */
public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> images = new ArrayList<>();
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(List<String> imageUrls, int position);
    }

    public ReviewImageAdapter(Context context) {
        this.context = context;
    }

    public void setImages(List<String> imageUrls) {
        images.clear();
        if (imageUrls != null) {
            images.addAll(imageUrls);
        }
        notifyDataSetChanged();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String url = images.get(position);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.product_placeholder_photo)
                .error(R.drawable.product_placeholder_photo)
                .into(holder.imageView);

        holder.imageView.post(() -> {
            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            int width = holder.imageView.getWidth();
            if (width > 0 && params.height != width) {
                params.height = width;
                holder.imageView.setLayoutParams(params);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(new ArrayList<>(images), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivReviewImage);
        }
    }
}

