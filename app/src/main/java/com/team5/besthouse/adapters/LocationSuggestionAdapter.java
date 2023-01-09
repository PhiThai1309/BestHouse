package com.team5.besthouse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team5.besthouse.R;
import com.team5.besthouse.interfaces.RecyclerViewInterface;

import java.util.ArrayList;

public class LocationSuggestionAdapter extends RecyclerView.Adapter<LocationSuggestionAdapter.LocationSuggestionHolder> {

    Context context;
    ArrayList<String> locationTextList;
    private final RecyclerViewInterface recyclerViewInterface;

    public LocationSuggestionAdapter(Context context, ArrayList<String> locationTextList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.locationTextList = locationTextList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void addNewItem(String ntl)
    {
        for(String lt : locationTextList)
        {
            if(lt.compareTo(ntl) == 0)
            {
               return;
            }
        }
        locationTextList.add(ntl);
        notifyItemInserted(locationTextList.size());
    }

    public void clearList() {
        locationTextList.clear();
    }


    @NonNull
    @Override
    public LocationSuggestionAdapter.LocationSuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_location_suggestion, parent, false);
        LocationSuggestionAdapter.LocationSuggestionHolder holder = new LocationSuggestionAdapter.LocationSuggestionHolder(view, recyclerViewInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationSuggestionAdapter.LocationSuggestionHolder holder, int position) {
        holder.lsTextView.setText(locationTextList.get(position));
    }

    @Override
    public int getItemCount() {
        return locationTextList.size();
    }

    public static class LocationSuggestionHolder extends RecyclerView.ViewHolder {
        TextView lsTextView;
        public LocationSuggestionHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            lsTextView = itemView.findViewById(R.id.lsTextView);

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
