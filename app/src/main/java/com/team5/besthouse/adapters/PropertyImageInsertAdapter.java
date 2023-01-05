package com.team5.besthouse.adapters;


import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team5.besthouse.R;
import com.team5.besthouse.activities.AddPropertyActivity;
import com.team5.besthouse.interfaces.RecyclerViewInterface;

import java.util.ArrayList;

public class PropertyImageInsertAdapter extends RecyclerView.Adapter<PropertyImageInsertAdapter.PropertyImageHolder>{
    LayoutInflater mInflater;
    private ArrayList<Bitmap> imageList;
    private final RecyclerViewInterface recyclerViewInterface;

    public PropertyImageInsertAdapter(AddPropertyActivity context, ArrayList<Bitmap> imageList, RecyclerViewInterface recyclerViewInterface  ) {
        this.imageList = imageList;
        this.recyclerViewInterface = recyclerViewInterface;
        mInflater = LayoutInflater.from(context);
    }


    public boolean addNewItem(Bitmap newBitmap)
    {
        if(imageList.size() >= 4)
        {
            return false;
        }
        imageList.add(newBitmap);
        notifyItemInserted(imageList.size());
        return true;
    }

    public boolean replaceItem(Bitmap newItem, int pos)
    {
       if(pos > imageList.size() - 1 || pos < 0)
       {
           return false;
       }
        imageList.set(pos, newItem);
       return true;
    }

    @NonNull
    @Override
    public PropertyImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_property_image_add, parent, false);
        PropertyImageInsertAdapter.PropertyImageHolder holder = new PropertyImageInsertAdapter.PropertyImageHolder(view, this.recyclerViewInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyImageInsertAdapter.PropertyImageHolder holder, int position) {
       holder.imageView.setImageBitmap(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class PropertyImageHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        public PropertyImageHolder(@NonNull View itemView,  RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardViewImage);
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
