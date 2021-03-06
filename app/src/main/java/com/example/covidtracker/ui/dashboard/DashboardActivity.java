package com.example.covidtracker.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

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
import com.example.covidtracker.data.model.UploadResponse;
import com.example.covidtracker.data.model.User;
import com.example.covidtracker.databinding.ActivityDashboardBinding;
import com.example.covidtracker.ui.base.BaseActivity;
import com.example.covidtracker.ui.login.LoginActivity;
import com.example.covidtracker.utils.AlertUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.net.URL;
import java.net.URLConnection;
import java.io.OutputStream;


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
        setRecyclerView();
        mSymptoms = new Symptoms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();

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
    public void upload() {
        File dbFile = this.getDatabasePath("covidscanner.db").getAbsoluteFile();
        System.out.println(dbFile);
        AlertUtil.showAlertDialog(this,
                "Upload to Server",
                "Do you want to upload the DB?",
                "Ok",
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "http://192.168.0.98:5500/upload_video-2.php";
                        String charset = "UTF-8";
                        String CRLF = "\r\n";
                        URLConnection connection = null;

                        try {
                            connection = new URL(url).openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        connection.setDoOutput(true);
                        String boundary = Long.toHexString(System.currentTimeMillis());
                        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                        try (
                                OutputStream output = connection.getOutputStream();
                                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                        ) {
                            // Send video file.
                            writer.append("--" + boundary).append(CRLF);
                            writer.append("Content-Disposition: form-data; name=\"files[]\"; filename=\"" + dbFile.getName() + "\"").append(CRLF);
                            writer.append("Content-Type: file; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
                            writer.append(CRLF).flush();
                            FileInputStream vf = new FileInputStream(dbFile);
                            try {
                                byte[] buffer = new byte[1024];
                                int bytesRead = 0;
                                while ((bytesRead = vf.read(buffer, 0, buffer.length)) >= 0) {
                                    output.write(buffer, 0, bytesRead);

                                }
                            } catch (Exception exception) {


                                //Toast.makeText(getApplicationContext(),"output exception in catch....."+ exception + "", Toast.LENGTH_LONG).show();
                                Log.d("Error", String.valueOf(exception));
//                                publishProgress(String.valueOf(exception));
                                // output.close();

                            }

                            output.flush(); // Important before continuing with writer!
                            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
                            // End of multipart/form-data.
                            writer.append("--" + boundary + "--").append(CRLF).flush();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Request is lazily fired whenever you need to obtain information about response.
                        int responseCode = 0;
                        try {
                            responseCode = ((HttpURLConnection) connection).getResponseCode();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        System.out.println(responseCode); // Should be 200

                        return;
                    }
                },
                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    @Override
    public void uploadRequest() {
        File Db = new File("/data/data/com.example.covidtracker/databases/covidscanner.db");
        Date d = new Date();

        File file = new File("newDb.db"); //if file was created earlier the delete the old file
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.setWritable(true);

        try {
            copyFile(new FileInputStream(Db), new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewModel.uploadRequest(Db);
        viewModel.getUploadResponseLiveData().observe(this, new Observer<UploadResponse>() {
            @Override
            public void onChanged(UploadResponse uploadVideoResponse) {
//                if (uploadVideoResponse != null) {
//                    if (uploadVideoResponse.getSuccess() == 1) {
//                        Toast.makeText(DashboardActivity.this, uploadVideoResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    } else if (uploadVideoResponse.getSuccess() == 0) {
//                        onError(uploadVideoResponse.getMessage());
//                    }
//                }
//                int success = uploadVideoResponse != null ? uploadVideoResponse.getSuccess() : 0;
//                String message = uploadVideoResponse != null ? uploadVideoResponse.getMessage() : "Video Upload Failed!";
//                Intent intent = new Intent();
//                intent.putExtra("success", success);
//                intent.putExtra("message", message);
//                setResult(RESULT_OK, intent);
//                finish();
            }
        });
    }

    @Override
    public User getUser() { return getCurrentUser(); }

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
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
                                    try {
                                        insertData(36, 60, 88);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                }
//                            }, TimeUnit.SECONDS.toMillis(5));
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
// 2 Symptoms are missing
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