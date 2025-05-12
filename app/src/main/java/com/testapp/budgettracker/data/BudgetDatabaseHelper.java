package com.testapp.budgettracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.testapp.budgettracker.model.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budget_db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_TRANSACTIONS = "transactions";

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_TYPE = "type"; // "income" or "expense"
    private static final String KEY_DATE = "date";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT,
            Locale.getDefault());

    // Creating table query
    private static final String CREATE_TRANSACTIONS_TABLE =
            "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_AMOUNT + " REAL,"
                    + KEY_TYPE + " TEXT,"
                    + KEY_DATE + " TEXT" + ")";

    public BudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        // Creating tables again
        onCreate(db);
    }

    // Adding new transaction
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, transaction.getDescription());
        values.put(KEY_AMOUNT, transaction.getAmount());
        values.put(KEY_TYPE, transaction.getType());
        values.put(KEY_DATE, dateFormatter.format(transaction.getDate()));

        // Inserting Row
        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close(); // Closing database connection
        return id;
    }

    // Getting single transaction
    public Transaction getTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSACTIONS, new String[] { KEY_ID,
                        KEY_DESCRIPTION, KEY_AMOUNT, KEY_TYPE, KEY_DATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Transaction transaction = new Transaction();
        if (cursor != null && cursor.getCount() > 0) {
            try {
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setDescription(cursor.getString(1));
                transaction.setAmount(Double.parseDouble(cursor.getString(2)));
                transaction.setType(cursor.getString(3));
                transaction.setDate(dateFormatter.parse(cursor.getString(4)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        db.close();
        return transaction;
    }

    // Getting All Transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + KEY_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setDescription(cursor.getString(1));
                transaction.setAmount(Double.parseDouble(cursor.getString(2)));
                transaction.setType(cursor.getString(3));
                try {
                    transaction.setDate(dateFormatter.parse(cursor.getString(4)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // Adding transaction to list
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        // return transaction list
        return transactionList;
    }

    // Getting transactions count
    public int getTransactionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

    // Updating single transaction
    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, transaction.getDescription());
        values.put(KEY_AMOUNT, transaction.getAmount());
        values.put(KEY_TYPE, transaction.getType());
        values.put(KEY_DATE, dateFormatter.format(transaction.getDate()));

        // updating row
        return db.update(TABLE_TRANSACTIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(transaction.getId()) });
    }

    // Deleting single transaction
    public int deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
            int rowsDeleted = db.delete(TABLE_TRANSACTIONS, KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
            db.close();
            return rowsDeleted;

    }
}