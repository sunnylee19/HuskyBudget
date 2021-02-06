package com.slee_hdworak.huskybudget.ui.transaction;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.slee_hdworak.huskybudget.R;
import com.slee_hdworak.huskybudget.data.daos.RepoDatabase;
import com.slee_hdworak.huskybudget.data.model.Transaction;
import com.slee_hdworak.huskybudget.ui.main.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModifyTransactionActivity extends AppCompatActivity {
    private Transaction transaction;
    private Button confirmButton;
    private Button deleteButton;
    private Button cancelButton;
    private int transactionId;
    private TextView title;
    private EditText name;
    private EditText amount;
    private EditText date;
    private CheckBox wishItem;
    private RepoDatabase db;

    final Calendar calendar = Calendar.getInstance();
    private boolean isDatePickerOpened = false;
    private final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_info);

        String id;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id = null;
            } else {
                id = extras.getString("id");
            }
        } else {
            id = (String) savedInstanceState.getSerializable("id");
        }

        assert id != null;
        transactionId = Integer.parseInt(id);
        db = RepoDatabase.getInstance(ModifyTransactionActivity.this);
        transaction = db.getTransactionDao().findTransactionById(id);
        initializeElements();

        if (transaction.getTransactionAmount() < 0) {
            setTitle("EDIT EXPENSE");
            amount.setText(String.valueOf(transaction.getTransactionAmount() * -1));
        } else {
            setTitle("EDIT INCOME");
            amount.setText(String.valueOf(transaction.getTransactionAmount()));
        }
        name.setText(transaction.getTransactionTitle());
        date.setText(transaction.getTransactionDate());

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
                isDatePickerOpened = false;
            }

        };

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!isDatePickerOpened) {
                    isDatePickerOpened = true;
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ModifyTransactionActivity.this, datePickerListener, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                isDatePickerOpened = false;
                            }
                        }
                    });

                    datePickerDialog.setCancelable(false);

                    datePickerDialog.show();

                }
            }
        });

        wishItem.setChecked(transaction.isWishItem());

        setWindow();
    }

    private void updateDateLabel() {
        Date dateVal = calendar.getTime();
        String today = formatter.format(dateVal);
        date.setText(today);
    }

    private void initializeElements() {
        title = findViewById(R.id.spending_item_title);
        confirmButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);
        cancelButton = findViewById(R.id.cancel_button);
        name = findViewById(R.id.item_name);
        amount = findViewById(R.id.item_amount);
        date = findViewById(R.id.item_date);
        wishItem = findViewById(R.id.wishitem_checkbox);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Date dateToday = Calendar.getInstance().getTime();
        String today = formatter.format(dateToday);
        date.setText(today);
    }

    protected void setTitle(String t) {
        title.setText(t);
    }

    @Override
    protected void onStart() {
        super.onStart();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verifyInputs();
                } catch (Exception e) {
                    String message = e.getMessage();
                    Toast toast = Toast.makeText(ModifyTransactionActivity.this, message, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                if(title.getText().toString().equals("EDIT EXPENSE")) {
                    Intent intent = new Intent();
                    intent.putExtra("toDelete", false);
                    intent.putExtra("transaction_id", transactionId);
                    intent.putExtra("transaction_name", name.getText().toString());
                    intent.putExtra("transaction_amount", Double.parseDouble(String.valueOf(amount.getText())) * -1);
                    intent.putExtra("transaction_date", date.getText().toString());
                    intent.putExtra("transaction_wish", wishItem.isChecked());
                    setResult(RESULT_OK, intent);
                } else if(title.getText().toString().equals("EDIT INCOME")) {
                    Intent intent = new Intent();
                    intent.putExtra("toDelete", false);
                    intent.putExtra("transaction_id", transactionId);
                    intent.putExtra("transaction_name", name.getText().toString());
                    intent.putExtra("transaction_amount", Double.parseDouble(String.valueOf(amount.getText())));
                    intent.putExtra("transaction_date", date.getText().toString());
                    intent.putExtra("transaction_wish", wishItem.isChecked());
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("toDelete", true);
                intent.putExtra("transaction_id", transactionId);
                intent.putExtra("transaction_name", name.getText().toString());
                intent.putExtra("transaction_amount", Double.parseDouble(String.valueOf(amount.getText())));
                intent.putExtra("transaction_date", date.getText().toString());
                intent.putExtra("transaction_wish", wishItem.isChecked());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void verifyInputs() throws Exception {
        if (name.getText() == null || name.getText().toString().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (amount.getText() == null || amount.getText().toString().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }

        try {
            Double.parseDouble(amount.getText().toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse amount");
        }

        if (date.getText() == null || date.getText().toString().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }

        try {
            formatter.parse(date.getText().toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse date");
        }
    }

    private void setWindow() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;

        getWindow().setLayout((int)(width * 0.95), (int)(width * 1.2));
    }
}
