package com.just.cn.mgg.backend.controller;

import com.just.cn.mgg.backend.dto.ApiResponse;
import com.just.cn.mgg.backend.entity.Product;
import com.just.cn.mgg.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts(
            @RequestParam(required = false) Integer category_id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int page_size,
            @RequestParam(defaultValue = "sales") String sort_by) {
        try {
            List<Product> products;
            if (category_id != null) {
                products = productRepository.findByCategoryIdAndIsOnSale(category_id, true);
            } else {
                products = productRepository.findByIsOnSaleTrue();
            }
            
            // 简单排序（按销量）
            if ("sales".equals(sort_by)) {
                products.sort((p1, p2) -> p2.getSales().compareTo(p1.getSales()));
            }
            
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Integer id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("产品不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<Product>>> getHotProducts(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Product> products = productRepository.findHotProducts(pageable);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int page_size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, page_size, Sort.by("sales").descending());
            Page<Product> productPage = productRepository.findByKeyword(keyword, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", productPage.getContent());
            result.put("total", productPage.getTotalElements());
            result.put("page", page);
            result.put("page_size", page_size);
            result.put("total_pages", productPage.getTotalPages());
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
