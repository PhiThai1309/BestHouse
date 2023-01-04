package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Landlord;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyAddress;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

import java.sql.Time;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class DetailActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Property property;
    private StoreService storeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();

        // set up store service
        storeService = new StoreService(getApplicationContext());

        property = (Property) getIntent().getSerializableExtra("property");

        if (property == null) property = Property.STATICPROPERTY;

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        //first feature
        LinearLayout bedroom = findViewById(R.id.details_bedroom);
        ImageView featureBedroom = bedroom.findViewById(R.id.feature_image);
        featureBedroom.setImageResource(R.drawable.ic_outline_single_bed_24);
        TextView bedroomText = bedroom.findViewById(R.id.feature_text);
        bedroomText.setText(property.getBedrooms() + " Bedrooms");

        //Second feature
        LinearLayout bathroom = findViewById(R.id.details_bathroom);
        ImageView featureBathroom = bathroom.findViewById(R.id.feature_image);
        featureBathroom.setImageResource(R.drawable.ic_outline_bathtub_24);
        TextView bathroomText = bathroom.findViewById(R.id.feature_text);
        bathroomText.setText(property.getBathrooms() + " Bathrooms");

        //Last feature
        LinearLayout other = findViewById(R.id.details_other);
        ImageView featureOther = other.findViewById(R.id.feature_image);
        featureOther.setImageResource(R.drawable.ic_outline_square_foot_24);
        TextView otherText = other.findViewById(R.id.feature_text);
        otherText.setText((int) property.getArea() + "m^2 Square foot");

        TextView desc = findViewById(R.id.details_desc);

        Button makeContractButton = findViewById(R.id.createPropertyBtn);

        TextView nameText = findViewById(R.id.details_name);

        TextView locationText = findViewById(R.id.details_address);

        //End of id get

        nameText.setText(property.getPropertyName());

        PropertyAddress address = property.getAddress();
        String location = address.getStreet() + ", " + address.getCity() + ", " + address.getWard();
        locationText.setText(location);


        //button to ask landlord for a contract
        makeContractButton.setOnClickListener(v -> {
//            Property property = Property.STATICPROPERTY;
//            db.collection(UnchangedValues.PROPERTIES_TABLE).add(property);
//
//            Toast.makeText(this, "New property added!", Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);

            //get final day to check for last day that the user can hire
            AtomicReference<Timestamp> finalDayToHire = new AtomicReference<>(new Timestamp(Date.from(Instant.now().plusSeconds(68400L*30*12*100))));

            db.collection(UnchangedValues.CONTRACTS_TABLE)
                    .whereEqualTo("contractStatus", ContractStatus.ACTIVE)
                    .whereGreaterThan("startDate", new Time(Date.from(Instant.now()).getTime()))
                    .orderBy("startDate")
                    .limit(1)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                Contract contract = task.getResult().getDocuments().get(0).toObject(Contract.class);
                                assert contract != null;
                                finalDayToHire.set(contract.getStartDate());
                            }
                        }
                    });

            //crate timestamp that is 12 months from now
            Timestamp endDate = new Timestamp(Date.from(Instant.now().plusSeconds(86400*30*12)));

            //12 month contract
            Contract contract = new Contract(ContractStatus.PENDING, property.getLandlordEmail(),  user.getEmail(), property.getId(), Timestamp.now(), endDate);

            DocumentReference dc = db.collection(UnchangedValues.CONTRACTS_TABLE).document();

            contract.setId(dc.getId());


            dc.set(contract)
                    .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Contract created!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Contract creation failed!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
            });
        });

        TextView price = findViewById(R.id.details_price);
        price.setText((int) property.getMonthlyPrice() + ".000 VND / Month");

        ImageView backBtn = findViewById(R.id.details_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //code for landlords to get list of contracts
//    void getContracts(){
//        Gson gson = new Gson();
//        User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Landlord.class);
//
//        db.collection(UnchangedValues.CONTRACTS_TABLE)
//                .whereEqualTo("contractStatus", ContractStatus.PENDING)
//                .whereEqualTo("landlordEmail", user.getEmail())
//                .get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Contract contract = document.toObject(Contract.class);
//
//                           //add to list and notify adapter
//                        }
//                    }
//                });
//    }
}