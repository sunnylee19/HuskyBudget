package com.slee_hdworak.huskybudget.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.slee_hdworak.huskybudget.R;
import com.slee_hdworak.huskybudget.data.LoginRepository;
import com.slee_hdworak.huskybudget.data.Result;
import com.slee_hdworak.huskybudget.data.model.User;
import com.slee_hdworak.huskybudget.ui.login.LoggedInUserView;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String firstName, String lastName, String password) {
        // can be launched in a separate asynchronous job
        Result<User> result = loginRepository.register(username, firstName, lastName, password);

        if (result instanceof Result.Success) {
            User data = ((Result.Success<User>) result).getData();
            registerResult.setValue(new RegisterResult(new LoggedInUserView(data)));
        } else {
            registerResult.setValue(new RegisterResult(R.string.register_failed));
        }
    }

    public void registerDataChanged(String firstName, String lastName, String username, String password, String confirmPassword) {
        if(!isFirstNameValid(firstName)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_first_name, null, null, null, null));
        } else if (!isLastNameValid(lastName)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_last_name, null, null, null));
        } else if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_password, null));
        } else if (!isConfirmPasswordValid(password, confirmPassword)){
            registerFormState.setValue(new RegisterFormState(null, null, null, null, R.string.invalid_confirm_password));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder firstName validation check
    private boolean isFirstNameValid(String firstName) {
        return !firstName.equals("");
    }

    // A placeholder lastName validation check
    private boolean isLastNameValid(String lastName) {
        return !lastName.equals("");
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // A placeholder confirmPassword validation check
    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}