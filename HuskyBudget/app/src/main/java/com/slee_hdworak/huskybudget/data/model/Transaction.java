package com.slee_hdworak.huskybudget.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")

public class Transaction {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    public int transactionId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "transaction_title")
    public String transactionTitle;

    @ColumnInfo(name = "transaction_amount")
    public double transactionAmount;

    @ColumnInfo(name = "transaction_date")
    public String transactionDate;

    @ColumnInfo(name = "is_wish_item")
    public boolean isWishItem;

    public Transaction(int userId, String transactionTitle,
                       double transactionAmount, String transactionDate, boolean isWishItem) {
        this.userId = userId;
        this.transactionTitle = transactionTitle;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
        this.isWishItem = isWishItem;
    }

    @Ignore
    public Transaction(int transactionId, int userId, String transactionTitle,
                       double transactionAmount, String transactionDate, boolean isWishItem) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionTitle = transactionTitle;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
        this.isWishItem = isWishItem;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTransactionTitle() {
        return transactionTitle;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public boolean isWishItem() {
        return isWishItem;
    }
}




