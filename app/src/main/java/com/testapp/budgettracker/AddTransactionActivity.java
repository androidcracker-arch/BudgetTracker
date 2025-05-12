package com.testapp.budgettracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.testapp.budgettracker.data.BudgetDatabaseHelper;
import com.testapp.budgettracker.model.Transaction;

import java.util.Date;

public class AddTransactionActivity extends AppCompatActivity {

    private TextView titleTextView;
    private EditText amountEditText;
    private EditText descriptionEditText;
    private Button saveButton;
    private String transactionType;
    private BudgetDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dbHelper = new BudgetDatabaseHelper(this);

        titleTextView = findViewById(R.id.text_add_transaction_title);
        amountEditText = findViewById(R.id.edit_text_amount);
        descriptionEditText = findViewById(R.id.edit_text_description);
        saveButton = findViewById(R.id.button_save_transaction);

        transactionType = getIntent().getStringExtra("type");
        if (transactionType != null) {
            titleTextView.setText("Add " + transactionType.substring(0, 1).toUpperCase() + transactionType.substring(1));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                if (amountStr.isEmpty()) {
                    Toast.makeText(AddTransactionActivity.this, "Please enter the amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    Transaction transaction = new Transaction(description, amount, transactionType, new Date());
                    long result = dbHelper.addTransaction(transaction);

                    if (result > 0) {
                        Toast.makeText(AddTransactionActivity.this, transactionType + " added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, "Failed to add " + transactionType, Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AddTransactionActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
