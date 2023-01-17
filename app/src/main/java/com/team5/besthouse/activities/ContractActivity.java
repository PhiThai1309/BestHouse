package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.Inflate.MoreLanlordFragment;
import com.team5.besthouse.fragments.Inflate.MorePropertyFragment;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContractActivity extends BaseActivity {
    private Contract contract;
    private View progressIndicator;
    private TextView propertyAddress, propertyPrice;
    TextView seeMore;

    FirebaseFirestore database;
    StoreService storeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);

        database = FirebaseFirestore.getInstance();
        // set up store service
        storeService = new StoreService(this);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        contract = (Contract) getIntent().getParcelableExtra("contract");

        Toolbar toolbar = findViewById(R.id.homeToolbar);
        this.setSupportActionBar(toolbar);

        TextView startDate = findViewById(R.id.contract_start_date);
        startDate.setText(contract.convertStartDay().toString());

        TextView endDate = findViewById(R.id.contract_end_date);
        endDate.setText(contract.convertEndDay().toString());

        TextView propertyName = findViewById(R.id.contract_property_name);
        propertyName.setText(contract.getPropertyId());

        propertyAddress = findViewById(R.id.contract_address_name);
        propertyPrice = findViewById(R.id.contract_price_detail);

        fetchUser();
        queryProperty();

        Gson gson = new Gson();
        User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);
        if(user.getRole() == UserRole.TENANT) {
            LinearLayout tenantDetails = findViewById(R.id.contract_tenant);
            tenantDetails.setVisibility(View.GONE);
        }
    }

    public void fetchUser() {
        progressIndicator = findViewById(R.id.progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        database.collection(UnchangedValues.USERS_TABLE).whereEqualTo("email", contract.getTenantEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                        ArrayList<User> insList = new ArrayList<>();
                        for(DocumentSnapshot ds : docList)
                        {
                            if(ds.exists())
                            {
                                View userDetails = findViewById(R.id.contract_tenant_details);
                                TextView landlordName = userDetails.findViewById(R.id.details_landlordName);
                                landlordName.setText(ds.getString("fullName"));
//                                   Log.d("TESSSSS", ds.getString("email"));

                                TextView landlordEmail = userDetails.findViewById(R.id.details_landlordEmail);
                                landlordEmail.setText(ds.getString("email"));

                                TextView landlordPhone = userDetails.findViewById(R.id.details_landlordPhone);
                                landlordPhone.setText(ds.getString("phoneNumber"));
                            }
                        }
                        // load data in to the spinner
                        Log.d("NewQuestFragment", insList.toString());
                        progressIndicator.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void queryProperty() {
        database.collection(UnchangedValues.PROPERTIES_TABLE).whereEqualTo("id", contract.getPropertyId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot ds : docList)
                        {
                            if(ds.exists())
                            {
                                Property property = ds.toObject(Property.class);

                                propertyAddress.setText(property.getAddress(getApplicationContext()).toString());
                                propertyPrice.setText(String.valueOf(property.getMonthlyPrice()) + " /Month");

                                View propertyDetails = findViewById(R.id.property_details);
                                seeMore = propertyDetails.findViewById(R.id.more);
                                seeMore.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ContractActivity.this, DetailActivity.class);
                                        intent.putExtra("property", (Parcelable) property);
                                        intent.putExtra("history", true);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }
                        // load data in to the spinner
//                        Log.d("NewQuestFragment", insList.toString());
//                        progressIndicator.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}