package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 收件地址数据模型
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("address_id")
    private int addressId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("receiver_name")
    private String receiverName;

    @SerializedName("receiver_phone")
    private String receiverPhone;

    private String province;
    private String city;
    private String district;

    @SerializedName("region")
    private String region;

    @SerializedName("detail_address")
    private String detailAddress;

    @SerializedName("is_default")
    private boolean isDefault;

    public Address() {}

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        if (region != null && !region.isEmpty()) {
            return region;
        }
        StringBuilder builder = new StringBuilder();
        if (province != null) builder.append(province);
        if (city != null) builder.append(city);
        if (district != null) builder.append(district);
        return builder.toString();
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getFullAddress() {
        StringBuilder builder = new StringBuilder();
        String baseRegion = getRegion();
        if (baseRegion != null && !baseRegion.isEmpty()) {
            builder.append(baseRegion);
        }
        if (detailAddress != null) {
            builder.append(detailAddress);
        }
        return builder.toString();
    }
}
