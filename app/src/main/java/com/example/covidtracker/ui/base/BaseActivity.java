package com.example.covidtracker.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import com.example.covidtracker.data.model.User;
import com.example.covidtracker.utils.SharedPreferenceHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public abstract class BaseActivity<VM extends ViewModel> extends AppCompatActivity {

    protected VM viewModel;

    @NonNull
    protected abstract VM createViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = createViewModel();
    }

    public void setCurrentUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferenceHelper.setSharedPreferenceString(this, "CurrentUser", json);
    }

    public User getCurrentUser() {
        Gson gson = new Gson();
        String json = SharedPreferenceHelper.getSharedPreferenceString(this, "CurrentUser", null);
        return gson.fromJson(json, User.class);
    }

    public void openActivity(Class activity) {
        startActivity(new Intent(this, activity));
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void showSnackbar(String message, int bgColor, int txtColor) {
        Snackbar snackbar;
        snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(bgColor);
        TextView textView = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(txtColor);
        snackbar.show();
    }
}