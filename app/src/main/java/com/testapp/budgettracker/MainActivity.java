package com.testapp.budgettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.budgettracker.adapter.ExpenseTrackerListAdapter;
import com.testapp.budgettracker.data.BudgetDatabaseHelper;
import com.testapp.budgettracker.model.Transaction;

import java.util.List;



public class MainActivity extends AppCompatActivity implements ExpenseTrackerListAdapter.OnItemDeleteListener {
    private static final int REQUEST_CODE_DELETE_ITEM = 0;
    private Button addIncomeButton,addExpenseButton;
    private RecyclerView expenseRecyclerView;
    private TextView totalIncomeTextView,noRecordsTextView,totalExpenseTextView,balanceTextView;
    List<Transaction> allTransactions;

    private BudgetDatabaseHelper dbHelper;
    private ExpenseTrackerListAdapter expenseTrackerListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        dbHelper = new BudgetDatabaseHelper(this);

        addIncomeButton = findViewById(R.id.button_add_income);
        addExpenseButton = findViewById(R.id.button_add_expense);
        expenseRecyclerView = findViewById(R.id.recycler_view_summary);
        totalIncomeTextView = findViewById(R.id.text_total_income);
        totalExpenseTextView = findViewById(R.id.text_total_expense);
        balanceTextView = findViewById(R.id.text_balance);
        noRecordsTextView = findViewById(R.id.headforlist);

        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                intent.putExtra("type", "income");
                startActivity(intent);
            }
        });

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                intent.putExtra("type", "expense");
                startActivity(intent);
            }
        });

        loadSummaryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummaryData(); // Reload data when the activity resumes
    }

    private void loadSummaryData() {
        allTransactions = dbHelper.getAllTransactions();
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction transaction : allTransactions) {
            if (transaction.getType().equals("income")) {
                totalIncome += transaction.getAmount();
            } else if (transaction.getType().equals("expense")) {
                totalExpense += transaction.getAmount();
            }
        }

        double balance = totalIncome - totalExpense;

        totalIncomeTextView.setText(String.format("Total Income: %.2f", totalIncome));
        totalExpenseTextView.setText(String.format("Total Expense: %.2f", totalExpense));
        balanceTextView.setText(String.format("Balance: %.2f", balance));


            expenseTrackerListAdapter = new ExpenseTrackerListAdapter(MainActivity.this,
                    allTransactions,this);

            expenseRecyclerView.setAdapter(expenseTrackerListAdapter);
            updateNoRecordsTextViewVisibility();

    }

    private void updateNoRecordsTextViewVisibility() {
        if (allTransactions.isEmpty()) {

             noRecordsTextView.setText("No Records Found");

        } else {
            allTransactions = dbHelper.getAllTransactions();
            double totalIncome = 0;
            double totalExpense = 0;

            for (Transaction transaction : allTransactions) {
                if (transaction.getType().equals("income")) {
                    totalIncome += transaction.getAmount();
                } else if (transaction.getType().equals("expense")) {
                    totalExpense += transaction.getAmount();
                }
            }

            double balance = totalIncome - totalExpense;

            totalIncomeTextView.setText(String.format("Total Income: %.2f", totalIncome));
            totalExpenseTextView.setText(String.format("Total Expense: %.2f", totalExpense));
            balanceTextView.setText(String.format("Balance: %.2f", balance));
            noRecordsTextView.setText("Income/Expense");
        }
    }

    @Override
    public void onItemDeleted(int position) {
        updateNoRecordsTextViewVisibility();
    }


}
