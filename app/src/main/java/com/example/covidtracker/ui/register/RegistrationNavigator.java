package com.example.covidtracker.ui.register;

import android.content.Context;

public interface RegistrationNavigator {

    void signUp();

    void onRegistrationComplete();

    void onError(String message);

    Context getActivityContext();

}
