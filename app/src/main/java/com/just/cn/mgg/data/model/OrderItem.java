package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 订单详情项数据模型
 */
public class OrderItem {
    @SerializedName("item_id")
    private int itemId;
    
    @SerializedName("order_id")
    private String orderId;
    
    @SerializedName("product_id")
    private int productId;
    
    @SerializedName("product_name")
    private String productName;
    
    private double price;
    private int quantity;
    
    @SerializedName("product_image")
    private String productImage;
    
    public OrderItem() {}
    
    // Getters and Setters
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getProductImage() {
        return productImage;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    
    public double getTotalPrice() {
        return price * quantity;
    }
}

