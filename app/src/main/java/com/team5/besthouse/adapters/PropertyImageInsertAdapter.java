package com.team5.besthouse.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team5.besthouse.R;
import com.team5.besthouse.interfaces.RecyclerViewInterface;

import java.util.ArrayList;

public class PropertyImageInsertAdapter extends RecyclerView.Adapter<PropertyImageInsertAdapter.PropertyImageHolder>{
    LayoutInflater mInflater;
    private ArrayList<Bitmap> imageList;
    private ArrayList<String> testList = new ArrayList<>();





    public PropertyImageInsertAdapter(Context context, ArrayList<Bitmap> imageList  ) {
        this.imageList = imageList;

        testList.add("Heello");
        testList.add("Hahah");

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
        PropertyImageInsertAdapter.PropertyImageHolder holder = new PropertyImageInsertAdapter.PropertyImageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyImageInsertAdapter.PropertyImageHolder holder, int position) {
       holder.textView.setText(testList.get(position));
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public static class PropertyImageHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView textView;
        public PropertyImageHolder(@NonNull View itemView) {
            super(itemView);


//            imageView = itemView.findViewById(R.id.cardViewImage);
            textView = itemView.findViewById(R.id.cardViewText);
//            itemView.setOnClickListener(v->{
//                if(recyclerViewInterface != null)
//                {
//                    int pos = getAdapterPosition();
//                    if(pos != RecyclerView.NO_POSITION)
//                    {
//                        recyclerViewInterface.onItemClick(pos);
//                    }
//                }
//            });

        }
    }
}
