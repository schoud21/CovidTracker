package com.example.covidtracker;

import android.app.Application;

public class CovidTrackerApp extends Application {

    private static CovidTrackerApp mInstance;
    public static CovidTrackerApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
