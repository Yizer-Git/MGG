package com.just.cn.mgg.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 300)
    private String summary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String author;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_published")
    private Boolean isPublished = Boolean.TRUE;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
