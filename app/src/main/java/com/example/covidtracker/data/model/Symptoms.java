package com.example.covidtracker.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "symptoms")
public class Symptoms {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(defaultValue = "0")
    public int userId;

    @ColumnInfo(defaultValue = "0")
    public String timestamp;

    @ColumnInfo(defaultValue = "0")
    public double X;

    @ColumnInfo(defaultValue = "0")
    public double Y;

    @ColumnInfo(defaultValue = "0")
    public float nausea;

    @ColumnInfo(defaultValue = "0")
    public float headache;

    @ColumnInfo(defaultValue = "0")
    public float diarrhea;

    @ColumnInfo(defaultValue = "0")
    public float soarThroat;

    @ColumnInfo(defaultValue = "0")
    public float fever;

    @ColumnInfo(defaultValue = "0")
    public float muscleAche;

    @ColumnInfo(defaultValue = "0")
    public float smellLoss;

    @ColumnInfo(defaultValue = "0")
    public float cough;

    @ColumnInfo(defaultValue = "0")
    public float shortnessBreath;

    @ColumnInfo(defaultValue = "0")
    public float tiredness;

    @ColumnInfo(defaultValue = "0")
    public float heartRate;

    @ColumnInfo(defaultValue = "0")
    public float respiRate;

    public Symptoms() {
    }

    @Ignore
    public Symptoms(int userId, String timestamp, double X, double Y, float nausea, float headache, float diarrhea, float soarThroat, float fever, float muscleAche, float smellLoss, float cough, float shortnessBreath, float tiredness, float heartRate, float respiRate) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.X = X;
        this.Y = Y;
        this.nausea = nausea;
        this.headache = headache;
        this.diarrhea = diarrhea;
        this.soarThroat = soarThroat;
        this.fever = fever;
        this.muscleAche = muscleAche;
        this.smellLoss = smellLoss;
        this.cough = cough;
        this.shortnessBreath = shortnessBreath;
        this.tiredness = tiredness;
        this.heartRate = heartRate;
        this.respiRate = respiRate;
    }
}
