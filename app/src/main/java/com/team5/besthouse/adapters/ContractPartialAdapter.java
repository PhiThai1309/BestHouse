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

import com.team5.besthouse.R;
import com.team5.besthouse.activities.ContractActivity;
import com.team5.besthouse.models.Contract;

import java.util.List;

public class ContractPartialAdapter extends RecyclerView.Adapter<ContractPartialAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Contract> contractList;

    private String key = "";

    // Constructor
    public ContractPartialAdapter(Context context, List<Contract> contracts) {
        mInflater = LayoutInflater.from(context);
        contractList = contracts;
    }

    // Create the view holder
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.layout_contract, parent, false);
        return new TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //if task is not null
        if (contractList != null) {
            // Get the task at the position
            Contract current = contractList.get(position);
            // Set the name of the view holder
            if(current.getContractStatus() == null) {
                holder.name.setText(" ");
            } else {
                holder.name.setText(current.getContractStatus().toString());
            }

            if(current.convertStartDay() == null) {
                holder.address.setText(" ");
            } else {
                // Set the address of the view holder
                holder.address.setText(current.convertStartDay().toString());
            }

            //Set the prize of the view holder
            holder.price.setText(current.getId());

            if(current.getContractStatus() != null && current.convertStartDay() != null) {
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
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.name.setText("Error");
            holder.address.setText("Error");
            holder.price.setText(0);
        }
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        return Math.min(contractList.size(), 5);
    }

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView address;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            price = itemView.findViewById(R.id.property_address);
            address = itemView.findViewById(R.id.property_price);
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
