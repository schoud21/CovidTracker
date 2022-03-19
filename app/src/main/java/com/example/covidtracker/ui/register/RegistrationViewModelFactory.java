package com.example.covidtracker.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RegistrationViewModelFactory implements ViewModelProvider.Factory {


    public RegistrationViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
            return (T) new RegistrationViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
