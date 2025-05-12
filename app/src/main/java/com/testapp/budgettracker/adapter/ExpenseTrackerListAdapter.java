package com.testapp.budgettracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.budgettracker.R;
import com.testapp.budgettracker.data.BudgetDatabaseHelper;
import com.testapp.budgettracker.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseTrackerListAdapter extends RecyclerView.Adapter<ExpenseTrackerListAdapter.ViewHolder> {

private final OnItemDeleteListener deleteListener;
private List<Transaction> transactionList;
private Context context;
private BudgetDatabaseHelper dbHelper;
private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
public interface OnItemDeleteListener {
        void onItemDeleted(int position);
    }
public ExpenseTrackerListAdapter(Context context,List<Transaction> transactionList,OnItemDeleteListener listener) {
    this.context=context;
    this.transactionList = transactionList;
    this.deleteListener =listener;
}

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_summary, parent, false);
    return new ViewHolder(itemView);
}

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Transaction transaction = transactionList.get(position);
    dbHelper = new BudgetDatabaseHelper(context);
    holder.descriptionTextView.setText(transaction.getDescription());

    holder.amountTextView.setText(String.format("%.2f", transaction.getAmount()));
    holder.typeTextView.setText(transaction.getType().toUpperCase());
    holder.dateTextView.setText(dateFormatter.format(transaction.getDate()));

    if (transaction.getType().equals("income")) {
        holder.amountTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        holder.typeTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
    } else {
        holder.amountTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        holder.typeTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
    }

    holder.deleteButton.setOnClickListener(v -> {
        // Get the adapter position of the clicked item
        int adapterPosition = holder.getAdapterPosition();
        removeItem(adapterPosition);
        deleteListener.onItemDeleted(adapterPosition);
    });


}
    private void removeItem(int position) {
        if (transactionList.size()>0) {
            Transaction transactionToDelete = transactionList.get(position); // Get the transaction to delete *before* removing it from the list!
            transactionList.remove(position); // *Now* remove it from the list.
            notifyItemRemoved(position); // Use notifyItemRemoved!
            notifyItemRangeChanged(position, transactionList.size() - position); //keep the list consistent

            //check if the database is not empty
            if (dbHelper.getTransactionsCount() > 0) {
                int deletedRows = dbHelper.deleteTransaction(transactionToDelete.getId()); // Use the ID from the transaction we fetched.
                if (deletedRows > 0) {
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete item from database", Toast.LENGTH_SHORT).show(); //handle the error
                }
            }
        } else {
            Log.e("removeItem", "Invalid position: " + position + ", list size: " + transactionList.size());
            Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show(); //handle the error
        }


    }

@Override
public int getItemCount() {
    return transactionList.size();
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView descriptionTextView;
    public TextView amountTextView;
    public TextView typeTextView;
    public TextView dateTextView;
    public ImageButton deleteButton;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        descriptionTextView = itemView.findViewById(R.id.text_summary_description);
        amountTextView = itemView.findViewById(R.id.text_summary_amount);
        typeTextView = itemView.findViewById(R.id.text_summary_type);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        dateTextView = itemView.findViewById(R.id.text_summary_date);
    }
}
}