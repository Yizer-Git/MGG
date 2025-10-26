package com.just.cn.mgg.backend.controller;

import com.just.cn.mgg.backend.dto.ApiResponse;
import com.just.cn.mgg.backend.entity.Address;
import com.just.cn.mgg.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "*")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 获取地址列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Address>>> getAddressList(Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            List<Address> addresses = addressService.getAddressList(userId);
            return ResponseEntity.ok(ApiResponse.success(addresses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<Address>> getDefaultAddress(Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Address address = addressService.getDefaultAddress(userId);
            if (address == null) {
                return ResponseEntity.ok(ApiResponse.error("暂无默认地址"));
            }
            return ResponseEntity.ok(ApiResponse.success(address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取地址详情
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Address>> getAddressDetail(
            @PathVariable Integer addressId,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            Address address = addressService.getAddressDetail(addressId, userId);
            return ResponseEntity.ok(ApiResponse.success(address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 添加地址
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Address>> addAddress(
            @RequestBody Address address,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            address.setUserId(userId);
            
            Address savedAddress = addressService.addAddress(address);
            return ResponseEntity.ok(ApiResponse.success(savedAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 更新地址
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @RequestBody Address address,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            address.setUserId(userId);
            
            Address updatedAddress = addressService.updateAddress(address);
            return ResponseEntity.ok(ApiResponse.success(updatedAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 删除地址
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @RequestParam Integer address_id,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            addressService.deleteAddress(address_id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 设置默认地址
     */
    @PostMapping("/setDefault")
    public ResponseEntity<ApiResponse<String>> setDefaultAddress(
            @RequestParam Integer address_id,
            Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuth(authentication);
            addressService.setDefaultAddress(address_id, userId);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 从认证信息获取用户ID
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        // TODO: 从JWT Token中提取用户ID
        // 暂时返回测试用户ID
        return 1;
    }
}

