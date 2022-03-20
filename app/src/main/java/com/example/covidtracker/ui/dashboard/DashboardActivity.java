package com.example.covidtracker.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.covidtracker.R;
import com.example.covidtracker.data.db.AppDatabase;
import com.example.covidtracker.data.db.dao.SymptomsDao;
import com.example.covidtracker.data.db.dao.UserDao;
import com.example.covidtracker.data.model.Symptoms;
import com.example.covidtracker.data.model.User;
import com.example.covidtracker.databinding.ActivityDashboardBinding;
import com.example.covidtracker.ui.base.BaseActivity;
import com.example.covidtracker.ui.login.LoginActivity;
import com.example.covidtracker.utils.AlertUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends BaseActivity<DashboardViewModel> implements DashboardNavigator {

    ActivityDashboardBinding binding;
    private final String TAG = "DashboardActivity<>";
    private final int CAMERA_PERMISSION_REQUEST_CODE = 0x01;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double latitude, longitude;
    String currentTime;

    @NonNull
    @Override
    protected DashboardViewModel createViewModel() {
        DashboardViewModelFactory factory = new DashboardViewModelFactory();
        return ViewModelProviders.of(this, factory).get(DashboardViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
        viewModel.initSymptomData(false);
        setData();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                viewModel.logout();
                break;
        }
        return true;
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
    public void logout() {
        class LogoutTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = AppDatabase.getInstance();
                UserDao userDao = db.userDao();
                User user = null;
                user = getCurrentUser();
                user.isLoggedIn = false;
                userDao.updateUser(user);
                openActivity(LoginActivity.class);
                finish();
                return null;
            }
        }
        LogoutTask logoutTask = new LogoutTask();
        logoutTask.execute();
    }


    @Override
    public void openNextScreen(int screenNum) {
        switch (screenNum) {
            case 1:
//                if (checkPermission())
//                    openActivity(HeartRateActivity.class);
                break;
            case 2:
//                openActivity(RespiratoryRateActivity.class);
                break;
            case 3:
//                openActivity(SymptomActivity.class);
                break;
            case 4:
//                openActivity(ReportsActivity.class);
                break;
        }
    }

    @Override
    public void createReport() {
//        AlertUtil.showAlertDialog(this, getString(R.string.create_report),
//                getString(R.string.confirm_create_report), getString(R.string.btn_ok),
//                getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        viewModel.addReportToDb();
//                    }
//                },
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
    }

    @Override
    public User getUser() {
        return getCurrentUser();
    }

    @Override
    public void onReportCreateSuccess() {
//        showSnackbar(getString(R.string.report_created), Color.GREEN, Color.WHITE);
    }

    public void getLocation() {
        if (this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.e(TAG, "Last location : " + location.toString());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        currentTime = sdf.format(new Date());
                        Log.e(TAG, currentTime);
                    } else {
                        Log.d(TAG, "could not get location");
                    }
                }
            });
        }
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    private void setData() {
        AppDatabase db = AppDatabase.getInstance();
        SymptomsDao symptomsDao = db.symptomsDao();
        symptomsDao.loadTempSymptoms().observe(this, new Observer<List<Symptoms>>() {
            @Override
            public void onChanged(List<Symptoms> symptoms) {
//                Symptoms s = symptoms.get(0);

//                binding.txtHeartRate.setText(String.valueOf(s.heartRate != 0f ? s.heartRate + " BPM" : "No Data"));
//                binding.txtHeartRate.setTextColor(s.heartRate != 0f ? getResources().getColor(android.R.color.holo_green_light) : getResources().getColor(android.R.color.holo_red_light));
//
//                binding.txtRespiRate.setText(String.valueOf(s.respiRate != 0f ? s.respiRate : "No Data"));
//                binding.txtRespiRate.setTextColor(s.respiRate != 0f ? getResources().getColor(android.R.color.holo_green_light) : getResources().getColor(android.R.color.holo_red_light));

            }
        });
    }

}