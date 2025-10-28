package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.just.cn.mgg.data.local.entity.AddressEntity;

import java.util.List;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, createTime DESC")
    List<AddressEntity> getAddressesForUser(int userId);

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    AddressEntity getDefaultAddress(int userId);

    @Query("SELECT * FROM addresses WHERE addressId = :addressId LIMIT 1")
    AddressEntity findById(int addressId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AddressEntity address);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AddressEntity> addresses);

    @Update
    int update(AddressEntity address);

    @Delete
    int delete(AddressEntity address);

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    void clearDefault(int userId);

    @Query("DELETE FROM addresses")
    void clearAll();
}
