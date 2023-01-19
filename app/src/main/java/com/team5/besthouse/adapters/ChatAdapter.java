package com.team5.besthouse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.MessageActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.interfaces.GetBitMapCallBack;
import com.team5.besthouse.models.Chat;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.TextMessage;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Chat> ChatList;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private StoreService storeService;
    private User user;

    // Constructor
    public ChatAdapter(Context context, List<Chat> Chats) {
        mInflater = LayoutInflater.from(context);
        ChatList = Chats;
    }

    // Create the view holder
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.layout_card_main_chat, parent, false);
        storeService = new StoreService(parent.getContext());
        Gson gson = new Gson();
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class);
        return new TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //if task is not null
        if (ChatList != null) {
            // Get the task at the position
            Chat current = ChatList.get(position);
            // Set the name of the view holder

//            holder.name.setText(property.getPropertyName());

            database.collection(UnchangedValues.CONTRACTS_TABLE).document(current.getContractId()).get().addOnSuccessListener(documentSnapshot -> {
                Contract contract = documentSnapshot.toObject(Contract.class);

                Intent intent = new Intent(mInflater.getContext(), MessageActivity.class);

                if (contract == null) {
                    database.collection(UnchangedValues.CHATS_TABLE).document(current.getId()).delete();
                    return;
                }

                database.collection(UnchangedValues.USERS_TABLE)
                        .whereEqualTo(UnchangedValues.USER_EMAIL_COL, user.getRole() == UserRole.LANDLORD ? contract.getTenantEmail() : contract.getLandlordEmail())
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    User user = document.toObject(User.class);
                                    if (user == null) return;


                                    intent.putExtra("name", user.getFullName());
                                }
                            }
                        });


                database.collection(UnchangedValues.PROPERTIES_TABLE).document(contract.getPropertyId()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Property property = task.getResult().toObject(Property.class);
                            if (property != null) {
                                loadImageFromFSUrl(property.getImageURLList().get(0), new GetBitMapCallBack() {
                                    @Override
                                    public void getBitMap(Bitmap bitmap) {
                                        holder.imageView.setImageBitmap(bitmap);
                                    }
                                });
                                holder.name.setText(property.getPropertyName());
                                // Set on click listener
                                holder.cardView.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onClick(View v) {
                                        intent.putExtra("chat", current);
                                        intent.putExtra("property", property);
                                        intent.putExtra("contract", contract);
                                        mInflater.getContext().startActivity(intent);
                                    }
                                });
                            }
                        }
                        else{
                            database.collection(UnchangedValues.CONTRACTS_TABLE).document(current.getContractId()).delete();
                        }
                });
            });

            holder.lastMessage.setText("");
            holder.lastChatTime.setText("");

            database.collection(UnchangedValues.MESSAGES_TABLE)
                    .whereEqualTo("chatId", current.getId())
                    .orderBy("time")
                    .limitToLast(1).addSnapshotListener((value, error) -> {
                        if (value != null) {
                            for (DocumentSnapshot dc : value.getDocuments()) {
                                TextMessage message = dc.toObject(TextMessage.class);
                                if (message != null) {
                                    holder.lastMessage.setText(message.getContent());
                                    //show time if it is today, else show date
                                    if (message.getTime().toDate().getDate() == java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)) {
                                        holder.lastChatTime.setText(message.getFormattedTime());
                                    } else {
                                        holder.lastChatTime.setText(message.getFormattedDate());
                                    }

                                }
                                else {
                                    // Covers the case of data not being ready yet.
                                    holder.lastMessage.setText("");
                                    holder.lastChatTime.setText("");
                                }
                            }
                        }
                    });
        } else {
            // Covers the case of data not being ready yet.
            holder.name.setText("Error");
            holder.lastMessage.setText("");
            holder.lastChatTime.setText("");
        }
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        if(ChatList.size() == 0){
            return 0;
        }
        return ChatList.size();
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

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        TextView lastChatTime;
        LinearLayout cardView;
        ShapeableImageView imageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            lastMessage = itemView.findViewById(R.id.property_price);
            lastChatTime = itemView.findViewById(R.id.last_chat_time);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.image_shape_contract);
        }
    }
}
