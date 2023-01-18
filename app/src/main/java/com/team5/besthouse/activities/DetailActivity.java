package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.SliderView;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.ImageSliderAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.MapsFragment;
import com.team5.besthouse.models.Chat;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.models.Utilities;
import com.team5.besthouse.services.StoreService;

import com.google.firebase.firestore.DocumentReference;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Landlord;

import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.w3c.dom.Text;

public class DetailActivity extends BaseActivity {
    private FirebaseFirestore db;
    private Property property;
    private StoreService storeService;
    private Landlord landlord;
    private SliderView sliderView;
    private List<Bitmap> sliderViewImageList;

    View progressIndicator;
    Gson gson;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();

        progressIndicator = findViewById(R.id.progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        // set up store service
        storeService = new StoreService(getApplicationContext());

        gson = new Gson();
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);

        Button makeContractButton = findViewById(R.id.createPropertyBtn);

        property = (Property) getIntent().getParcelableExtra("property");
        boolean disableReservation = getIntent().getBooleanExtra("history", false);
        if(disableReservation){
            makeContractButton.setEnabled(false);
        }

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

        sliderViewConfig();

        //Second feature
        LinearLayout bathroom = findViewById(R.id.details_bathroom);
        ImageView featureBathroom = bathroom.findViewById(R.id.feature_image);
        featureBathroom.setImageResource(R.drawable.ic_outline_bathtub_24);
        TextView bathroomText = bathroom.findViewById(R.id.feature_text);
        bathroomText.setText(property.getBathrooms() + " Bathrooms");

        //Last feature
        LinearLayout other = findViewById(R.id.details_other);
        ImageView featureOther = other.findViewById(R.id.feature_image);
        featureOther.setImageResource(R.drawable.ic_outline_done_outline_24);

        View returnView = findViewById(R.id.details_returnBar);
        ImageView backBtn = returnView.findViewById(R.id.returnButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView editBtn = returnView.findViewById(R.id.editButton);
        if(user.getRole() == UserRole.TENANT){
            editBtn.setVisibility(View.GONE);
        }
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getApplicationContext(), EditActivity.class);
                editIntent.putExtra("property", property);
                startActivity(editIntent);
            }
        });

        featureOther.setImageResource(R.drawable.ic_outline_square_foot_24);
        TextView otherText = other.findViewById(R.id.feature_text);
        otherText.setText((int) property.getArea() + " Square foot");

        TextView desc = findViewById(R.id.details_desc);
        desc.setText(property.getPropertyDescription());

        TextView nameText = findViewById(R.id.details_name);

        TextView locationText = findViewById(R.id.details_address);

        //End of id get

        nameText.setText(property.getPropertyName());

        String location = property.getAddress(getApplicationContext());
        locationText.setText(location);

        //button to ask landlord for a contract
        makeContractButton.setOnClickListener(v -> {
//            Property property = Property.STATICPROPERTY;
//            db.collection(UnchangedValues.PROPERTIES_TABLE).add(property);
//
//            Toast.makeText(this, "New property added!", Toast.LENGTH_SHORT).show();
            makeContract();
        });

//        Log.d(TAG, property.getLandlordEmail());
//        database1.collection(UnchangedValues.USERS_TABLE)
//                .whereEqualTo("email",true)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                User user = document.toObject(User.class);
//                                TextView landlordID = findViewById(R.id.details_lanlordID);
//                                if(user.getEmail() == property.getLandlordEmail()) {
//                                    landlordID.setText(user.getEmail());
//                                    Log.d("Hello", document.get("propertyName").toString());
////                                Log.d(TAG, user.getEmail());
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//
//                    }
//                });

//        TextView type = findViewById(R.id.property_type);
//        type.setText(property.getPropertyType().toString().toLowerCase(Locale.ROOT));
//
//        TextView utilities = findViewById(R.id.Utilities);
//        utilities.setText(property.getUtilities().toString().substring(1));

//        GridView grid = findViewById(R.id.home_grid_view);

//        List<Utilities> utilitiesList = new ArrayList<>();
//        for(int i = 0; i < property.getUtilities().size(); i++) {
//            utilitiesList.add(property.getUtilities().get(i));
//        }

//        final ArrayAdapter adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1, property.getUtilities());
//        GridViewCustomAdapter GridViewCustomAdapter = new GridViewCustomAdapter(this, property.getUtilities());
//        grid.setAdapter(GridViewCustomAdapter);

        if(property.getUtilities() != null) {
            for(Utilities utility : property.getUtilities()) {
                String ult = utility.toString().toLowerCase(Locale.ROOT);
                View ulView = findViewById(this.getResources().
                        getIdentifier(ult, "id", this.getPackageName()));
                TextView ulText = ulView.findViewById(R.id.grid_text);
                ulText.setText(utility.toString());
            }
        }
//
        for (Utilities dir : Utilities.values()) {
            String ult = dir.toString().toLowerCase(Locale.ROOT);
            View ulView = findViewById(this.getResources().
                    getIdentifier(ult, "id", this.getPackageName()));

            int drawable = getResources().getIdentifier(ult, "drawable", getPackageName());
            ImageView imageView = ulView.findViewById(R.id.grid_image);
            imageView.setImageResource(drawable);
        }

        fetchUser();

        TextView price = findViewById(R.id.details_price);
        price.setText((int) property.getMonthlyPrice() + ".000 VND / Month");
    }

    private void sliderViewConfig()
    {

        sliderView = findViewById(R.id.imageSlider);
        if(property.getImageURLList() != null)
        {
            
            ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, property.getImageURLList()) ;
            sliderView.setSliderAdapter(sliderAdapter);
            sliderView.startAutoCycle();
        }


    }

    public void makeContract() {
        //get final day to check for last day that the user can hire
        AtomicReference<Timestamp> finalDayToHire = new AtomicReference<>(new Timestamp(Date.from(Instant.now().plusSeconds(68400L * 30 * 12 * 100))));

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
        Timestamp endDate = new Timestamp(Date.from(Instant.now().plusSeconds(86400 * 30 * 12)));

        //12 month contract
        Contract contract = new Contract(ContractStatus.PENDING, property.getLandlordEmail(), user.getEmail(), property.getId(), Timestamp.now(), endDate);

        DocumentReference dc = db.collection(UnchangedValues.CONTRACTS_TABLE).document();

        contract.setId(dc.getId());

        Intent intent = new Intent(this, MapsFragment.class);

        dc.set(contract)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentReference dr =  db.collection("chats").document();
                        Chat chat = new Chat(contract.getId());
                        chat.setId(dr.getId());
                        dr.set(chat).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(this, "Contract created!", Toast.LENGTH_SHORT).show();
                                intent.putExtra("created", true);
                                setResult(200, intent);
                            }
                            else {
                                dc.delete();
                                Toast.makeText(this, "Contract creation failed!", Toast.LENGTH_SHORT).show();
                                intent.putExtra("created", false);
                            }
                            finish();
                        });
                    } else {
                        Toast.makeText(this, "Contract creation failed!", Toast.LENGTH_SHORT).show();
                        intent.putExtra("created", false);
                        finish();
                    }
                });
    }

    public void fetchUser() {
        db.collection(UnchangedValues.USERS_TABLE).whereEqualTo("email", property.getLandlordEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                        ArrayList<User> insList = new ArrayList<>();
                        for(DocumentSnapshot ds : docList)
                        {
                            if(ds.exists())
                            {
                                progressIndicator.setVisibility(View.GONE);
                                View userDetails = findViewById(R.id.details_user);
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

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}