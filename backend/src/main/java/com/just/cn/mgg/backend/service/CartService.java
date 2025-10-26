package com.just.cn.mgg.backend.service;

import com.just.cn.mgg.backend.entity.CartItem;
import com.just.cn.mgg.backend.entity.Product;
import com.just.cn.mgg.backend.repository.CartItemRepository;
import com.just.cn.mgg.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 添加商品到购物车
     */
    @Transactional
    public CartItem addToCart(Integer userId, Integer productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("产品不存在"));
        
        // 检查库存
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        // 检查是否已在购物车
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem.isPresent()) {
            // 更新数量
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("库存不足");
            }
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // 新增购物车项
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setSelected(true);
            return cartItemRepository.save(newItem);
        }
    }
    
    /**
     * 获取购物车列表
     */
    public List<CartItem> getCartList(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        // 填充产品信息
        for (CartItem item : cartItems) {
            productRepository.findById(item.getProductId()).ifPresent(item::setProduct);
        }
        
        return cartItems;
    }
    
    /**
     * 更新购物车商品
     */
    @Transactional
    public CartItem updateCartItem(Integer userId, Integer productId, Integer quantity, Boolean selected) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        // 检查库存
        if (quantity != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("产品不存在"));
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            item.setQuantity(quantity);
        }
        
        if (selected != null) {
            item.setSelected(selected);
        }
        
        return cartItemRepository.save(item);
    }
    
    /**
     * 删除购物车商品
     */
    @Transactional
    public void deleteCartItem(Integer userId, Integer productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    /**
     * 清空购物车
     */
    @Transactional
    public void clearCart(Integer userId) {
        cartItemRepository.deleteByUserId(userId);
    }
    
    /**
     * 清空选中的商品
     */
    @Transactional
    public void clearSelectedItems(Integer userId) {
        cartItemRepository.deleteSelectedByUserId(userId);
    }
    
    /**
     * 获取选中的购物车项
     */
    public List<CartItem> getSelectedItems(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndSelectedTrue(userId);
        
        // 填充产品信息
        for (CartItem item : cartItems) {
            productRepository.findById(item.getProductId()).ifPresent(item::setProduct);
        }
        
        return cartItems;
    }
}

