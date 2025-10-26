package com.just.cn.mgg.backend.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Integer userId;
        private String phone;
        private String nickname;
        private String avatar;
        private String email;
        private Integer gender;
    }
}
