package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    List<ReviewComment> findByReviewIdOrderByCreatedAtAsc(Long reviewId);

    Page<ReviewComment> findByReviewIdOrderByCreatedAtAsc(Long reviewId, Pageable pageable);
}
