package com.team5.besthouse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.team5.besthouse.R;
import com.team5.besthouse.interfaces.RecyclerViewInterface;
import com.team5.besthouse.models.Coordinates;
import com.team5.besthouse.models.Property;

import java.util.ArrayList;
import java.util.Collections;

public class PropertiesSuggestionAdapter extends RecyclerView.Adapter<PropertiesSuggestionAdapter.PropertiesSuggestionHolder> {

    Context context;
    ArrayList<Property> properties;
    LatLng currentLocation;
    private final RecyclerViewInterface recyclerViewInterface;

    public PropertiesSuggestionAdapter(Context context, ArrayList<Property> properties, RecyclerViewInterface recyclerViewInterface, LatLng currentLocation) {
        this.context = context;
        this.properties = properties;
        this.recyclerViewInterface = recyclerViewInterface;
        this.currentLocation = currentLocation;
    }

    public void addNewItem(Property ntl)
    {
        for(Property lt : properties)
        {
            if(lt.equals(ntl))
            {
                return;
            }
        }
        properties.add(ntl);
        notifyItemInserted(properties.size());
    }

    public void sortProperties(LatLng currentLocation){
        this.currentLocation = currentLocation;
        properties.sort((t1, t2) -> {
            double distance1 = t1.getNonSqrtDistance(currentLocation.latitude, currentLocation.longitude);
            double distance2 = t2.getNonSqrtDistance(currentLocation.latitude, currentLocation.longitude);
            return (int) Double.compare(distance1, distance2);
        });
        notifyDataSetChanged();
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void clearList() {
        properties.clear();
    }

    @NonNull
    @Override
    public PropertiesSuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_location_suggestion, parent, false);
        PropertiesSuggestionAdapter.PropertiesSuggestionHolder holder = new PropertiesSuggestionAdapter.PropertiesSuggestionHolder(view, recyclerViewInterface);
        return holder;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull PropertiesSuggestionAdapter.PropertiesSuggestionHolder holder, int position) {
        holder.lsTextView.setText(properties.get(position).getAddress(context));
        holder.lsName.setText(properties.get(position).getPropertyName());
        double disLat = Math.abs(properties.get(position).getLatitude() - currentLocation.latitude);
        double disLng = Math.abs(properties.get(position).getLongitude() - currentLocation.longitude);
        double distance = disLat*110.574;
        distance += Math.abs(disLng*111.320*Math.cos(currentLocation.latitude));
        if (distance >= 1) {
            holder.lsDistanceTextView.setText(String.format("%.1f", distance) + " km");
        }
        else {
            holder.lsDistanceTextView.setText(String.format("%.0f", distance*1000) + " m");
        }

    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class PropertiesSuggestionHolder extends RecyclerView.ViewHolder {
        TextView lsName;
        TextView lsTextView;
        TextView lsDistanceTextView;
        public PropertiesSuggestionHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            lsName = itemView.findViewById(R.id.lsNameTextView);
            lsTextView = itemView.findViewById(R.id.lsTextView);
            lsDistanceTextView = itemView.findViewById(R.id.distance_num);

            itemView.setOnClickListener(v->{
                if(recyclerViewInterface != null)
                {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });

        }
    }
}
