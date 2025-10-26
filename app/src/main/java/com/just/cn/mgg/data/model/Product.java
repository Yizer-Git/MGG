package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 产品数据模型
 */
public class Product {
    @SerializedName("product_id")
    private int productId;
    
    @SerializedName("product_name")
    private String productName;
    
    @SerializedName("category_id")
    private int categoryId; // 1:经典复刻 2:潮流创味
    
    private double price;
    
    @SerializedName("original_price")
    private double originalPrice;
    
    private int stock;
    private int sales;
    private String description;
    private List<String> images;
    
    @SerializedName("is_on_sale")
    private boolean isOnSale;
    
    @SerializedName("create_time")
    private String createTime;
    
    // 购物车相关字段
    private int quantity; // 购买数量
    private boolean selected; // 是否选中
    
    public Product() {}
    
    // Getters and Setters
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
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public int getSales() {
        return sales;
    }
    
    public void setSales(int sales) {
        this.sales = sales;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public boolean isOnSale() {
        return isOnSale;
    }
    
    public void setOnSale(boolean onSale) {
        isOnSale = onSale;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    // 业务方法
    public String getCategoryName() {
        return categoryId == 1 ? "经典复刻" : "潮流创味";
    }
    
    public boolean hasDiscount() {
        return originalPrice > price;
    }
    
    public String getMainImage() {
        return (images != null && !images.isEmpty()) ? images.get(0) : "";
    }
    
    public double getTotalPrice() {
        return price * quantity;
    }
}

