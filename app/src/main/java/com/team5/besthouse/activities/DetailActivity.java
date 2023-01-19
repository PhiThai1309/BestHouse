package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.SliderView;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.ImageSliderAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.MapsFragment;
import com.team5.besthouse.models.Chat;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyStatus;
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

    boolean disableEdit;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarCL;
    private Button makeContractButton;
    private TextView type;
    private LinearLayout bedroom;
    private ImageView featureBedroom;
    private TextView bedroomText;
    private LinearLayout bathroom;
    private ImageView featureBathroom;
    private TextView bathroomText;
    private LinearLayout other;
    private ImageView featureOther;
    private TextView otherText;
    private TextView desc;
    private TextView locationText;
    private TextView price;

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

        makeContractButton = findViewById(R.id.createPropertyBtn);

        property = (Property) getIntent().getParcelableExtra("property");
        boolean disableReservation = getIntent().getBooleanExtra("history", false);
        if(disableReservation){
            makeContractButton.setEnabled(false);
        }

        disableEdit = getIntent().getBooleanExtra("chat", false);

        if (property == null) property = Property.STATICPROPERTY;

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        toolbar = findViewById(R.id.homeToolbar);
        this.setSupportActionBar(toolbar);

        retrieveDataFromUI();
        displayPropertyInfo(property);
        fetchUser();
        listenToCollection(property.getId());


    }

    private void retrieveDataFromUI()
    {
        type = findViewById(R.id.details_type);
        bedroom = findViewById(R.id.details_bedroom);
        featureBedroom = bedroom.findViewById(R.id.feature_image);
        bedroomText = bedroom.findViewById(R.id.feature_text);
        bathroom = findViewById(R.id.details_bathroom);
        featureBathroom = bathroom.findViewById(R.id.feature_image);
        bathroomText = bathroom.findViewById(R.id.feature_text);
        other = findViewById(R.id.details_other);
        featureOther = other.findViewById(R.id.feature_image);
        otherText = other.findViewById(R.id.feature_text);
        desc = findViewById(R.id.details_desc);
        locationText = findViewById(R.id.details_address);
        price = findViewById(R.id.details_price);
        toolbarCL = findViewById(R.id.toolbar_layout);
    }
    private void displayPropertyInfo(@NonNull Property property)
    {
        type.setText(String.valueOf(property.getPropertyType()));
        //first feature
        featureBedroom.setImageResource(R.drawable.ic_outline_single_bed_24);
        bedroomText.setText(property.getBedrooms() + " Bedrooms");
        sliderViewConfig();

        //Second feature
        featureBathroom.setImageResource(R.drawable.ic_outline_bathtub_24);
        bathroomText.setText(property.getBathrooms() + " Bathrooms");
        //Last feature


        featureOther.setImageResource(R.drawable.ic_outline_done_outline_24);
        featureOther.setImageResource(R.drawable.ic_outline_square_foot_24);
        otherText.setText((int) property.getArea() + " Square foot");
        desc.setText(property.getPropertyDescription());
        //End of id get

        toolbar.setTitle(property.getPropertyName());
        toolbarCL.setTitle(property.getPropertyName());
        String location = property.getAddress(getApplicationContext());
        locationText.setText(location);

        //button to ask landlord for a contract
        makeContractButton.setOnClickListener(v -> {
            //Create a dialog
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Confirm your reservation");
            builder.setMessage("If you confirm, this request will be sent to the landlord for further reviewed and the outcome will be sent to you later.");
            //Set the positive button
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    makeContract();
                }
            });
            //Set the negative button
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        });

        if(property.getUtilities() != null) {
            for(Utilities utility : property.getUtilities()) {
                String ult = utility.toString().toLowerCase(Locale.ROOT);
                View ulView = findViewById(this.getResources().
                        getIdentifier(ult, "id", this.getPackageName()));
                TextView ulText = ulView.findViewById(R.id.grid_text);
                ulText.setText(utility.toString());
            }
        }

        for (Utilities dir : Utilities.values()) {
            String ult = dir.toString().toLowerCase(Locale.ROOT);
            View ulView = findViewById(this.getResources().
                    getIdentifier(ult, "id", this.getPackageName()));

            int drawable = getResources().getIdentifier(ult, "drawable", getPackageName());
            ImageView imageView = ulView.findViewById(R.id.grid_image);
            imageView.setImageResource(drawable);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(user.getRole() != UserRole.TENANT && !disableEdit){
            getMenuInflater().inflate(R.menu.edit_app_bar, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                Intent editIntent = new Intent(getApplicationContext(), EditActivity.class);
                editIntent.putExtra("property", property);
                startActivity(editIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * learned from https://firebase.google.com/docs/firestore/query-data/listen
     */
    private void listenToCollection(String propertyId) {
        FirebaseFirestore database =  FirebaseFirestore.getInstance();

        database.collection(UnchangedValues.PROPERTIES_TABLE).document(propertyId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) return;

                if(value != null && value.exists() && value.getId().compareTo(propertyId) == 0 )
                {

                    try {
                        Property propertyChange = value.toObject(Property.class);
                        property = propertyChange;
                        displayPropertyInfo(propertyChange);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}