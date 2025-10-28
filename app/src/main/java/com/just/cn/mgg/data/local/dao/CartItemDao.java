package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.just.cn.mgg.data.local.entity.CartItemEntity;

import java.util.List;

@Dao
public interface CartItemDao {

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    List<CartItemEntity> getCartItems(int userId);

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    CartItemEntity findCartItem(int userId, int productId);

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId LIMIT 1")
    CartItemEntity findById(int cartId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CartItemEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartItemEntity> items);

    @Update
    int update(CartItemEntity item);

    @Delete
    int delete(CartItemEntity item);

    @Query("DELETE FROM cart_items WHERE userId = :userId AND cartId IN (:cartIds)")
    void deleteByIds(int userId, List<Integer> cartIds);

    @Query("DELETE FROM cart_items")
    void clearAll();
}
