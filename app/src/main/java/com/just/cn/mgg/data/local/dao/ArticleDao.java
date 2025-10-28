package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.ArticleEntity;

import java.util.List;

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY createdAt DESC")
    List<ArticleEntity> getAll();

    @Query("SELECT * FROM articles WHERE (:category IS NULL OR category = :category) ORDER BY createdAt DESC")
    List<ArticleEntity> getByCategory(String category);

    @Query("SELECT * FROM articles WHERE articleId = :articleId LIMIT 1")
    ArticleEntity findById(long articleId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ArticleEntity> articles);

    @Query("DELETE FROM articles")
    void clearAll();
}
