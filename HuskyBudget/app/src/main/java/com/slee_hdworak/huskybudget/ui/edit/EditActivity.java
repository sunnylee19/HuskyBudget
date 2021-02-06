package com.slee_hdworak.huskybudget.ui.edit;

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

public class EditActivity extends AppCompatActivity {
    private EditViewModel editViewModel;
    private boolean editDataValid;
    private static final String EDIT_TITLE = "EDIT";
    private static final String UPDATE_TEXT = "UPDATE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account);
        editViewModel = ViewModelProviders.of(this, new EditViewModelFactory(this))
                .get(EditViewModel.class);

        final TextView title = findViewById(R.id.register_title);
        final EditText firstName = findViewById(R.id.first_name);
        final EditText lastName = findViewById(R.id.last_name);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText confirmPassword = findViewById(R.id.confirm_password);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        Button editButton = findViewById(R.id.register_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        title.setText(EDIT_TITLE);
        editButton.setText(UPDATE_TEXT);

        firstName.setText(getIntent().getStringExtra("FIRST_NAME"));
        lastName.setText(getIntent().getStringExtra("LAST_NAME"));
        username.setText(getIntent().getStringExtra("USERNAME"));
        username.setEnabled(false);

        editDataValid = false;

        editViewModel.getEditFormState().observe(this, new Observer<EditFormState>() {
            @Override
            public void onChanged(@Nullable EditFormState editFormState) {
                editDataValid = false;
                if (editFormState == null) {
                    return;
                }
                if (editFormState.getFirstNameError() != null) {
                    firstName.setError(getString(editFormState.getFirstNameError()));
                }
                if (editFormState.getLastNameError() != null) {
                    lastName.setError(getString(editFormState.getLastNameError()));
                }
                if (editFormState.getUsernameError() != null) {
                    username.setError(getString(editFormState.getUsernameError()));
                }
                if (editFormState.getPasswordError() != null) {
                    password.setError(getString(editFormState.getPasswordError()));
                }
                if (editFormState.getConfirmPasswordError() != null) {
                    confirmPassword.setError(getString(editFormState.getConfirmPasswordError()));
                }
                if (editFormState.getFirstNameError() == null && editFormState.getLastNameError() == null && editFormState.getUsernameError() == null && editFormState.getPasswordError() == null && editFormState.getConfirmPasswordError() == null) {
                    editDataValid = true;
                }
            }
        });

        editViewModel.getEditResult().observe(this, new Observer<EditResult>() {
            @Override
            public void onChanged(@Nullable EditResult editResult) {
                if (editResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (editResult.getError() != null) {
                    showEditFailed(editResult.getError());
                }
                if (editResult.getSuccess() != null) {
                    updateUiWithUser(editResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                }
            }
        });

        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editViewModel.editDataChanged(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(),
                            password.getText().toString(), confirmPassword.getText().toString());
                    if (editDataValid) {
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        editViewModel.edit(username.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                                password.getText().toString());
                    }

                }
                return false;
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editViewModel.editDataChanged(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(),
                        password.getText().toString(), confirmPassword.getText().toString());
                if (editDataValid) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    editViewModel.edit(username.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
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

    private void showEditFailed(@StringRes Integer errorString) {
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
