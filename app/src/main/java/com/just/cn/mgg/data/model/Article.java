package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 文章数据模型（品牌故事 / 非遗技艺 / 国潮资讯）
 */
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("article_id")
    private long articleId;

    private String title;
    private String summary;
    private String content;

    @SerializedName("cover_image")
    private String coverImage;

    private String category;
    private String author;

    @SerializedName("view_count")
    private int viewCount;

    @SerializedName("is_published")
    private boolean isPublished;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public Article() {}

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 提供一个友好的日期展示（默认返回创建日期，若为空则返回更新日期）
     */
    public String getDisplayDate() {
        if (createdAt != null && !createdAt.isEmpty()) {
            return createdAt.split(" ")[0];
        }
        if (updatedAt != null && !updatedAt.isEmpty()) {
            return updatedAt.split(" ")[0];
        }
        return "";
    }
}
