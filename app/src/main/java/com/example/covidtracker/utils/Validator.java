package com.example.covidtracker.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.covidtracker.R;

public final class Validator {

    public enum Type {
        USERNAME, PASSWORD, FIRSTNAME, LASTNAME
    }

    public static boolean isValid(EditText edt, Type type) {
        boolean isValid = true;
        switch (type) {
            case USERNAME:
                isValid = isValidUsername(edt);
                break;
            case PASSWORD:
                isValid = isValidPassword(edt);
                break;
            case FIRSTNAME:
                isValid = isValidFirstName(edt);
                break;
            case LASTNAME:
                isValid = isValidLastName(edt);
                break;
        }
        return isValid;
    }

    private static boolean isValidUsername(EditText edt) {
        if (!TextUtils.isEmpty(edt.getText().toString().trim()))
            return true;
        edt.requestFocus();
        edt.addTextChangedListener(new ValidationWatcher(edt));
        edt.setError(edt.getContext().getString(R.string.validate_username));
        return false;
    }

    private static boolean isValidFirstName(EditText edt) {
        if (!TextUtils.isEmpty(edt.getText().toString().trim())
                && edt.getText().toString().trim().length() >= 3)
            return true;
        edt.requestFocus();
        edt.addTextChangedListener(new ValidationWatcher(edt));
        edt.setError(edt.getContext().getString(R.string.validate_first_name));
        return false;
    }


    private static boolean isValidLastName(EditText edt) {
        if (!TextUtils.isEmpty(edt.getText().toString().trim())
                && edt.getText().toString().trim().length() >= 1)
            return true;
        edt.requestFocus();
        edt.addTextChangedListener(new ValidationWatcher(edt));
        edt.setError(edt.getContext().getString(R.string.validate_last_name));
        return false;
    }

    private static boolean isValidPassword(EditText edt) {
        if (edt.getText().toString().trim().length() >= 6 && edt.getText().toString().trim().length() <= 12) {
            return true;
        }
        edt.requestFocus();
        edt.addTextChangedListener(new ValidationWatcher(edt));
        edt.setError(edt.getContext().getString(R.string.validate_password));
        return false;
    }

    public static boolean isPasswordMatch(EditText password, EditText retypedPassword) {
        if (password.getText().toString().trim().equals(retypedPassword.getText().toString().trim()))
            return true;
        retypedPassword.requestFocus();
        retypedPassword.addTextChangedListener(new ValidationWatcher(retypedPassword));
        retypedPassword.setError(retypedPassword.getContext().getString(R.string.password_mismatch));
        return false;
    }

    /*private static boolean isValidPassword(EditText edt) {
        String pattern = "^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-ZÀ-ÿ0-9!@#$%^&*]{6,16}$";
        boolean isValid = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(edt.getText().toString().trim()).matches();
        if (edt.getText().toString().trim().length() >= 6 && edt.getText().toString().trim().length() <= 12 && isValid) {
            return true;
        }
        edt.requestFocus();
        edt.addTextChangedListener(new ValidationWatcher(edt));
        edt.setError(edt.getContext().getString(R.string.validate_password));
        return false;
    }*/

    private static class ValidationWatcher implements TextWatcher {
        private EditText edt;

        public ValidationWatcher(EditText edt) {
            this.edt = edt;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            edt.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
