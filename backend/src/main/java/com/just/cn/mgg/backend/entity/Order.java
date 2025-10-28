package com.just.cn.mgg.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @Column(name = "order_id", length = 32)
    private String orderId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "address_id")
    private Integer addressId;
    
    @Column(name = "receiver_name", length = 50)
    private String receiverName;
    
    @Column(name = "receiver_phone", length = 11)
    private String receiverPhone;
    
    @Column(name = "receiver_address", length = 500)
    private String receiverAddress;
    
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status = 1; // 1-待付款，2-待发货，3-待收货，4-已完成，5-已取消
    
    private String remark;
    
    @Column(name = "pay_time")
    private LocalDateTime payTime;
    
    @Column(name = "ship_time")
    private LocalDateTime shipTime;
    
    @Column(name = "finish_time")
    private LocalDateTime finishTime;
    
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    // 订单详情列表（不存储在数据库）
    @Transient
    private List<OrderItem> orderItems;
}

