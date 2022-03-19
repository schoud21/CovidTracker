package com.example.covidtracker.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {


    public DashboardViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
