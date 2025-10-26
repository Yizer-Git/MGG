package com.just.cn.mgg.backend.controller;

import com.just.cn.mgg.backend.dto.ApiResponse;
import com.just.cn.mgg.backend.entity.CartItem;
import com.just.cn.mgg.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Integer productId = (Integer) request.get("product_id");
            Integer quantity = (Integer) request.get("quantity");
            
            if (quantity == null || quantity <= 0) {
                quantity = 1;
            }
            
            cartService.addToCart(userId, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success("添加成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCartList(Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            List<CartItem> cartItems = cartService.getCartList(userId);
            return ResponseEntity.ok(ApiResponse.success(cartItems));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 更新购物车商品
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItem(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Integer productId = (Integer) request.get("product_id");
            Integer quantity = (Integer) request.get("quantity");
            Boolean selected = (Boolean) request.get("selected");
            
            CartItem updatedItem = cartService.updateCartItem(userId, productId, quantity, selected);
            return ResponseEntity.ok(ApiResponse.success(updatedItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 删除购物车商品
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteCartItem(
            @RequestParam Integer product_id,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            cartService.deleteCartItem(userId, product_id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 清空购物车
     */
    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            cartService.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success("清空成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 从认证信息获取用户ID
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        // TODO: 从JWT Token中提取用户ID
        // 暂时返回测试用户ID
        return 1;
    }
}

