package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.MessageAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Chat;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.TextMessage;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private Chat chat;
    private Contract contract;
    private Property property;
    private ArrayList<TextMessage> messageList;

    private StoreService storeService;

    MessageAdapter messageAdapter;

    RecyclerView msgRecyclerView;

    Gson gson;
    User user;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        db = FirebaseFirestore.getInstance();
        storeService = new StoreService(getApplicationContext());

        gson = new Gson();
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class);

        Intent intent = getIntent();
        chat = (Chat) intent.getExtras().get("chat");
        property = (Property) intent.getExtras().get("property");
        contract = (Contract) intent.getExtras().get("contract");

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.md_theme_surfaceVariant));

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        this.setSupportActionBar(toolbar);
        setTitle(property.getPropertyName());

//        linearLayoutManager.setStackFromEnd(true);

        // Get RecyclerView object.
        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        // Create the initial data list.
        messageList = new ArrayList<TextMessage>();

        // Create the data adapter with above data list.
        messageAdapter = new MessageAdapter(messageList, user);

        // Set data adapter to RecyclerView.
        msgRecyclerView.setAdapter(messageAdapter);

        ((LinearLayoutManager) msgRecyclerView.getLayoutManager()).setStackFromEnd(true);

        final EditText msgInputText = findViewById(R.id.chat_input_msg);

        ImageView msgSendButton = findViewById(R.id.chat_send_msg);

        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = msgInputText.getText().toString();
                if(!TextUtils.isEmpty(content))
                {
                    // Add a new sent message to the list.
                    TextMessage message = new TextMessage(chat.getId(), user.getEmail(), content, Timestamp.now());

                    DocumentReference dr = db.collection("messages").document();

                    message.setId(dr.getId());

                    dr.set(message);

                    int newMsgPosition = messageList.size() - 1;

                    // Empty the input edit text box.
                    msgInputText.setText("");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection(UnchangedValues.MESSAGES_TABLE)
                .whereEqualTo("chatId", chat.getId())
                .orderBy("time")
                .addSnapshotListener(this, (documentSnapshot, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null) {
                        for (DocumentChange doc : documentSnapshot.getDocumentChanges()) {
                            TextMessage message = doc.getDocument().toObject(TextMessage.class);
                            Log.i("Message", message.toString());
                            messageList.add(message);
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                        }
                        if (messageList.size() > 0) {
                            msgRecyclerView.smoothScrollToPosition(messageList.size() - 1);
                        }
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.info:
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("property", property);
                //Already click reserve button for the chat to appear, disable the reserve button
                intent.putExtra("history", true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}