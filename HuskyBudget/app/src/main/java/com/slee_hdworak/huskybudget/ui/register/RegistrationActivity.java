package com.slee_hdworak.huskybudget.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.slee_hdworak.huskybudget.R;
import com.slee_hdworak.huskybudget.data.model.User;
import com.slee_hdworak.huskybudget.ui.login.LoggedInUserView;
import com.slee_hdworak.huskybudget.ui.main.MainActivity;

public class RegistrationActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;
    private boolean registerDataValid;
    private static final String REGISTRATION_TITLE = "REGISTRATION";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory(this))
                .get(RegisterViewModel.class);

        final TextView title = findViewById(R.id.register_title);
        final EditText firstName = findViewById(R.id.first_name);
        final EditText lastName = findViewById(R.id.last_name);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText confirmPassword = findViewById(R.id.confirm_password);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        Button registerButton = findViewById(R.id.register_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        title.setText(REGISTRATION_TITLE);

        registerDataValid = false;

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                registerDataValid = false;
                if (registerFormState == null) {
                    return;
                }
                if (registerFormState.getFirstNameError() != null) {
                    firstName.setError(getString(registerFormState.getFirstNameError()));
                }
                if (registerFormState.getLastNameError() != null) {
                    lastName.setError(getString(registerFormState.getLastNameError()));
                }
                if (registerFormState.getUsernameError() != null) {
                    username.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    password.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getConfirmPasswordError() != null) {
                    confirmPassword.setError(getString(registerFormState.getConfirmPasswordError()));
                }
                if (registerFormState.getFirstNameError() == null && registerFormState.getLastNameError() == null && registerFormState.getUsernameError() == null && registerFormState.getPasswordError() == null && registerFormState.getConfirmPasswordError() == null) {
                    registerDataValid = true;
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                if (registerResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (registerResult.getError() != null) {
                    showRegisterFailed(registerResult.getError());
                }
                if (registerResult.getSuccess() != null) {
                    updateUiWithUser(registerResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                }
            }
        });

        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.registerDataChanged(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(),
                            password.getText().toString(), confirmPassword.getText().toString());
                    if (registerDataValid) {
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        registerViewModel.register(username.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                                password.getText().toString());
                    }

                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerViewModel.registerDataChanged(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(),
                        password.getText().toString(), confirmPassword.getText().toString());
                if (registerDataValid) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    registerViewModel.register(username.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                            password.getText().toString());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = String.format(getString(R.string.welcome), model.getUser().firstName);
        startMainActivity(model.getUser());
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FIRST_NAME", user.firstName); // String
        intent.putExtra("LAST_NAME", user.lastName); // String
        intent.putExtra("USERNAME", user.email); // String
        intent.putExtra("USER_ID", user.userId); // Int
        startActivity(intent);
        //Complete and destroy registration activity once successful
        finish();
    }


}
