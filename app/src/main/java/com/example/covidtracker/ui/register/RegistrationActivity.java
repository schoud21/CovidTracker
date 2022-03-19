package com.example.covidtracker.ui.register;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.covidtracker.R;
import com.example.covidtracker.databinding.ActivityRegisterBinding;
import com.example.covidtracker.ui.base.BaseActivity;

public class RegistrationActivity extends BaseActivity<RegistrationViewModel> implements RegistrationNavigator {

    ActivityRegisterBinding binding;

    @NonNull
    @Override
    protected RegistrationViewModel createViewModel() {
        RegistrationViewModelFactory factory = new RegistrationViewModelFactory();
        return ViewModelProviders.of(this, factory).get(RegistrationViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setDataBindings();
        viewModel.setNavigator(this);
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void signUp() {
        hideKeyboard();
        if (viewModel.isValidated(binding.etFName, binding.etLName, binding.etUserName, binding.etPass,
                binding.etConfirmPass)) {
            viewModel.signUp(binding.etFName.getText().toString().trim(), binding.etLName.getText().toString().trim(),
                    binding.etUserName.getText().toString().trim(), binding.etPass.getText().toString().trim());
        }
    }

    @Override
    public void onRegistrationComplete() {
        Toast.makeText(this, getResources().getString(R.string.toast_registration), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(String message) {
        showSnackbar(message, Color.RED, Color.WHITE);
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}