package com.team5.besthouse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.DetailActivity;
import com.team5.besthouse.activities.PropertyContractsActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.interfaces.GetBitMapCallBack;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyDAO;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;

public class HomePropertyCardAdapter extends RecyclerView.Adapter<HomePropertyCardAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private final ArrayList<PropertyDAO> propertyDAOList;
    private boolean toProperty = true;
    private int maxItemCount = 1000;

    StoreService storeService;

    private String key = "";

    // Constructor
    public HomePropertyCardAdapter(Context context, ArrayList<PropertyDAO> tasks) {
        mInflater = LayoutInflater.from(context);
        propertyDAOList = tasks;
    }

    public HomePropertyCardAdapter(Context context, ArrayList<PropertyDAO> propertyDAOList, int maxItemCount) {
        mInflater = LayoutInflater.from(context);
        this.propertyDAOList = propertyDAOList;
        this.maxItemCount = maxItemCount;
    }

    public HomePropertyCardAdapter(Context context, ArrayList<PropertyDAO> propertyDAOList, boolean toPropertyDAO) {
        mInflater = LayoutInflater.from(context);
        this.propertyDAOList = propertyDAOList;
        this.toProperty = toPropertyDAO;
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
        if (propertyDAOList != null) {
            // Get the task at the position
            Property current = propertyDAOList.get(position).getProperty();
            // Set the name of the view holder
            holder.name.setText(current.getPropertyName());
            // Set the address of the view holder
            holder.address.setText(current.getAddress(this.mInflater.getContext()));
            //Set the prize of the view holder
            int numContracts = propertyDAOList.get(position).getNumOfContracts();
            String res = propertyDAOList.get(position).getNumOfContracts() + " pending contract" + (numContracts > 1 ? "s" : "");
            holder.numOfContracts.setText(res);

            loadImageFromFSUrl(current.getImageURLList().get(0), new GetBitMapCallBack() {
                @Override
                public void getBitMap(Bitmap bitmap) {
                    holder.imageView.setImageBitmap(bitmap);
                }
            }) ;


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
            holder.numOfContracts.setText(0);
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
        return Math.min(propertyDAOList.size(), maxItemCount);
    }

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView numOfContracts;
        TextView address;
        CardView cardView;
        ImageView imageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            numOfContracts = itemView.findViewById(R.id.last_chat_time);
            address = itemView.findViewById(R.id.property_address);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.property_image);
        }
    }
    private void loadImageFromFSUrl(String imageURL, final GetBitMapCallBack callBack)
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        try {
            StorageReference httpsReference = firebaseStorage.getReferenceFromUrl(imageURL);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    callBack.getBitMap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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