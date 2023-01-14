package com.team5.besthouse.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.team5.besthouse.R;
import com.team5.besthouse.databinding.ItemPropertyTypeSelectBinding;
import com.team5.besthouse.models.PropertyType;

import java.util.ArrayList;

public class PropertyTypeSelectAdapter extends ArrayAdapter<PropertyType> {

    public PropertyTypeSelectAdapter(Context context, ArrayList<PropertyType> pList)
    {
        super(context,0,pList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return generateView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return generateView(position, convertView, parent);
    }

    private View generateView(int position, View convertView, ViewGroup parent) {
        ItemPropertyTypeSelectBinding itemBinding = null;
        if(convertView == null)
        {
            itemBinding = ItemPropertyTypeSelectBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = itemBinding.getRoot();
        }

        PropertyType p = getItem(position);

        TextView type = convertView.findViewById(R.id.spinItemName);
        type.setText(p.toString());


        return convertView;
    }


}
