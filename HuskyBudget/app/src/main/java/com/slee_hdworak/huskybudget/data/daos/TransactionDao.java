package com.slee_hdworak.huskybudget.data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.slee_hdworak.huskybudget.data.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions")
    List<Transaction> getTransactionsList();

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE user_id LIKE :uid")
    Transaction[] findTransactionsByUserId(int uid);

    @Query("SELECT * FROM transactions WHERE transaction_id LIKE :tid")
    Transaction findTransactionById(String tid);


    @Query("SELECT * FROM transactions WHERE user_id LIKE :uid AND transaction_date LIKE :monthAndYear ORDER BY transaction_date")
    List<Transaction> findTransactionByUserAndDate(int uid, String monthAndYear);
}
