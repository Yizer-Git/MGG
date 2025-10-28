package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.just.cn.mgg.data.local.entity.OrderEntity;

import java.util.List;

@Dao
public interface OrderDao {

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createTime DESC")
    List<OrderEntity> getOrdersForUser(int userId);

    @Query("SELECT * FROM orders WHERE userId = :userId AND (:status IS NULL OR status = :status) ORDER BY createTime DESC")
    List<OrderEntity> getOrdersForUser(int userId, Integer status);

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    OrderEntity findById(String orderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OrderEntity> orders);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderEntity order);

    @Update
    void update(OrderEntity order);

    @Query("DELETE FROM orders")
    void clearAll();
}
