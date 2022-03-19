package com.example.covidtracker.ui.dashboard;

import android.os.AsyncTask;

import com.example.covidtracker.data.db.AppDatabase;
import com.example.covidtracker.data.db.dao.SymptomsDao;
import com.example.covidtracker.data.model.Symptoms;
import com.example.covidtracker.ui.base.BaseViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashboardViewModel extends BaseViewModel<DashboardNavigator> {

    AppDatabase db;

    public DashboardViewModel() {
        db = AppDatabase.getInstance();
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
                Symptoms symptoms = new Symptoms(-1, "0", 0, 0, 0,
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

    public void createReport() {
        getNavigator().createReport();
    }

    public void addReportToDb() {
        class ReportTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                SymptomsDao symptomsDao = db.symptomsDao();
                Symptoms symptoms = symptomsDao.getSymptoms().get(symptomsDao.getSymptoms().size() - 1);
                symptoms.userId = getNavigator().getUser().getId();
                long currentTime = System.currentTimeMillis();
                symptoms.creationDate = getDate(currentTime, "EEE, d MMM yyyy HH:mm:ss");
                symptomsDao.updateSymptom(symptoms);
                initSymptomData(true);
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
