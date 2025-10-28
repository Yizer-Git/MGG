package com.just.cn.mgg.backend.controller;

import com.just.cn.mgg.backend.dto.ApiResponse;
import com.just.cn.mgg.backend.entity.Article;
import com.just.cn.mgg.backend.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getArticles(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size) {
        try {
            Pageable pageable = PageRequest.of(Math.max(page - 1, 0), page_size);
            Page<Article> articlePage;
            if (category != null && !category.isEmpty()) {
                articlePage = articleRepository.findByCategoryAndIsPublishedOrderByCreatedAtDesc(category, true, pageable);
            } else {
                articlePage = articleRepository.findByIsPublishedOrderByCreatedAtDesc(true, pageable);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("list", articlePage.getContent());
            result.put("total", articlePage.getTotalElements());
            result.put("page", page);
            result.put("page_size", page_size);
            result.put("total_pages", articlePage.getTotalPages());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Article>> getArticleDetail(@PathVariable Long id) {
        try {
            Article article = articleRepository.findById(id).orElse(null);
            if (article == null || Boolean.FALSE.equals(article.getIsPublished())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("�ĵ�������"));
            }

            Integer viewCount = article.getViewCount() == null ? 0 : article.getViewCount();
            article.setViewCount(viewCount + 1);
            articleRepository.save(article);

            return ResponseEntity.ok(ApiResponse.success(article));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
