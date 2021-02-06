package com.slee_hdworak.huskybudget.data.daos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.slee_hdworak.huskybudget.data.model.Transaction;
import com.slee_hdworak.huskybudget.data.model.User;

@Database(entities = { User.class, Transaction.class },
        version = 1)
public abstract class RepoDatabase extends RoomDatabase {
    private static RepoDatabase instance;

    public static synchronized RepoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RepoDatabase.class, "HuskyBudget")
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract UserDao getUserDao();
    public abstract TransactionDao getTransactionDao();
}