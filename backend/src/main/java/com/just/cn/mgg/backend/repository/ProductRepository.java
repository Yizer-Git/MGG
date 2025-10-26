package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryIdAndIsOnSale(Integer categoryId, Boolean isOnSale);
    List<Product> findByIsOnSaleTrue();
    
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true ORDER BY p.sales DESC")
    List<Product> findHotProducts(Pageable pageable);
}
