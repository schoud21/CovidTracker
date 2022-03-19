package com.example.covidtracker.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user")
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String username;

    @ColumnInfo
    public String password;

    @ColumnInfo
    public String firstName;

    @ColumnInfo
    public String lastName;

    @ColumnInfo
    public boolean isLoggedIn;

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isLoggedIn = false;
    }

    public int getId() {
        return id;
    }
}
