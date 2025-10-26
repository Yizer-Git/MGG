package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByIsActiveTrueOrderBySortOrder();
}
