package com.just.cn.mgg.ui.review;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 评价列表适配器（社区、商品详情复用）
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static final Gson GSON = new Gson();

    private final Context context;
    private final List<Review> data = new ArrayList<>();

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void replaceItems(List<Review> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<Review> list) {
        if (list == null || list.isEmpty()) return;
        int start = data.size();
        data.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = data.get(position);

        holder.ratingBar.setRating(review.getRating());
        holder.tvContent.setText(review.getContent());
        holder.tvLikeCount.setText(context.getString(R.string.review_like_format, review.getLikeCount()));
        holder.tvCommentCount.setText(context.getString(R.string.review_comment_format, review.getCommentCount()));
        holder.tvCreatedAt.setText(review.getCreatedAt());

        Review.ReviewUser user = review.getUser();
        if (user != null) {
            holder.tvNickname.setText(!TextUtils.isEmpty(user.getNickname()) ? user.getNickname() : context.getString(R.string.review_user_default));
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.profile_header_photo)
                    .error(R.drawable.profile_header_photo)
                    .circleCrop()
                    .into(holder.imgAvatar);
        } else {
            holder.tvNickname.setText(context.getString(R.string.review_user_default));
            holder.imgAvatar.setImageResource(R.drawable.profile_header_photo);
        }

        List<String> images = review.getImages();
        if (images.isEmpty()) {
            holder.recyclerImages.setVisibility(View.GONE);
        } else {
            holder.recyclerImages.setVisibility(View.VISIBLE);
            holder.imageAdapter.setImages(images);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView tvNickname;
        final TextView tvCreatedAt;
        final RatingBar ratingBar;
        final TextView tvContent;
        final RecyclerView recyclerImages;
        final TextView tvLikeCount;
        final TextView tvCommentCount;
        final ReviewImageAdapter imageAdapter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvNickname = itemView.findViewById(R.id.tvNickname);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvContent = itemView.findViewById(R.id.tvContent);
            recyclerImages = itemView.findViewById(R.id.recyclerViewImages);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);

            imageAdapter = new ReviewImageAdapter(itemView.getContext());
            recyclerImages.setLayoutManager(new GridLayoutManager(itemView.getContext(), 3));
            recyclerImages.setAdapter(imageAdapter);
            recyclerImages.setNestedScrollingEnabled(false);
        }
    }

    public static List<Review> parseReviewList(List<Map<String, Object>> rawList) {
        List<Review> result = new ArrayList<>();
        if (rawList == null) return result;
        for (Map<String, Object> map : rawList) {
            try {
                Review review = GSON.fromJson(GSON.toJson(map), Review.class);
                if (review != null) {
                    result.add(review);
                }
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
