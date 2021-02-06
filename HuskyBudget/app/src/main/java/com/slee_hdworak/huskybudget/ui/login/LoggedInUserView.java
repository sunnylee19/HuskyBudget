package com.slee_hdworak.huskybudget.ui.login;

import com.slee_hdworak.huskybudget.data.model.User;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private User user;
    //... other data fields that may be accessible to the UI

    public LoggedInUserView(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}