package com.slee_hdworak.huskybudget.ui.login;

import android.app.Activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.slee_hdworak.huskybudget.data.LoginDataSource;
import com.slee_hdworak.huskybudget.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {
    Activity activity;

    public LoginViewModelFactory(LoginActivity loginActivity) {
        activity = loginActivity;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource(activity)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}