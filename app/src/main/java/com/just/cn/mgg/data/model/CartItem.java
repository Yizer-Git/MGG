package com.just.cn.mgg.data.model;

import java.io.Serializable; // <-- 导入 Serializable

/**
 * 购物车项模型
 */
public class CartItem implements Serializable { // <-- 实现 Serializable
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Boolean selected;
    private Product product; // 关联的产品信息

    public CartItem() {
    }

    // Getters and Setters
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
        // 保证数量不为空且大于0
        this.quantity = (quantity == null || quantity < 1) ? 1 : quantity;
    }

    public Boolean getSelected() {
        // 保证选中状态不为 null，默认为 false
        return selected != null && selected;
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
