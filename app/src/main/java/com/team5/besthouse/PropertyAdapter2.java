package com.team5.besthouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team5.besthouse.activities.MainActivity;
import com.team5.besthouse.models.Property;

import java.util.List;

public class PropertyAdapter2 extends RecyclerView.Adapter<PropertyAdapter2.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Property> propertyList;

    private String key = "";

    // Constructor
    public PropertyAdapter2(MainActivity context, List<Property> tasks) {
        mInflater = LayoutInflater.from(context);
        propertyList = tasks;
    }

    // Create the view holder
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.main_layout, parent, false);
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
            holder.name.setText(current.getId());
            // Set the address of the view holder
            holder.address.setText(current.getAddress().toString());
            //Set the prize of the view holder
            holder.price.setText("12");
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

    // Return the size of the data set
    @Override
    public int getItemCount() {
        if (propertyList != null)
            return propertyList.size();
        else return 0;
    }

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView address;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            price = itemView.findViewById(R.id.property_price);
            address = itemView.findViewById(R.id.property_address);
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