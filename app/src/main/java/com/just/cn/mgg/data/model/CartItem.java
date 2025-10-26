package com.just.cn.mgg.data.model;

/**
 * 购物车项模型
 */
public class CartItem {
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Boolean selected;
    private Product product; // 关联的产品信息
    
    public CartItem() {
    }
    
    public Integer getCartId() {
        return cartId;
    }
    
    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Boolean getSelected() {
        return selected;
    }
    
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}

