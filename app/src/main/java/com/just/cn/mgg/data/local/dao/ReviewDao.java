package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.ReviewEntity;

import java.util.List;

@Dao
public interface ReviewDao {

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    List<ReviewEntity> getAll();

    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    List<ReviewEntity> getByProduct(int productId);

    @Query("SELECT * FROM reviews WHERE reviewId = :reviewId LIMIT 1")
    ReviewEntity findById(long reviewId);

    @Query("SELECT * FROM reviews WHERE content LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    List<ReviewEntity> search(String keyword);

    @Query("UPDATE reviews SET likeCount = CASE WHEN likeCount + :delta < 0 THEN 0 ELSE likeCount + :delta END WHERE reviewId = :reviewId")
    void adjustLikeCount(long reviewId, int delta);

    @Query("UPDATE reviews SET commentCount = CASE WHEN commentCount + :delta < 0 THEN 0 ELSE commentCount + :delta END WHERE reviewId = :reviewId")
    void adjustCommentCount(long reviewId, int delta);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReviewEntity> reviews);

    @Query("DELETE FROM reviews")
    void clearAll();
}
