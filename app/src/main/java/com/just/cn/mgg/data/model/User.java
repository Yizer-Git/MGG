package com.just.cn.mgg.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用户数据模型
 */
public class User {
    @SerializedName("user_id")
    private int userId;
    
    private String username;
    private String phone;
    private String nickname;
    private String avatar;
    
    @SerializedName("register_time")
    private String registerTime;
    
    private String token;
    
    public User() {}
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getRegisterTime() {
        return registerTime;
    }
    
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}

