package com.slee_hdworak.huskybudget.ui.edit;

import androidx.annotation.Nullable;

import com.slee_hdworak.huskybudget.ui.login.LoggedInUserView;

/**
 * Edit result : success (user details) or error message.
 */
class EditResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    EditResult(@Nullable Integer error) {
        this.error = error;
    }

    EditResult(@Nullable LoggedInUserView success) {
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