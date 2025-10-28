package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.OrderItemEntity;

import java.util.List;

@Dao
public interface OrderItemDao {

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItemEntity> getItemsForOrder(String orderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OrderItemEntity> items);

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    void deleteForOrder(String orderId);

    @Query("DELETE FROM order_items")
    void clearAll();
}
