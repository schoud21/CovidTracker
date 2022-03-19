package com.example.covidtracker.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.covidtracker.data.model.Symptoms;

import java.util.List;

@Dao
public interface SymptomsDao {
    @Delete
    void deleteEntry(Symptoms symptoms);

    @Query("SELECT * FROM symptoms")
    List<Symptoms> getSymptoms();

    @Insert
    void insertSymptoms(Symptoms... symptoms);

    @Update
    void updateSymptom(Symptoms symptoms);

    @Query("SELECT * FROM symptoms WHERE userId LIKE :userId ORDER BY ID DESC")
    LiveData<List<Symptoms>> findByUserId(int userId);

    @Query("DELETE FROM symptoms WHERE userId = :userId")
    void deleteByUserId(int userId);

    @Query("SELECT * FROM symptoms ORDER BY ID DESC LIMIT 1")
    LiveData<List<Symptoms>> loadSymptoms();

    @Query("SELECT * FROM symptoms WHERE userId = -1 ORDER BY ID DESC LIMIT 1")
    LiveData<List<Symptoms>> loadTempSymptoms();
}
