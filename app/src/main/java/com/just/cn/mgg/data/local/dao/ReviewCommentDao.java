package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.ReviewCommentEntity;

import java.util.List;

@Dao
public interface ReviewCommentDao {

    @Query("SELECT * FROM review_comments WHERE reviewId = :reviewId ORDER BY createdAt ASC")
    List<ReviewCommentEntity> getCommentsForReview(long reviewId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReviewCommentEntity> comments);

    @Query("DELETE FROM review_comments")
    void clearAll();
}
