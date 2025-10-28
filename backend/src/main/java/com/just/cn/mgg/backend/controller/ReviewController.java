package com.just.cn.mgg.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.just.cn.mgg.backend.dto.ApiResponse;
import com.just.cn.mgg.backend.entity.Review;
import com.just.cn.mgg.backend.entity.ReviewComment;
import com.just.cn.mgg.backend.entity.User;
import com.just.cn.mgg.backend.repository.ReviewRepository;
import com.just.cn.mgg.backend.repository.UserRepository;
import com.just.cn.mgg.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/reviews/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createReview(
            @RequestBody ReviewCreateRequest request,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("评分范围应为1-5星"));
            }

            String imagesJson = convertImagesToJson(request.getImages());

            Review review = reviewService.createReview(
                    userId,
                    request.getOrderId(),
                    request.getProductId(),
                    request.getRating(),
                    request.getContent(),
                    imagesJson);

            Map<String, Object> responseData = convertReviewToMap(review);
            return ResponseEntity.ok(ApiResponse.success("评价成功", responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductReviews(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size) {
        try {
            Page<Review> reviewPage = reviewService.getProductReviews(productId, page, page_size);
            List<Map<String, Object>> list = reviewPage.getContent().stream()
                    .map(this::convertReviewToMap)
                    .toList();

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("page", page);
            result.put("page_size", page_size);
            result.put("total", reviewPage.getTotalElements());
            result.put("total_pages", reviewPage.getTotalPages());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reviews/community")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommunityReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int page_size) {
        try {
            Page<Review> reviewPage = reviewService.getCommunityReviews(page, page_size);
            List<Map<String, Object>> list = reviewPage.getContent().stream()
                    .map(this::convertReviewToMap)
                    .toList();

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("page", page);
            result.put("page_size", page_size);
            result.put("total", reviewPage.getTotalElements());
            result.put("total_pages", reviewPage.getTotalPages());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<ApiResponse<String>> likeReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            reviewService.likeReview(reviewId, userId);
            return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reviews/{reviewId}/unlike")
    public ResponseEntity<ApiResponse<String>> unlikeReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            reviewService.unlikeReview(reviewId, userId);
            return ResponseEntity.ok(ApiResponse.success("已取消点赞", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addComment(
            @PathVariable Long reviewId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("评论内容不能为空"));
            }
            ReviewComment comment = reviewService.addComment(reviewId, userId, request.getContent().trim());

            Map<String, Object> map = convertCommentToMap(comment);
            return ResponseEntity.ok(ApiResponse.success("评论成功", map));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getComments(
            @PathVariable Long reviewId) {
        try {
            List<Map<String, Object>> list = reviewService.getComments(reviewId).stream()
                    .map(this::convertCommentToMap)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private Map<String, Object> convertReviewToMap(Review review) {
        Map<String, Object> map = new HashMap<>();
        map.put("review_id", review.getReviewId());
        map.put("order_id", review.getOrderId());
        map.put("product_id", review.getProductId());
        map.put("user_id", review.getUserId());
        map.put("rating", review.getRating());
        map.put("content", review.getContent());
        map.put("images", parseImages(review.getImages()));
        map.put("like_count", review.getLikeCount());
        map.put("comment_count", review.getCommentCount());
        map.put("created_at", review.getCreatedAt());
        map.put("updated_at", review.getUpdatedAt());

        userRepository.findById(review.getUserId()).ifPresent(user -> map.put("user", toUserSummary(user)));

        return map;
    }

    private Map<String, Object> convertCommentToMap(ReviewComment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("comment_id", comment.getCommentId());
        map.put("review_id", comment.getReviewId());
        map.put("user_id", comment.getUserId());
        map.put("content", comment.getContent());
        map.put("created_at", comment.getCreatedAt());

        userRepository.findById(comment.getUserId()).ifPresent(user -> map.put("user", toUserSummary(user)));
        return map;
    }

    private Map<String, Object> toUserSummary(User user) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("user_id", user.getUserId());
        summary.put("nickname", user.getNickname());
        summary.put("avatar", user.getAvatar());
        return summary;
    }

    private List<String> parseImages(String images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(images, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String convertImagesToJson(List<String> images) throws JsonProcessingException {
        if (images == null) {
            return "[]";
        }
        return objectMapper.writeValueAsString(images);
    }

    private Integer getUserIdFromAuth(Authentication authentication) {
        // TODO 结合JWT解析用户ID，目前作为演示返回固定值
        return 6001;
    }

    public static class ReviewCreateRequest {
        private String orderId;
        private Integer productId;
        private Integer rating;
        private String content;
        private List<String> images;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class CommentRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
