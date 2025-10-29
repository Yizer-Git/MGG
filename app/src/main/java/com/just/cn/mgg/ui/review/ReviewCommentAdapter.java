package com.just.cn.mgg.ui.review;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Review;
import com.just.cn.mgg.data.model.ReviewComment;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表适配器
 */
public class ReviewCommentAdapter extends RecyclerView.Adapter<ReviewCommentAdapter.ViewHolder> {

    private final Context context;
    private final List<ReviewComment> data = new ArrayList<>();

    public ReviewCommentAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<ReviewComment> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addItem(ReviewComment comment) {
        if (comment == null) return;
        data.add(comment);
        notifyItemInserted(data.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewComment comment = data.get(position);
        Review.ReviewUser user = comment.getUser();
        String nickname = user != null && !TextUtils.isEmpty(user.getNickname())
                ? user.getNickname()
                : context.getString(R.string.review_user_default);
        holder.tvUser.setText(nickname);
        holder.tvContent.setText(comment.getContent());
        holder.tvCreatedAt.setText(comment.getCreatedAt());

        if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.profile_header_photo)
                    .error(R.drawable.profile_header_photo)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.profile_header_photo);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final com.google.android.material.imageview.ShapeableImageView imgAvatar;
        final TextView tvUser;
        final TextView tvContent;
        final TextView tvCreatedAt;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgCommentAvatar);
            tvUser = itemView.findViewById(R.id.tvCommentUser);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvCreatedAt = itemView.findViewById(R.id.tvCommentCreatedAt);
        }
    }

}
