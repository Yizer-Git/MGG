package com.just.cn.mgg.backend.service;

import com.just.cn.mgg.backend.entity.Order;
import com.just.cn.mgg.backend.entity.OrderItem;
import com.just.cn.mgg.backend.entity.Review;
import com.just.cn.mgg.backend.entity.ReviewComment;
import com.just.cn.mgg.backend.entity.ReviewLike;
import com.just.cn.mgg.backend.repository.OrderItemRepository;
import com.just.cn.mgg.backend.repository.OrderRepository;
import com.just.cn.mgg.backend.repository.ReviewCommentRepository;
import com.just.cn.mgg.backend.repository.ReviewLikeRepository;
import com.just.cn.mgg.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * 创建评价
     */
    @Transactional
    public Review createReview(Integer userId,
                               String orderId,
                               Integer productId,
                               Integer rating,
                               String content,
                               String images) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权评价该订单");
        }

        if (order.getStatus() == null || order.getStatus() != 4) {
            throw new RuntimeException("订单未完成，暂不能评价");
        }

        // 校验订单中包含该商品
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        boolean containsProduct = orderItems.stream()
                .anyMatch(item -> item.getProductId().equals(productId));
        if (!containsProduct) {
            throw new RuntimeException("订单中不包含该商品，无法评价");
        }

        if (reviewRepository.existsByOrderIdAndProductId(orderId, productId)) {
            throw new RuntimeException("该商品已评价，无需重复提交");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        review.setLikeCount(0);
        review.setCommentCount(0);
        return reviewRepository.save(review);
    }

    /**
     * 获取商品评价
     */
    public Page<Review> getProductReviews(Integer productId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    /**
     * 获取社区优质晒单
     */
    public Page<Review> getCommunityReviews(int page, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);
        return reviewRepository.findAllByOrderByLikeCountDesc(pageable);
    }

    /**
     * 点赞评价
     */
    @Transactional
    public void likeReview(Long reviewId, Integer userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RuntimeException("您已点赞过该评价");
        }

        ReviewLike like = new ReviewLike();
        like.setReviewId(reviewId);
        like.setUserId(userId);
        reviewLikeRepository.save(like);

        long count = reviewLikeRepository.countByReviewId(reviewId);
        review.setLikeCount((int) count);
        reviewRepository.save(review);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikeReview(Long reviewId, Integer userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        if (!reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RuntimeException("尚未点赞该评价");
        }

        reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);
        long count = reviewLikeRepository.countByReviewId(reviewId);
        review.setLikeCount((int) count);
        reviewRepository.save(review);
    }

    /**
     * 添加评论
     */
    @Transactional
    public ReviewComment addComment(Long reviewId, Integer userId, String content) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        ReviewComment comment = new ReviewComment();
        comment.setReviewId(reviewId);
        comment.setUserId(userId);
        comment.setContent(content);
        ReviewComment saved = reviewCommentRepository.save(comment);

        review.setCommentCount(review.getCommentCount() + 1);
        reviewRepository.save(review);
        return saved;
    }

    /**
     * 获取评价评论
     */
    public List<ReviewComment> getComments(Long reviewId) {
        return reviewCommentRepository.findByReviewIdOrderByCreatedAtAsc(reviewId);
    }
}
