package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    /**
     * 查询订单的所有商品
     */
    List<OrderItem> findByOrderId(String orderId);
    
    /**
     * 删除订单的所有商品
     */
    void deleteByOrderId(String orderId);
}

