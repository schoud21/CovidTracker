package com.example.covidtracker.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.covidtracker.R;
import com.example.covidtracker.data.db.AppDatabase;
import com.example.covidtracker.data.db.dao.SymptomsDao;
import com.example.covidtracker.data.db.dao.UserDao;
import com.example.covidtracker.data.model.Symptoms;
import com.example.covidtracker.data.model.User;
import com.example.covidtracker.databinding.ActivityLoginBinding;
import com.example.covidtracker.ui.base.BaseActivity;
import com.example.covidtracker.ui.dashboard.DashboardActivity;
import com.example.covidtracker.ui.register.RegistrationActivity;

import java.util.List;

public class LoginActivity extends BaseActivity<LoginViewModel> implements LoginNavigator {

    ActivityLoginBinding binding;
    AppDatabase db;

    @NonNull
    @Override
    protected LoginViewModel createViewModel() {
        LoginViewModelFactory factory = new LoginViewModelFactory();
        return ViewModelProviders.of(this, factory).get(LoginViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setDataBindings();
        viewModel.setNavigator(this);
        db = AppDatabase.getInstance();
//        setReportList();
    }
    private void setReportList() {
        SymptomsDao symptomsDao = db.symptomsDao();
        symptomsDao.findByUserId(1).observe(this, new Observer<List<Symptoms>>() {
            @Override
            public void onChanged(List<Symptoms> symptoms) {
String s = "";            }
        });
    }
    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void openRegistration() {
        openActivity(RegistrationActivity.class);
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void onError(String message) {
        showSnackbar(message, Color.RED, Color.WHITE);
    }

    @Override
    public void onLoginComplete(User user) {
        setCurrentUser(user);
        Toast.makeText(this, getResources().getString(R.string.toast_login), Toast.LENGTH_SHORT).show();
        openActivity(DashboardActivity.class);
        finish();
    }

    @Override
    public void login() {
        if (checkPermission()) {
            hideKeyboard();
            if (viewModel.isValidated(binding.etUserName, binding.etPass)) {
                viewModel.attemptLogin(binding.etUserName.getText().toString().trim(),
                        binding.etPass.getText().toString().trim());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openActivity(DashboardActivity.class);
                break;
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{
                            android.Manifest.permission.BLUETOOTH,
                            android.Manifest.permission.BLUETOOTH_ADMIN,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.CHANGE_WIFI_STATE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1);
            return false;
        }

        return true;
    }
}