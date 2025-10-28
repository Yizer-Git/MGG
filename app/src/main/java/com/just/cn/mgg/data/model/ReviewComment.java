package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 评价评论数据模型
 */
public class ReviewComment {

    @SerializedName("comment_id")
    private long commentId;

    @SerializedName("review_id")
    private long reviewId;

    @SerializedName("user_id")
    private int userId;

    private String content;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user")
    private Review.ReviewUser user;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Review.ReviewUser getUser() {
        return user;
    }

    public void setUser(Review.ReviewUser user) {
        this.user = user;
    }
}
