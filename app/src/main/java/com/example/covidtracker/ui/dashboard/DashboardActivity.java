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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DashboardActivity extends BaseActivity<DashboardViewModel> implements DashboardNavigator, SymptomsAdapter.RatingClickListener {

    ActivityDashboardBinding binding;
    private final String TAG = "DashboardActivity<>";
    private final int CAMERA_PERMISSION_REQUEST_CODE = 0x01;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double latitude, longitude;
    String currentTime;
    SymptomsAdapter adapter;
    Symptoms mSymptoms;

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
//        viewModel.initSymptomData(false);
        setData();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        setRecyclerView();
        mSymptoms = new Symptoms();
    }

    private void setRecyclerView() {
        adapter = new SymptomsAdapter(this, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rvSymptoms.getContext(),
                new LinearLayoutManager(this).getOrientation());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvSymptoms.setLayoutManager(layoutManager);
        binding.rvSymptoms.addItemDecoration(dividerItemDecoration);
        binding.rvSymptoms.setAdapter(adapter);

        viewModel.setSymptomsList();

        viewModel.getSymptomsLiveData().observe(this, new Observer<List<SymptomsModel>>() {
            @Override
            public void onChanged(List<SymptomsModel> symptomsModels) {
                updateList(symptomsModels);
            }
        });
    }

    public void updateList(List<SymptomsModel> symptomList) {
        adapter.clearAll();
        adapter.addAll(symptomList);
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
    public void submit() {
        AlertUtil.showAlertDialog(this, "Symptoms Ratings",
                "Do you want to submit these symptoms ratings?", "Ok",
                "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        mSymptoms.userId = getCurrentUser().id;
                        mSymptoms.timestamp = sdf.format(new Date());
                        mSymptoms.X = latitude;
                        mSymptoms.Y = longitude;
                        viewModel.insertToDb(mSymptoms);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        insertData(36, 60, 88);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, TimeUnit.SECONDS.toMillis(5));
                    } else {
                        Log.d(TAG, "could not get location");
                    }
                }
            });
        }
    }

    public void insertData(int interval1, int interval2, int interval3) throws ParseException {
        String sDate = "2022/03/17 00:14:33";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

        if (this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            int n = 24*4;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < n; j++) {
                    Date date = dateFormat.parse(sDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE, 15);
                    String newTime = dateFormat.format(calendar.getTime());
                    sDate = newTime;
                    if (j == interval1 - 1 || j == interval2 - 1 || j == interval3 - 1 ) {
                        Symptoms s = new Symptoms(getCurrentUser().id, newTime, latitude, longitude,
                                getRandom(), getRandom(), getRandom(), getRandom(), getRandom(), getRandom(),
                                getRandom(), getRandom(), getRandom(), getRandom(), getRandom(), getRandom());
                        viewModel.insertToDb(s);
                    } else {
                        Symptoms s = new Symptoms(getCurrentUser().id, newTime, latitude, longitude,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                        viewModel.insertToDb(s);
                    }

                }
            }
        }
    }

    public float getRandom() {
        float[] arr = new float[] {0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f};
        int rnd = new Random().nextInt(arr.length);
        return arr[rnd];
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

    @Override
    public void onRatingClick(SymptomsModel symptom, float rating) {
        class RatingTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                switch (symptom.getName()) {
                    case "Nausea":
                        mSymptoms.nausea = rating;
                        break;
                    case "Headache":
                        mSymptoms.headache = rating;
                        break;
                    case "Diarrhea":
                        mSymptoms.diarrhea = rating;
                        break;
                    case "Soar Throat":
                        mSymptoms.soarThroat = rating;
                        break;
                    case "Fever":
                        mSymptoms.fever = rating;
                        break;
                    case "Muscle Ache":
                        mSymptoms.muscleAche = rating;
                        break;
                    case "Loss of Smell or Taste":
                        mSymptoms.smellLoss = rating;
                        break;
                    case "Cough":
                        mSymptoms.cough = rating;
                        break;
                    case "Shortness of Breath":
                        mSymptoms.shortnessBreath = rating;
                        break;
                    case "Feeling tired":
                        mSymptoms.tiredness = rating;
                        break;
                }
                return null;
            }
        }
        RatingTask ratingTask = new RatingTask();
        ratingTask.execute();
    }
}