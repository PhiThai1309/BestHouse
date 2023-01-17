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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.MessageActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Chat;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.TextMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Chat> ChatList;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

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

            database.collection(UnchangedValues.CONTRACTS_TABLE).document(current.getContractId()).get().addOnSuccessListener(documentSnapshot -> {
                Contract contract = documentSnapshot.toObject(Contract.class);
                if (contract != null) {
                    database.collection(UnchangedValues.PROPERTIES_TABLE).document(contract.getPropertyId()).get().addOnCompleteListener(task -> {
                        Property property = task.getResult().toObject(Property.class);
                        if (property != null){
                            holder.name.setText(property.getPropertyName());

                            // Set on click listener
                            holder.cardView.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mInflater.getContext(), MessageActivity.class);
                                    intent.putExtra("chat", current);
                                    intent.putExtra("property", property);
                                    intent.putExtra("contract", contract);
                                    mInflater.getContext().startActivity(intent);
                                }
                            });
                        }
                    });
                }
            });

            holder.lastMessage.setText("No Chats Yet");
            holder.lastChatTime.setText("No Chats Yet");

            database.collection(UnchangedValues.MESSAGES_TABLE)
                    .whereEqualTo("chatId", current.getId())
                    .orderBy("time")
                    .limitToLast(1).addSnapshotListener((value, error) -> {
                        if (value != null) {
                            for (DocumentSnapshot dc : value.getDocuments()) {
                                TextMessage message = dc.toObject(TextMessage.class);
                                if (message != null) {
                                    holder.lastMessage.setText(message.getContent());
                                    holder.lastChatTime.setText(message.getTime().toDate().toString());
                                }
                                else {
                                    // Covers the case of data not being ready yet.
                                    holder.lastMessage.setText("");
                                    holder.lastChatTime.setText("Error");
                                }
                            }
                        }
                    });
        } else {
            // Covers the case of data not being ready yet.
            holder.name.setText("Error");
            holder.lastMessage.setText("");
            holder.lastChatTime.setText("Error");
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

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        TextView lastChatTime;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            lastMessage = itemView.findViewById(R.id.property_price);
            lastChatTime = itemView.findViewById(R.id.last_chat_time);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
