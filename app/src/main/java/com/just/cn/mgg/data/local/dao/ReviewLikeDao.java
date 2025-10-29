package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.ReviewLikeEntity;

import java.util.List;

@Dao
public interface ReviewLikeDao {

    @Query("SELECT * FROM review_likes WHERE reviewId = :reviewId")
    List<ReviewLikeEntity> getLikes(long reviewId);

    @Query("SELECT * FROM review_likes WHERE reviewId = :reviewId AND userId = :userId LIMIT 1")
    ReviewLikeEntity findLike(long reviewId, int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReviewLikeEntity like);

    @Query("DELETE FROM review_likes WHERE reviewId = :reviewId AND userId = :userId")
    void delete(long reviewId, int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReviewLikeEntity> likes);

    @Query("DELETE FROM review_likes")
    void clearAll();
}
