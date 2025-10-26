package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
    /**
     * 查询用户的购物车列表
     */
    List<CartItem> findByUserId(Integer userId);
    
    /**
     * 查询用户的特定商品
     */
    Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);
    
    /**
     * 删除用户的购物车商品
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.productId = :productId")
    void deleteByUserIdAndProductId(Integer userId, Integer productId);
    
    /**
     * 清空用户购物车
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId")
    void deleteByUserId(Integer userId);
    
    /**
     * 删除用户选中的购物车项
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.selected = true")
    void deleteSelectedByUserId(Integer userId);
    
    /**
     * 获取用户选中的购物车项
     */
    List<CartItem> findByUserIdAndSelectedTrue(Integer userId);
}

