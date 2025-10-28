package com.just.cn.mgg.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;
    
    @Column(name = "category_desc", length = 200)
    private String categoryDesc;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private Boolean isActive;
    
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
