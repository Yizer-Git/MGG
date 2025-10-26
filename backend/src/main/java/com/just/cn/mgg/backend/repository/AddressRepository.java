package com.just.cn.mgg.backend.repository;

import com.just.cn.mgg.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    
    /**
     * 查询用户的所有地址
     */
    List<Address> findByUserId(Integer userId);
    
    /**
     * 查询用户的默认地址
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(Integer userId);
    
    /**
     * 取消用户所有地址的默认状态
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.userId = :userId")
    void cancelAllDefault(Integer userId);
    
    /**
     * 统计用户的地址数量
     */
    long countByUserId(Integer userId);
}

