package com.slee_hdworak.huskybudget.ui.edit;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.slee_hdworak.huskybudget.R;
import com.slee_hdworak.huskybudget.data.LoginRepository;
import com.slee_hdworak.huskybudget.data.Result;
import com.slee_hdworak.huskybudget.data.model.User;
import com.slee_hdworak.huskybudget.ui.login.LoggedInUserView;

public class EditViewModel extends ViewModel {

    private MutableLiveData<EditFormState> editFormState = new MutableLiveData<>();
    private MutableLiveData<EditResult> editResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    EditViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<EditFormState> getEditFormState() {
        return editFormState;
    }

    LiveData<EditResult> getEditResult() {
        return editResult;
    }

    public void edit(String username, String firstName, String lastName, String password) {
        // can be launched in a separate asynchronous job
        Result<User> result = loginRepository.edit(username, firstName, lastName, password);

        if (result instanceof Result.Success) {
            User data = ((Result.Success<User>) result).getData();
            editResult.setValue(new EditResult(new LoggedInUserView(data)));
        } else {
            editResult.setValue(new EditResult(R.string.edit_failed));
        }
    }

    public void editDataChanged(String firstName, String lastName, String username, String password, String confirmPassword) {
        if(!isFirstNameValid(firstName)) {
            editFormState.setValue(new EditFormState(R.string.invalid_first_name, null, null, null, null));
        } else if (!isLastNameValid(lastName)) {
            editFormState.setValue(new EditFormState(null, R.string.invalid_last_name, null, null, null));
        } else if (!isUserNameValid(username)) {
            editFormState.setValue(new EditFormState(null, null, R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            editFormState.setValue(new EditFormState(null, null, null, R.string.invalid_password, null));
        } else if (!isConfirmPasswordValid(password, confirmPassword)){
            editFormState.setValue(new EditFormState(null, null, null, null, R.string.invalid_confirm_password));
        } else {
            editFormState.setValue(new EditFormState(true));
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