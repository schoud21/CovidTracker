package com.example.covidtracker.ui.login;

import android.content.Context;

import com.example.covidtracker.data.model.User;

public interface LoginNavigator {

    void openRegistration();

    Context getActivityContext();

    void onError(String message);

    void onLoginComplete(User user);

    void login();
}
