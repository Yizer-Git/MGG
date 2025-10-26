package com.just.cn.mgg.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Boolean selected = true;
    
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 关联查询时使用
    @Transient
    private Product product;
}

