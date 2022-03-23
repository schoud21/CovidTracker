package com.example.covidtracker.ui.dashboard;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.covidtracker.R;
import com.example.covidtracker.data.db.AppDatabase;
import com.example.covidtracker.data.db.dao.SymptomsDao;
import com.example.covidtracker.data.model.Symptoms;
import com.example.covidtracker.data.model.UploadResponse;
import com.example.covidtracker.data.repository.UploadRepo;
import com.example.covidtracker.ui.base.BaseViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardViewModel extends BaseViewModel<DashboardNavigator> {

    AppDatabase db;
    private final MutableLiveData<List<SymptomsModel>> symptomsLiveData;

    private UploadRepo uploadRepo;
    private LiveData<UploadResponse> uploadResponseLiveData;

    public DashboardViewModel() {
        db = AppDatabase.getInstance();
        symptomsLiveData = new MutableLiveData<>();
        uploadRepo = new UploadRepo();
        uploadResponseLiveData = uploadRepo.getUploadResponseLiveData();
    }

    public void setSymptomsList() {
        class SymptomsListTask extends AsyncTask<Void, Void, List<SymptomsModel>> {
            @Override
            protected List<SymptomsModel> doInBackground(Void... voids) {
                String[] symptomNames = getNavigator().getActivityContext().getResources().getStringArray(R.array.symptomList);
                List<SymptomsModel> symptomsList = new ArrayList<>();

                for (int i = 0; i < symptomNames.length; i++) {
                    switch (symptomNames[i]) {
                        case "Nausea":
                            symptomsList.add(new SymptomsModel(symptomNames[i],0));
                            break;
                        case "Headache":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                        case "Diarrhea":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                        case "Soar Throat":
                            symptomsList.add(new SymptomsModel(symptomNames[i],  0));
                            break;
                        case "Fever":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                        case "Muscle Ache":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                        case "Loss of Smell or Taste":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                        case "Cough":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                        case "Shortness of Breath":
                            symptomsList.add(new SymptomsModel(symptomNames[i],0));
                            break;
                        case "Feeling tired":
                            symptomsList.add(new SymptomsModel(symptomNames[i], 0));
                            break;
                    }
                }
                return symptomsList;
            }

            @Override
            protected void onPostExecute(List<SymptomsModel> symptomsList) {
                super.onPostExecute(symptomsList);
                symptomsLiveData.setValue(symptomsList);
            }
        }

        SymptomsListTask symptomsListTask = new SymptomsListTask();
        symptomsListTask.execute();
    }

    public LiveData<List<SymptomsModel>> getSymptomsLiveData() {
        return symptomsLiveData;
    }

    public void openNextScreen(int screenNum) {
        getNavigator().openNextScreen(screenNum);
    }

    public void logout() {
        getNavigator().logout();
    }


    public void initSymptomData(boolean resetHeartRespi) {
        class InitSymptomsTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                Symptoms symptoms = new Symptoms(-1, "0", 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0);
                if (resetHeartRespi) {
                    symptoms.heartRate = 0;
                    symptoms.respiRate = 0;
                }
                SymptomsDao symptomsDao = db.symptomsDao();
                symptomsDao.deleteByUserId(-1);
                symptomsDao.insertSymptoms(symptoms);
                return null;
            }
        }
        InitSymptomsTask initSymptomsTask = new InitSymptomsTask();
        initSymptomsTask.execute();
    }

    public void insertToDb(Symptoms symptoms) {
        class InitSymptomsTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                SymptomsDao symptomsDao = db.symptomsDao();
                symptomsDao.insertSymptoms(symptoms);
                return null;
            }
        }
        InitSymptomsTask initSymptomsTask = new InitSymptomsTask();
        initSymptomsTask.execute();
    }

    public void submit() {
        getNavigator().submit();
    }

//    public void upload(){
//        getNavigator().upload();
//    }

    public void uploadRequest(File file) {
        uploadRepo.uploadFile(file);
    }

    public void upload() {
        getNavigator().uploadRequest();
    }

    public LiveData<UploadResponse> getUploadResponseLiveData() {
        return uploadResponseLiveData;
    }
    public void addReportToDb() {
        class ReportTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                SymptomsDao symptomsDao = db.symptomsDao();
                Symptoms symptoms = symptomsDao.getSymptoms().get(symptomsDao.getSymptoms().size() - 1);
                symptoms.userId = getNavigator().getUser().getId();
                long currentTime = System.currentTimeMillis();
                symptoms.timestamp = getDate(currentTime, "EEE, d MMM yyyy HH:mm:ss");
                symptomsDao.updateSymptom(symptoms);
//                initSymptomData(true);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                getNavigator().onReportCreateSuccess();
            }
        }
        ReportTask reportTask = new ReportTask();
        reportTask.execute();

    }

    private static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
