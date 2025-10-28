package com.just.cn.mgg.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.just.cn.mgg.data.local.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    UserEntity findByPhone(String phone);

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    UserEntity findById(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserEntity> users);

    @Query("SELECT MAX(userId) FROM users")
    Integer getMaxUserId();

    @Query("DELETE FROM users")
    void clearAll();
}
