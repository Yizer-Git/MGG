package com.just.cn.mgg.backend.service;

import com.just.cn.mgg.backend.dto.LoginResponse;
import com.just.cn.mgg.backend.entity.User;
import com.just.cn.mgg.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    public LoginResponse login(String phone, String password) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 生成JWT Token
        String token = jwtService.generateToken(user);
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setPhone(user.getPhone());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setEmail(user.getEmail());
        userInfo.setGender(user.getGender());
        
        response.setUser(userInfo);
        
        return response;
    }
}
