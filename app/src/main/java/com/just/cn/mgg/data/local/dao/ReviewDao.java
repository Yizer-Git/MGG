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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ReviewEntity> reviews);

    @Query("DELETE FROM reviews")
    void clearAll();
}
