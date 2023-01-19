package com.team5.besthouse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.LandlordActivity;
import com.team5.besthouse.models.Property;

import java.util.List;

public class LandlordPropertyAdapter extends RecyclerView.Adapter<LandlordPropertyAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Property> propertyList;

    private String key = "";

    // Constructor
    public LandlordPropertyAdapter(LandlordActivity context, List<Property> tasks) {
        mInflater = LayoutInflater.from(context);
        propertyList = tasks;
    }

    // Create the view holder
    @NonNull
    @Override
    public LandlordPropertyAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.layout_card_feature, parent, false);
        return new LandlordPropertyAdapter.TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull LandlordPropertyAdapter.TaskViewHolder holder, int position) {
        //if task is not null
        if (propertyList != null) {
            // Get the task at the position
            Property current = propertyList.get(position);
            // Set the name of the view holder
            holder.name.setText(current.getId());
            // Set the address of the view holder
            holder.address.setText(current.getAddress(this.mInflater.getContext()).toString());
            //Set the prize of the view holder
            holder.price.setText("12");
        } else {
            // Covers the case of data not being ready yet.
            holder.name.setText("Error");
            holder.address.setText("Error");
            holder.price.setText(0);
        }
        // Set the click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //direct to edit property activity
            }
        });
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
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            price = itemView.findViewById(R.id.last_chat_time);
            address = itemView.findViewById(R.id.property_price);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
