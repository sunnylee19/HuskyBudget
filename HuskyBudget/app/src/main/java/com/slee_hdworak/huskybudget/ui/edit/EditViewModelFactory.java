package com.slee_hdworak.huskybudget.ui.edit;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.slee_hdworak.huskybudget.data.LoginDataSource;
import com.slee_hdworak.huskybudget.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate EditViewModel.
 * Required given EditViewModel has a non-empty constructor
 */
public class EditViewModelFactory implements ViewModelProvider.Factory {
    Activity activity;

    public EditViewModelFactory(EditActivity editActivity) {
        activity = editActivity;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EditViewModel.class)) {
            return (T) new EditViewModel(LoginRepository.getInstance(new LoginDataSource(activity)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}