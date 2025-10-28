package com.just.cn.mgg.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(unique = true, nullable = false, length = 11)
    private String phone;
    
    @Column(nullable = false)
    private String password;
    
    private String nickname;
    private String avatar;
    private String email;
    @Column(name = "gender", columnDefinition = "TINYINT")
    private Integer gender;

    @Column(name = "status", columnDefinition = "TINYINT")
    private Integer status;
    
    @CreatedDate
    @Column(name = "register_time")
    private LocalDateTime registerTime;
    
    @LastModifiedDate
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Column(name = "login_attempts")
    private Integer loginAttempts;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
}
