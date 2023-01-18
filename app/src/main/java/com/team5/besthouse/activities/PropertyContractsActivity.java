package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.ChatAdapter;
import com.team5.besthouse.adapters.ContractAdapter;
import com.team5.besthouse.adapters.ContractReviewAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;

public class PropertyContractsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StoreService storeService;
    Context context;
    private ContractReviewAdapter adapter;
    View progressIndicator;

    ArrayList<Contract> list;
    User user;
    Property property;

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_contracts);

        Intent intent = getIntent();

        property = (Property) intent.getExtras().get("property");

        context = getApplicationContext();

        storeService = new StoreService(getApplicationContext());

        list = new ArrayList<>();

        recyclerView = findViewById(R.id.contract_list);

        progressIndicator = findViewById(R.id.account_progressBar);
        progressIndicator.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ContractReviewAdapter(context, list);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Gson gson = new Gson();
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class);

        progressIndicator.setVisibility(View.VISIBLE);
        database.collection(UnchangedValues.CONTRACTS_TABLE)
                .whereEqualTo(UnchangedValues.LANDLORD_EMAIL_COL, user.getEmail())
                .whereEqualTo(UnchangedValues.PROPERTY_ID_COL, property.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Contract contract = document.toObject(Contract.class);
                            list.add(contract);
                        }
                        adapter.notifyItemRangeChanged(0, list.size());
                    }
                    progressIndicator.setVisibility(View.GONE);
                });
    }

}