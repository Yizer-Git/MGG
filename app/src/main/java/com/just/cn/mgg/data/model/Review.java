package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品评价数据模型
 */
public class Review {

    @SerializedName("review_id")
    private long reviewId;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("content")
    private String content;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("like_count")
    private int likeCount;

    @SerializedName("comment_count")
    private int commentCount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("user")
    private ReviewUser user;

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        if (images == null) {
            return Collections.emptyList();
        }
        return images;
    }

    public void setImages(List<String> images) {
        if (images == null) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public ReviewUser getUser() {
        return user;
    }

    public void setUser(ReviewUser user) {
        this.user = user;
    }

    public static class ReviewUser {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("nickname")
        private String nickname;

        @SerializedName("avatar")
        private String avatar;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
