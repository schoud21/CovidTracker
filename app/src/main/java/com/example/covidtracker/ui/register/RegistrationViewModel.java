package com.example.covidtracker.ui.register;

import android.os.AsyncTask;

import com.example.covidtracker.R;
import com.example.covidtracker.data.db.AppDatabase;
import com.example.covidtracker.data.db.dao.UserDao;
import com.example.covidtracker.data.model.User;
import com.example.covidtracker.ui.base.BaseViewModel;
import com.example.covidtracker.utils.Validator;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationViewModel extends BaseViewModel<RegistrationNavigator> {

    AppDatabase db;

    public RegistrationViewModel() {
        db = AppDatabase.getInstance();
    }

    public boolean isValidated(TextInputEditText firstName, TextInputEditText lastName,
                               TextInputEditText username, TextInputEditText password,
                               TextInputEditText confirmPassword) {
        return Validator.isValid(firstName, Validator.Type.FIRSTNAME)
                && Validator.isValid(lastName, Validator.Type.LASTNAME)
                && Validator.isValid(username, Validator.Type.USERNAME)
                && Validator.isValid(password, Validator.Type.PASSWORD)
                && Validator.isValid(confirmPassword, Validator.Type.PASSWORD)
                && Validator.isPasswordMatch(password, confirmPassword);
    }

    public void signUp(String firstName, String lastName, String username,
                       String password) {
        final boolean[] isRegistrationSuccess = {false};
        UserDao userDao = db.userDao();
        User user = new User(username, password, firstName, lastName);
        class RegisterTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                if (!userDao.getUsernames().contains(username)) {
                    userDao.insertUser(user);
                    isRegistrationSuccess[0] = true;
                } else {
                    isRegistrationSuccess[0] = false;
                    getNavigator().onError(getNavigator().getActivityContext().getResources().getString(R.string.error_username));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                if (isRegistrationSuccess[0])
                getNavigator().onRegistrationComplete();
            }
        }
        RegisterTask registerTask = new RegisterTask();
        registerTask.execute();
    }

    public void register() {
        getNavigator().signUp();
    }
}
