package com.example.covidtracker.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.covidtracker.data.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Insert
    void insertUser(User... user);

    @Update
    void updateUser(User user);

    @Query("SELECT username FROM user")
    List<String> getUsernames();

    @Query("SELECT * FROM user WHERE isLoggedIn")
    List<User> isLoggedIn();

    @Query("SELECT * FROM user WHERE username LIKE :username LIMIT 1")
    List<User> findByUsername(String username);

}
