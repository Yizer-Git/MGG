package com.just.cn.mgg.data.remote.response;

import com.just.cn.mgg.data.model.User;

/**
 * 登录响应数据
 */
public class LoginResponse {
    private String token;
    private User user;
    
    public LoginResponse() {}
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}

