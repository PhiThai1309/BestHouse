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

import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.DetailActivity;
import com.team5.besthouse.activities.PropertyContractsActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;

public class PropertyCardAdapter extends RecyclerView.Adapter<PropertyCardAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private final ArrayList<Property> propertyList;
    private boolean toProperty = true;
    private int maxItemCount = 1000;

    StoreService storeService;

    private String key = "";

    // Constructor
    public PropertyCardAdapter(Context context, ArrayList<Property> tasks) {
        mInflater = LayoutInflater.from(context);
        propertyList = tasks;
    }

    public PropertyCardAdapter(Context context, ArrayList<Property> propertyList, int maxItemCount) {
        mInflater = LayoutInflater.from(context);
        this.propertyList = propertyList;
        this.maxItemCount = maxItemCount;
    }

    public PropertyCardAdapter(Context context, ArrayList<Property> propertyList, boolean toProperty) {
        mInflater = LayoutInflater.from(context);
        this.propertyList = propertyList;
        this.toProperty = toProperty;
    }

    // Create the view holder
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.layout_card_main, parent, false);
        return new TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //if task is not null
        if (propertyList != null) {
            // Get the task at the position
            Property current = propertyList.get(position);
            // Set the name of the view holder
            holder.name.setText(current.getPropertyName());
            // Set the address of the view holder
            holder.address.setText(current.getAddress(this.mInflater.getContext()).toString());
            //Set the prize of the view holder
            holder.price.setText(String.valueOf(current.getMonthlyPrice()));

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    if (toProperty) {
                        goToProperty(current);
                    } else {
                        goToContracts(current);
                    }
                }
            });
        } else {
            // Covers the case of data not being ready yet.
            holder.name.setText("Error");
            holder.address.setText("Error");
            holder.price.setText(0);
        }
        // Set the click listener
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                key = mTasks.get(position).getId();
//                updateTask();
//            }
//        });
    }

    private void goToContracts(Property current) {
        Intent intent = new Intent(mInflater.getContext(), PropertyContractsActivity.class);
        intent.putExtra("property", current);
        mInflater.getContext().startActivity(intent);
    }

    private void goToProperty(Property current) {
        Intent intent = new Intent(mInflater.getContext(), DetailActivity.class);

//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            MainActivity.this, imageView, ViewCompat.getTransitionName(imageView));
        intent.putExtra("property", current);

        // set up store service
        storeService = new StoreService(mInflater.getContext());

        Gson gson = new Gson();
        User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class);
        if(user.getRole() == UserRole.LANDLORD){
            intent.putExtra("history", true);
        }

        mInflater.getContext().startActivity(intent);
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        return Math.min(propertyList.size(), 5);
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
            price = itemView.findViewById(R.id.last_chat_time);
            address = itemView.findViewById(R.id.property_address);
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
