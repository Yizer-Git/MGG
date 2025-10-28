package com.just.cn.mgg.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;
    
    @Column(name = "receiver_phone", nullable = false, length = 11)
    private String receiverPhone;
    
    @Column(length = 50)
    private String province;
    
    @Column(length = 50)
    private String city;
    
    @Column(length = 50)
    private String district;
    
    @Column(name = "detail_address", nullable = false)
    private String detailAddress;
    
    @Column(name = "is_default", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isDefault = false;
    
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    // 获取完整地址
    @Transient
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (province != null) sb.append(province);
        if (city != null) sb.append(city);
        if (district != null) sb.append(district);
        if (detailAddress != null) sb.append(detailAddress);
        return sb.toString();
    }
}

