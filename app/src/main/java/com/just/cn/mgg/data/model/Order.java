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

    @SerializedName("has_reviewed")
    private boolean hasReviewed;

    @SerializedName("status_description")
    private String statusDescription;

    @SerializedName("pay_time")
    private String payTime;

    @SerializedName("ship_time")
    private String shipTime;

    @SerializedName("create_time")
    private String createTime;

    @SerializedName("remark")
    private String remark;

    @SerializedName("shipping_fee")
    private double shippingFee;

    @SerializedName("order_items")
    private List<OrderItem> orderItems;

    private Address address;

    public Order() {}

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

    public boolean hasReviewed() {
        return hasReviewed;
    }

    public void setHasReviewed(boolean hasReviewed) {
        this.hasReviewed = hasReviewed;
    }

    public String getStatusDescription() {
        return (statusDescription == null || statusDescription.isEmpty())
                ? getStatusText()
                : statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
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

    public String getStatusText() {
        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "待发货";
            case 2:
                return "待收货";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    public boolean canCancel() {
        return status == 0;
    }

    public boolean canPay() {
        return status == 0;
    }

    public boolean canConfirm() {
        return status == 2;
    }

    public boolean canReview() {
        return status == 3 && !hasReviewed;
    }
}
