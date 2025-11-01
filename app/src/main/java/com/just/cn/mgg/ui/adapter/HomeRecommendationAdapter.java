package com.just.cn.mgg.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.HomeRecommendation;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页推荐展览适配器。
 */
public class HomeRecommendationAdapter extends RecyclerView.Adapter<HomeRecommendationAdapter.ViewHolder> {

    private final Context context;
    private final List<HomeRecommendation> items = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnBookmarkClickListener onBookmarkClickListener;

    public interface OnItemClickListener {
        void onItemClick(HomeRecommendation item);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(HomeRecommendation item);
    }

    public HomeRecommendationAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<HomeRecommendation> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
        this.onBookmarkClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeRecommendation item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvTag.setText(item.getTag());
        holder.tvMeta.setText(item.getMeta());

        if (item.getCoverRes() != 0) {
            holder.ivCover.setImageResource(item.getCoverRes());
        } else if (!TextUtils.isEmpty(item.getCover())) {
            Glide.with(context)
                .load(item.getCover())
                .placeholder(R.drawable.article_placeholder_photo)
                .error(R.drawable.article_placeholder_photo)
                .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.article_placeholder_photo);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
        holder.btnBookmark.setOnClickListener(v -> {
            if (onBookmarkClickListener != null) {
                onBookmarkClickListener.onBookmarkClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTag;
        TextView tvTitle;
        TextView tvMeta;
        ImageButton btnBookmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivRecommendationCover);
            tvTag = itemView.findViewById(R.id.tvRecommendationTag);
            tvTitle = itemView.findViewById(R.id.tvRecommendationTitle);
            tvMeta = itemView.findViewById(R.id.tvRecommendationMeta);
            btnBookmark = itemView.findViewById(R.id.btnRecommendationBookmark);
        }
    }
}
