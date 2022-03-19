package com.example.covidtracker.data.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.covidtracker.CovidTrackerApp;
import com.example.covidtracker.data.db.dao.SymptomsDao;
import com.example.covidtracker.data.db.dao.UserDao;
import com.example.covidtracker.data.model.Symptoms;
import com.example.covidtracker.data.model.User;

@Database(entities = {User.class, Symptoms.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mInstance;

    public abstract UserDao userDao();

    public abstract SymptomsDao symptomsDao();

    public static AppDatabase getInstance() {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(CovidTrackerApp.getInstance(), AppDatabase.class, "covidscanner.db").build();
        }
        return mInstance;
    }
}
