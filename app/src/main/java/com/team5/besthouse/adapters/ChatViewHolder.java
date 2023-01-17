package com.team5.besthouse.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.team5.besthouse.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    LinearLayout leftMsgLayout;

    LinearLayout rightMsgLayout;

    TextView leftMsgTextView;

    TextView rightMsgTextView;

    public ChatViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_msg_layout);
            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
        }
    }
}