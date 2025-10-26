package com.just.cn.mgg.backend.service;

import com.just.cn.mgg.backend.entity.Address;
import com.just.cn.mgg.backend.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    /**
     * 获取地址列表
     */
    public List<Address> getAddressList(Integer userId) {
        return addressRepository.findByUserId(userId);
    }
    
    /**
     * 获取默认地址
     */
    public Address getDefaultAddress(Integer userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId).orElse(null);
    }
    
    /**
     * 添加地址
     */
    @Transactional
    public Address addAddress(Address address) {
        // 如果设置为默认，取消其他默认地址
        if (address.getIsDefault()) {
            addressRepository.cancelAllDefault(address.getUserId());
        }
        
        // 如果是第一个地址，自动设为默认
        long count = addressRepository.countByUserId(address.getUserId());
        if (count == 0) {
            address.setIsDefault(true);
        }
        
        return addressRepository.save(address);
    }
    
    /**
     * 更新地址
     */
    @Transactional
    public Address updateAddress(Address address) {
        // 验证地址是否存在
        Address existingAddress = addressRepository.findById(address.getAddressId())
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        // 验证所有权
        if (!existingAddress.getUserId().equals(address.getUserId())) {
            throw new RuntimeException("无权修改此地址");
        }
        
        // 如果设置为默认，取消其他默认地址
        if (address.getIsDefault()) {
            addressRepository.cancelAllDefault(address.getUserId());
        }
        
        return addressRepository.save(address);
    }
    
    /**
     * 删除地址
     */
    @Transactional
    public void deleteAddress(Integer addressId, Integer userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        // 验证所有权
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此地址");
        }
        
        boolean wasDefault = address.getIsDefault();
        addressRepository.deleteById(addressId);
        
        // 如果删除的是默认地址，将第一个地址设为默认
        if (wasDefault) {
            List<Address> addresses = addressRepository.findByUserId(userId);
            if (!addresses.isEmpty()) {
                Address firstAddress = addresses.get(0);
                firstAddress.setIsDefault(true);
                addressRepository.save(firstAddress);
            }
        }
    }
    
    /**
     * 设置默认地址
     */
    @Transactional
    public void setDefaultAddress(Integer addressId, Integer userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        // 验证所有权
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此地址");
        }
        
        // 取消其他默认地址
        addressRepository.cancelAllDefault(userId);
        
        // 设置为默认
        address.setIsDefault(true);
        addressRepository.save(address);
    }
    
    /**
     * 获取地址详情
     */
    public Address getAddressDetail(Integer addressId, Integer userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        // 验证所有权
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此地址");
        }
        
        return address;
    }
}

