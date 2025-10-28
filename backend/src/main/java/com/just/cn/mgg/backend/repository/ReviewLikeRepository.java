package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByReviewIdAndUserId(Long reviewId, Integer userId);

    void deleteByReviewIdAndUserId(Long reviewId, Integer userId);

    long countByReviewId(Long reviewId);
}
