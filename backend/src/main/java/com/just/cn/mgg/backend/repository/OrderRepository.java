package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    /**
     * 查询用户的所有订单（分页）
     */
    Page<Order> findByUserIdOrderByCreateTimeDesc(Integer userId, Pageable pageable);
    
    /**
     * 查询用户的特定状态订单（分页）
     */
    Page<Order> findByUserIdAndStatusOrderByCreateTimeDesc(Integer userId, Integer status, Pageable pageable);
    
    /**
     * 查询用户的所有订单
     */
    List<Order> findByUserIdOrderByCreateTimeDesc(Integer userId);
    
    /**
     * 统计用户的订单数量
     */
    long countByUserId(Integer userId);
    
    /**
     * 统计用户特定状态的订单数量
     */
    long countByUserIdAndStatus(Integer userId, Integer status);
}

