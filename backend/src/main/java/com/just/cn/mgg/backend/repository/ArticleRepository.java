package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByIsPublishedOrderByCreatedAtDesc(Boolean isPublished, Pageable pageable);

    Page<Article> findByCategoryAndIsPublishedOrderByCreatedAtDesc(String category, Boolean isPublished, Pageable pageable);
}
