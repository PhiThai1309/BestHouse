package com.team5.besthouse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.ContractActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;

import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Contract> contractList;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    private String key = "";
    private int maxItemCount = 1000;

    // Constructor
    public ContractAdapter(Context context, List<Contract> contracts, int maxItemCount) {
        mInflater = LayoutInflater.from(context);
        contractList = contracts;
        this.maxItemCount = maxItemCount;
    }

    public ContractAdapter(Context context, List<Contract> contracts) {
        mInflater = LayoutInflater.from(context);
        contractList = contracts;
    }

    // Create the view holder
    @NonNull
    @Override
    public ContractAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.layout_contract, parent, false);
        return new ContractAdapter.TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull ContractAdapter.TaskViewHolder holder, int position) {
        //if task is not null
        if (contractList != null) {
            // Get the task at the position
            Contract current = contractList.get(position);

            // Set the status of the view holder
            holder.status.setText(current.getContractStatus().toString());

            database.collection(UnchangedValues.PROPERTIES_TABLE).document(current.getPropertyId()).get().addOnSuccessListener(documentSnapshot -> {
                holder.name.setText(documentSnapshot.getString(UnchangedValues.PROPERTY_NAME_COL));
            });

            //Set the prize of the view holder
            String res = current.getFormattedStartDate() + " to " + current.getFormattedEndDate();
            holder.date.setText(res);

            // Set the click listener
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mInflater.getContext(), ContractActivity.class);
                    intent.putExtra("contract", current);
                    mInflater.getContext().startActivity(intent);
                    notifyDataSetChanged();
                }
            });
        } else {
            // Covers the case of data not being ready yet.
            holder.status.setText("Error");
            holder.name.setText("Error");
            holder.date.setText(0);
        }
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        return Math.min(contractList.size(), maxItemCount);
    }

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView status;
        TextView date;
        TextView name;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.property_name);
            name = itemView.findViewById(R.id.property_price);
//            endDate = itemView.findViewById(R.id.end_date);
            date = itemView.findViewById(R.id.property_address);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

//    // Create an intent to update the task
//    public void updateTask() {
//        Intent intent = new Intent(mInflater.getContext(), DetailsActivity.class);
//        intent.putExtra("key", key);
//        mInflater.getContext().startActivity(intent);
//        notifyDataSetChanged();
//    }
}
