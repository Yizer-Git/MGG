package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 订单数据模型
 */
public class Order {
    @SerializedName("order_id")
    private String orderId;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("total_amount")
    private double totalAmount;
    
    @SerializedName("address_id")
    private int addressId;
    
    private int status; // 1待付款 2待发货 3待收货 4已完成 5已取消
    
    @SerializedName("pay_time")
    private String payTime;
    
    @SerializedName("ship_time")
    private String shipTime;
    
    @SerializedName("create_time")
    private String createTime;
    
    @SerializedName("order_items")
    private List<OrderItem> orderItems;
    
    private Address address;
    
    public Order() {}
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int getAddressId() {
        return addressId;
    }
    
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getPayTime() {
        return payTime;
    }
    
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
    
    public String getShipTime() {
        return shipTime;
    }
    
    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    // 业务方法
    public String getStatusText() {
        switch (status) {
            case 1: return "待付款";
            case 2: return "待发货";
            case 3: return "待收货";
            case 4: return "已完成";
            case 5: return "已取消";
            default: return "未知状态";
        }
    }
    
    public boolean canCancel() {
        return status == 1; // 只有待付款可取消
    }
    
    public boolean canPay() {
        return status == 1;
    }
    
    public boolean canConfirm() {
        return status == 3; // 待收货可确认收货
    }
}

