package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.ProductEntity;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM products ORDER BY sales DESC")
    List<ProductEntity> getAll();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY sales DESC")
    List<ProductEntity> getByCategory(int categoryId);

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    ProductEntity getProduct(int productId);

    @Query("SELECT * FROM products WHERE productName LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    List<ProductEntity> search(String keyword);

    @Query("SELECT * FROM products ORDER BY sales DESC LIMIT :limit")
    List<ProductEntity> getHotProducts(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductEntity> products);

    @Query("DELETE FROM products")
    void clearAll();
}
