package com.slee_hdworak.huskybudget.ui.transaction;

import android.os.Bundle;

import androidx.annotation.Nullable;

public class AddIncomeActivity extends TransactionActivity {

    private static final String ADD_INCOME_TITLE = "ADD INCOME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(ADD_INCOME_TITLE);
    }
}
