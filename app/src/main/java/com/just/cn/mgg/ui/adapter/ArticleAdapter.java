package com.just.cn.mgg.ui.adapter;

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
import com.just.cn.mgg.data.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    private final List<Article> articles = new ArrayList<>();
    private final Context context;
    private OnArticleClickListener articleClickListener;

    public ArticleAdapter(Context context) {
        this.context = context;
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.articleClickListener = listener;
    }

    public void replaceItems(List<Article> newItems) {
        articles.clear();
        if (newItems != null) {
            articles.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void appendItems(List<Article> moreItems) {
        if (moreItems == null || moreItems.isEmpty()) {
            return;
        }
        int start = articles.size();
        articles.addAll(moreItems);
        notifyItemRangeInserted(start, moreItems.size());
    }

    public Article getItem(int position) {
        return position >= 0 && position < articles.size() ? articles.get(position) : null;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCover;
        private final TextView tvTitle;
        private final TextView tvSummary;
        private final TextView tvMeta;

        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivArticleCover);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvSummary = itemView.findViewById(R.id.tvArticleSummary);
            tvMeta = itemView.findViewById(R.id.tvArticleMeta);
        }

        void bind(Article article) {
            tvTitle.setText(article.getTitle());
            if (TextUtils.isEmpty(article.getSummary())) {
                tvSummary.setVisibility(View.GONE);
            } else {
                tvSummary.setVisibility(View.VISIBLE);
                tvSummary.setText(article.getSummary());
            }

            String author = TextUtils.isEmpty(article.getAuthor()) ? context.getString(R.string.brand_name) : article.getAuthor();
            String date = article.getDisplayDate();
            String metaText = context.getString(R.string.article_meta_format, author, date, article.getViewCount());
            tvMeta.setText(metaText);

            Glide.with(context)
                    .load(article.getCoverImage())
                    .placeholder(R.drawable.article_placeholder_photo)
                    .error(R.drawable.article_placeholder_photo)
                    .centerCrop()
                    .into(ivCover);

            itemView.setOnClickListener(v -> {
                if (articleClickListener != null) {
                    articleClickListener.onArticleClick(article);
                }
            });
        }
    }
}
