package com.slee_hdworak.huskybudget.ui.register;

import androidx.annotation.Nullable;

import com.slee_hdworak.huskybudget.ui.login.LoggedInUserView;

/**
 * Registration result : success (user details) or error message.
 */
class RegisterResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}