package com.slee_hdworak.huskybudget.data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.slee_hdworak.huskybudget.data.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getUsersList();

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE user_id LIKE :uid")
    User findUserById(int uid);

    @Query("SELECT * FROM users WHERE email LIKE :email")
    User findUserByUsername(String email);

    @Query("SELECT COUNT(email) FROM users WHERE email LIKE :email")
    int getUserCountByUsername(String email);

}
