package com.team5.besthouse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.team5.besthouse.R;
import com.team5.besthouse.models.TextMessage;
import com.team5.besthouse.models.User;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private ArrayList<TextMessage> textMessageList = null;
    private User user;

    public MessageAdapter(ArrayList<TextMessage> textMessageList, User user) {
        this.textMessageList = textMessageList;
        this.user = user;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        TextMessage message = this.textMessageList.get(position);
        // If the message is a received message.
        if(!message.isSender(user)){
            // Show received message in left linearlayout.
            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.leftMsgTextView.setText(message.getContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        // If the message is a sent message.
        else{
            // Show sent message in right linearlayout.
            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.rightMsgTextView.setText(message.getContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
        }
    }



    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chat_bubble_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(textMessageList==null)
        {
            textMessageList = new ArrayList<TextMessage>();
        }
        return textMessageList.size();
    }

}
