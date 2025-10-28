package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // <-- 导入 Serializable
import java.util.List;

/**
 * 产品数据模型
 */
public class Product implements Serializable { // <-- 实现 Serializable
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
    private List<String> images; // 注意：如果 List<String> 需要序列化，确保 String 本身是可序列化的（它是）

    @SerializedName("is_on_sale")
    private boolean isOnSale;

    @SerializedName("create_time")
    private String createTime;

    // 购物车相关字段 (这些字段主要用于购物车展示，传递到订单确认页时可能不需要)
    // 如果这些字段不需要持久化或通过 Intent 传递，可以考虑加上 transient 关键字
    // private transient int quantity;
    // private transient boolean selected;
    // 但为了方便在订单确认页复用 CartItem，暂时保留
    private int quantity;
    private boolean selected;


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
        // 返回购物车中的数量，默认为1
        return quantity < 1 ? 1 : quantity;
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
        // 添加非零检查
        return originalPrice > 0 && originalPrice > price;
    }

    public String getMainImage() {
        return (images != null && !images.isEmpty()) ? images.get(0) : "";
    }

    public double getTotalPrice() {
        // 计算总价时使用正确的数量
        return price * getQuantity();
    }
}
