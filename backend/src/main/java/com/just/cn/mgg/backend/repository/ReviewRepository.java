package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdOrderByCreatedAtDesc(Integer productId, Pageable pageable);

    boolean existsByOrderIdAndProductId(String orderId, Integer productId);

    Optional<Review> findByOrderIdAndProductId(String orderId, Integer productId);

    Page<Review> findAllByOrderByLikeCountDesc(Pageable pageable);
}
