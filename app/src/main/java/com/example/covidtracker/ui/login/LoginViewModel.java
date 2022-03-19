package com.example.covidtracker.ui.login;

import android.os.AsyncTask;

import com.example.covidtracker.R;
import com.example.covidtracker.data.db.AppDatabase;
import com.example.covidtracker.data.db.dao.UserDao;
import com.example.covidtracker.data.model.User;
import com.example.covidtracker.ui.base.BaseViewModel;
import com.example.covidtracker.utils.Validator;
import com.google.android.material.textfield.TextInputEditText;

public class LoginViewModel extends BaseViewModel<LoginNavigator> {

    AppDatabase db;

    public LoginViewModel() {
        db = AppDatabase.getInstance();
    }

    public boolean isValidated(TextInputEditText username, TextInputEditText password) {
        return Validator.isValid(username, Validator.Type.USERNAME)
                && Validator.isValid(password, Validator.Type.PASSWORD);
    }

    public void attemptLogin(String username, String password) {
        final boolean[] isLoginSuccess = {false};
        UserDao userDao = db.userDao();
        class LoginTask extends AsyncTask<Void, Void, User> {
            @Override
            protected User doInBackground(Void... voids) {
                User user = null;
                if (userDao.findByUsername(username) != null &&
                        userDao.findByUsername(username).size() > 0)
                    user = userDao.findByUsername(username).get(0);
                if (user != null && user.username.equals(username)) {
                    if (user.password.equals(password)) {
                        user.isLoggedIn = true;
                        userDao.updateUser(user);
                        isLoginSuccess[0] = true;
                    } else {
                        isLoginSuccess[0] = false;
                        getNavigator().onError(getNavigator().getActivityContext().getResources().getString(R.string.error_wrong_password));
                    }
                } else {
                    isLoginSuccess[0] = false;
                    getNavigator().onError(getNavigator().getActivityContext().getResources().getString(R.string.error_wrong_login));
                }
                return user;
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if (isLoginSuccess[0])
                    getNavigator().onLoginComplete(user);
            }
        }
        LoginTask loginTask = new LoginTask();
        loginTask.execute();
    }

    public void login() {
        getNavigator().login();
    }

    public void openRegistration() {
        getNavigator().openRegistration();
    }
}
