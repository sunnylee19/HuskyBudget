package com.slee_hdworak.huskybudget.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.slee_hdworak.huskybudget.R;
import com.slee_hdworak.huskybudget.data.daos.RepoDatabase;
import com.slee_hdworak.huskybudget.data.model.Transaction;
import com.slee_hdworak.huskybudget.data.model.User;
import com.slee_hdworak.huskybudget.ui.edit.EditActivity;
import com.slee_hdworak.huskybudget.ui.login.LoginActivity;
import com.slee_hdworak.huskybudget.ui.sensor.ShakeDetector;
import com.slee_hdworak.huskybudget.ui.transaction.AddExpenseActivity;
import com.slee_hdworak.huskybudget.ui.transaction.AddIncomeActivity;
import com.slee_hdworak.huskybudget.ui.transaction.ModifyTransactionActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    private final int ADD_INCOME_CODE = 1;
    private final int ADD_EXPENSE_CODE = 2;
    private final int EDIT_TRANSACTION_CODE = 3;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private boolean includeWishItems = false;

    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private RepoDatabase db;
    private Calendar cal =  Calendar.getInstance();
    private ImageView heartIcon;
    private ImageButton accountIcon;
    private TextView monthView;
    private TextView remainingBudget;
    private ListView transactionListView;
    private Button expenseButton;
    private Button incomeButton;

    private List<Transaction> transactions = new ArrayList<>();
    private final DateFormat monthFormatter = new SimpleDateFormat("MM/YYYY");
    private final DateFormat monthTypedFormatter = new SimpleDateFormat("MMMM YYYY");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        userId = getIntent().getIntExtra("USER_ID", -1);
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        username = getIntent().getStringExtra("USERNAME");
        db = RepoDatabase.getInstance(MainActivity.this);

        // ShakeDetector initialization
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                // Toast.makeText(getApplicationContext(), String.valueOf(userId), Toast.LENGTH_LONG).show();
                if(includeWishItems) {
                    includeWishItems = false;
                    heartIcon.setImageResource(R.drawable.heart_icon);
                } else {
                    includeWishItems = true;
                    heartIcon.setImageResource(R.drawable.heart_f_icon);
                }
                double val = calculateBudgetValue();
                updateRemainingBudget(val);
            }
        });

        getTransactionsForDateAndUser();

        initializeElements();

        double val = calculateBudgetValue();
        updateRemainingBudget(val);

        accountIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenu(v);

            }
        });

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddExpenseActivity.class), ADD_EXPENSE_CODE);
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddIncomeActivity.class), ADD_INCOME_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        sensorManager.unregisterListener(shakeDetector);
        super.onPause();
    }

    private void initializeElements() {
        TextView welcomeMessage = findViewById(R.id.user_welcome);
        heartIcon = findViewById(R.id.heart_icon);
        accountIcon = findViewById(R.id.account_icon);
        monthView = findViewById(R.id.month);
        TextView monthForward = findViewById(R.id.month_forward);
        TextView monthPrevious = findViewById(R.id.month_prior);
        remainingBudget = findViewById(R.id.remaining_budget);
        expenseButton = findViewById(R.id.expense_button);
        incomeButton = findViewById(R.id.income_button);
        transactionListView = findViewById(R.id.transaction_list_view);
        welcomeMessage.setText("Hello, " + firstName );

        monthForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                monthView.setText(monthTypedFormatter.format(cal.getTime()));
                getTransactionsForDateAndUser();
                updateRemainingBudget(calculateBudgetValue());
                transactionListView.setAdapter(getListAdapter());
            }
        });

        monthPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                monthView.setText(monthTypedFormatter.format(cal.getTime()));
                getTransactionsForDateAndUser();
                updateRemainingBudget(calculateBudgetValue());
                transactionListView.setAdapter(getListAdapter());
            }
        });

        monthView.setText(monthTypedFormatter.format(cal.getTime()));

        transactionListView.setAdapter(getListAdapter());

        transactionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,
                        ModifyTransactionActivity.class);
                TextView viewById = view.findViewById(R.id.transaction_id_invisible);
                intent.putExtra("id", viewById.getText());
                startActivityForResult(intent, EDIT_TRANSACTION_CODE);
            }
        });
    }

    private ArrayAdapter<Transaction> getListAdapter() {
        return new ArrayAdapter<Transaction>(this, R.layout.list_transaction_item, R.id.transaction_list_item, transactions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final Transaction transaction = getItem(position);
                assert transaction != null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_transaction_item, parent, false);
                }
                LinearLayout background = convertView.findViewById(R.id.transaction_list_item);
                if (transaction.isWishItem()) {
                    background.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccentBlue));
                } else {
                    background.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                }
                TextView name = (TextView) convertView.findViewById(R.id.transaction_name);
                TextView amount = (TextView) convertView.findViewById(R.id.transaction_price);
                TextView id = (TextView) convertView.findViewById(R.id.transaction_id_invisible);
                name.setText(transaction.getTransactionTitle());
                id.setText(String.valueOf(transaction.getTransactionId()));
                double amt = transaction.getTransactionAmount();
                amount.setText(String.format("%.2f", amt));
                amount.setTextColor(amt < 0 ? ContextCompat.getColor(MainActivity.this, R.color.colorRed)
                        : ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
                return convertView;
            }
        };
    }

    private void getTransactionsForDateAndUser() {
        transactions.clear();
        transactions.addAll(db.getTransactionDao().findTransactionByUserAndDate(userId, String.format("%%%s", monthFormatter.format(cal.getTime()))));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == ADD_EXPENSE_CODE || requestCode == ADD_INCOME_CODE)
                && resultCode == RESULT_OK) {
            String name = data.getStringExtra("transaction_name");
            double amount = data.getDoubleExtra("transaction_amount", -1);
            String date = data.getStringExtra("transaction_date");
            boolean wishItem = data.getBooleanExtra("transaction_wish", false);

            assert name != null && amount != -1 && date != null;

            if (requestCode == ADD_EXPENSE_CODE ) {
                amount = amount * -1;
            }

            Snackbar snackbar;
            if (requestCode == ADD_EXPENSE_CODE ) {
                snackbar = Snackbar.make(findViewById(R.id.budget_box_background), "Adding Expense", Snackbar.LENGTH_SHORT);
            } else {
                snackbar = Snackbar.make(findViewById(R.id.budget_box_background), "Adding Income", Snackbar.LENGTH_SHORT);
            }
            snackbar.show();

            Transaction transaction = new Transaction(userId, name, amount, date, wishItem);

            db.getTransactionDao().insert(transaction);
            getTransactionsForDateAndUser();
            updateRemainingBudget(calculateBudgetValue());
            transactionListView.setAdapter(getListAdapter());
        } else if (requestCode == EDIT_TRANSACTION_CODE && resultCode == RESULT_OK) {
            boolean toDelete = data.getBooleanExtra("toDelete", false);

            int id = data.getIntExtra("transaction_id", -1);
            String name = data.getStringExtra("transaction_name");
            double amount = data.getDoubleExtra("transaction_amount", -1);
            String date = data.getStringExtra("transaction_date");
            boolean wishItem = data.getBooleanExtra("transaction_wish", false);
            assert name != null && amount != -1 && date != null;
            Transaction transaction = new Transaction(id, userId, name, amount, date, wishItem);

            if (toDelete) {
                Snackbar snackbar;
                snackbar = Snackbar.make(findViewById(R.id.budget_box_background), "Deleting Transaction", Snackbar.LENGTH_SHORT);
                snackbar.show();

                db.getTransactionDao().delete(transaction);
            } else {
                Snackbar snackbar;
                snackbar = Snackbar.make(findViewById(R.id.budget_box_background), "Updating Transaction", Snackbar.LENGTH_SHORT);
                snackbar.show();

                db.getTransactionDao().update(transaction);
            }
            getTransactionsForDateAndUser();
            updateRemainingBudget(calculateBudgetValue());
            transactionListView.setAdapter(getListAdapter());
        }
    }

    private Double calculateBudgetValue() {
        double val = 0.0;
        for (Transaction t: transactions) {
            if (!t.isWishItem || includeWishItems) {
                val += t.getTransactionAmount();
            }
        }
        return val;
    }

    private void updateRemainingBudget(Double amount) {
        if (amount >= 0) {
            remainingBudget.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccentGreen));
            remainingBudget.setText(String.format("$%.2f", amount));
        } else {
            remainingBudget.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccentRed));
            remainingBudget.setText(String.format("-$%.2f", -1 * amount));
        }
    }

    private void showLogoutDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();



    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.logout_menu);
        popup.show();
    }

    private void startEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("FIRST_NAME", firstName); // String
        intent.putExtra("LAST_NAME", lastName); // String
        intent.putExtra("USERNAME", username); // String
        intent.putExtra("USER_ID", userId); // Int
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu_edit_account:
                startEditActivity();
                return true;
            case R.id.logout_menu_logout:
                showLogoutDialog();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
