package com.slee_hdworak.huskybudget.ui.transaction;

import android.os.Bundle;

import androidx.annotation.Nullable;

public class AddExpenseActivity extends TransactionActivity {

    private static final String ADD_EXPENSE_TITLE = "ADD EXPENSE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(ADD_EXPENSE_TITLE);
    }
}
