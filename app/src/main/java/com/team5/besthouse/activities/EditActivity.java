package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.PropertyImageInsertAdapter;
import com.team5.besthouse.adapters.PropertyTypeSelectAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.LandLordMapsFragment;
import com.team5.besthouse.interfaces.SetReceiveImageURLCallBack;
import com.team5.besthouse.models.Landlord;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyStatus;
import com.team5.besthouse.models.PropertyType;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.Utilities;
import com.team5.besthouse.services.StoreService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditActivity extends BaseActivity {
    private FirebaseFirestore db;
    private Property property;
    private StoreService storeService;

    private EditText pAddressEditText;
    private TextInputEditText pnameEditText, priceEditText, pdescEditText;
    private EditText pBedRoomEditText, pBathRoomEditText, pAreaEditText;
    AutoCompleteTextView ptypeSpinner;
    private PropertyType selectPropertyType = PropertyType.HOUSE;

    private CheckBox checkBoxElectric, checkBoxWater, checkBoxInternet, checkBoxGas;
    Button submitButton, deleteBtn;
    private Landlord loginLandlord;
    private ProgressBar deleteProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        // set storage service
        storeService = new StoreService(getApplicationContext());
        Gson json = new Gson();
        loginLandlord= json.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Landlord.class );


        Toolbar toolbar = findViewById(R.id.homeToolbar);
        this.setSupportActionBar(toolbar);

        property = (Property) getIntent().getParcelableExtra("property");

        //Set hint for adding property name textbox
        View pname = findViewById(R.id.add_property_name);
        TextInputLayout pnameWrapper= pname.findViewById(R.id.textInput_wrapper);
        pnameEditText = pname.findViewById(R.id.box);
        pnameWrapper.setHint("Property Name:");
        pnameEditText.setText(property.getPropertyName());

        //Set hint for adding property type textbox
//        View ptype = findViewById(R.id.property_type);
        ptypeSpinner = findViewById(R.id.spinner);

        //Set hint for adding property address textbox
        View pAddress = findViewById(R.id.property_address);
        TextInputLayout pAddressWrapper = pAddress.findViewById(R.id.textInput_wrapper);
        pAddressEditText = (EditText) pAddress.findViewById(R.id.box);
        pAddressWrapper.setHint("Address:");
        pAddressEditText.setText(property.getAddress(this));

        View pBedRoomEditTextView = findViewById(R.id.bedroomQuantity);
        pBedRoomEditText = (EditText) pBedRoomEditTextView.findViewById(R.id.box);
        pBedRoomEditText.setText(String.valueOf(property.getBedrooms()));

        View pBathRoomEditTextView = findViewById(R.id.bathroomQuantity);
        pBathRoomEditText= (EditText) pBathRoomEditTextView.findViewById(R.id.box);
        pBathRoomEditText.setText(String.valueOf(property.getBathrooms()));

        pAreaEditText = findViewById(R.id.propertyArea).findViewById(R.id.box);
        pAreaEditText.setText(String.valueOf(property.getArea()));

        //Set hint for adding property price textbox
        View price = findViewById(R.id.property_price);
        TextInputLayout priceWrapper= price.findViewById(R.id.textInput_wrapper);
        priceEditText = price.findViewById(R.id.box);
        priceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceWrapper.setHint("Monthly Price:");
        priceEditText.setText(property.getMonthlyPrice() + "");

        //Set hint for add property description text box
        View description = findViewById(R.id.property_description);
        pdescEditText = description.findViewById(R.id.box);
        TextInputLayout desWrapper = findViewById(R.id.descInput_wrapper);
        desWrapper.setHint("Describe the Property in Detail");
        pdescEditText.setText(property.getPropertyDescription());

        //set the delete button
        View deleteBtnView = findViewById(R.id.delete_button);
         deleteProgress =deleteBtnView.findViewById(R.id.progress_bar);
         deleteBtn = deleteBtnView.findViewById(R.id.button);
        deleteBtn.setBackgroundColor(Color.TRANSPARENT);
        deleteBtn.setTextColor(getResources().getColor(R.color.md_theme_error));
        deleteBtn.setText("DELETE");
        // set the submit button
        View submitButtonHolder = findViewById(R.id.progress_button);
        submitButton = submitButtonHolder.findViewById(R.id.button);
        submitButton.setText("CONFIRM EDIT");

        // set checkbox config
        checkBoxElectric = findViewById(R.id.electric_option_checkbox);
        checkBoxWater = findViewById(R.id.water_option_checkbox);
        checkBoxInternet = findViewById(R.id.internet_option_checkbox);
        checkBoxGas = findViewById(R.id.gas_option_checkbox);


        initUtilities();
        setAddAddressAction();
        initializeSpinner();
        setSpinSelectAction();
        setSubmitButton();
        setDeleteBtn();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeSpinner()
    {
        ArrayList<PropertyType> pList = new ArrayList<>();
        pList.add(PropertyType.APARTMENT);
        pList.add(PropertyType.HOUSE);
        pList.add(PropertyType.FLOOR);
        pList.add(PropertyType.ROOM);
        ptypeSpinner.setAdapter(new PropertyTypeSelectAdapter(this, pList));
        ptypeSpinner.setText(ptypeSpinner.getAdapter().getItem(pList.indexOf(property.getPropertyType())).toString(), false);
    }

    private void setSpinSelectAction()
    {
        ptypeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPropertyType = (PropertyType) parent.getItemAtPosition(position);
            }
        });
    }

    private void setAddAddressAction()
    {
        pAddressEditText.setOnClickListener(v ->{
            Intent i = new Intent(getApplicationContext(), LandLordMapsFragment.class);
            i.putExtra(UnchangedValues.ACTIVITY_REQUEST_CODE, 100);
            startActivityForResult(i, 100);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK)
        {
            if(data.getExtras().get(UnchangedValues.LOCATION_ADDRESS) != null)
            {
                String pAddress = data.getExtras().get(UnchangedValues.LOCATION_ADDRESS).toString();
                pAddressEditText.setText(pAddress);
            }
        }
    }

    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void setSubmitButton()
    {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInputs()) {
                    return;
                }
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                try {
                    DocumentReference dc = firestore.collection(UnchangedValues.PROPERTY_TABLE).document(property.getId());
                    dc.set(Objects.requireNonNull(createNewProperty()))
                            .addOnSuccessListener(
                                    documentReference -> {
                                        // display successful message
                                        showTextLong("Property change successfully");
                                        finish();
                                    }
                            )
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showTextLong(e.getMessage());
                                }
                            });
                }
                catch (Exception e)
                {
                    showTextLong(e.getMessage());
                }
                createNewProperty();
            }
        });
    }

    private void setDeleteBtn()
    {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmation();
            }
        });
    }

    //override on back pressed with a dialog if the user click delete button
    public void deleteConfirmation() {
        //Create a dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Delete confirmation");
        builder.setMessage("Do you want to delete this property?");
        //Set the positive button
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performDeleteOnProperty();
            }
        });
        //Set the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void performDeleteOnProperty()
    {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        CollectionReference pCollect = fs.collection(UnchangedValues.PROPERTIES_TABLE);
        deleteProgress.setVisibility(View.VISIBLE);
//        for(int i = 0; i < property.getImageURLList().size(); i ++)
//        {
//            deletePropertyImages(property.getImageURLList().get(i));
//        }
        performDeleteOnContracts(new DeleteCollectionCallBack() {
            @Override
            public void callback() {
                pCollect.document(property.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                            Intent intent = new Intent(getApplicationContext(), LandlordActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            showTextLong("Property is deleted successfully.");
                            deleteProgress.setVisibility(View.GONE);
                            startActivity(intent);
                            finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteProgress.setVisibility(View.GONE);
                        showTextLong("Error. Can't delete the property.");
                    }
                });
            }
        });
    }

    private void deletePropertyImages(String url)
    {
        try {
            FirebaseStorage fstorage = FirebaseStorage.getInstance();
            StorageReference storageReference = fstorage.getReferenceFromUrl(url);
            storageReference.delete();
        } catch (Exception e) {
        }
    }

    private void performDeleteOnContracts(final DeleteCollectionCallBack dcallBack)
    {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        CollectionReference pCollect = fs.collection(UnchangedValues.CONTRACTS_TABLE);


        pCollect.whereEqualTo(UnchangedValues.PROPERTY_ID_COL, property.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               for(DocumentSnapshot ds: queryDocumentSnapshots.getDocuments())
               {
                   performDeleteOnChat(ds.getId(), new DeleteCollectionCallBack() {
                       @Override
                       public void callback() {
                           ds.getReference().delete();
                       }
                   });
               }
               dcallBack.callback();
            }
        });
    }

    private void performDeleteOnChat(String contractId, final DeleteCollectionCallBack dcallback)
    {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        CollectionReference chatCollect = fs.collection(UnchangedValues.CHATS_TABLE);

        chatCollect.whereEqualTo(UnchangedValues.CONTRACTS_ID_COL, contractId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    performDeleteOnMessage(documentSnapshot.getId(), new DeleteCollectionCallBack() {
                        @Override
                        public void callback() {
                            documentSnapshot.getReference().delete();
                        }
                    });
                }
                dcallback.callback();
            }
        });
    }

    private void performDeleteOnMessage(String messageId, final DeleteCollectionCallBack dcallback)
    {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        CollectionReference messageCollect = fs.collection(UnchangedValues.MESSAGES_TABLE);

        messageCollect.whereEqualTo(UnchangedValues.MESSAGE_CHAT_ID_COL, messageId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    documentSnapshot.getReference().delete();
                }
                dcallback.callback();
            }
        });

    }

    private List<Utilities> getSelectUtilities()
    {
        List<Utilities> utilities = new ArrayList<>();
        if(checkBoxElectric.isChecked())
        {
            utilities.add(Utilities.ELECTRIC) ;
        }
        if(checkBoxWater.isChecked())
        {
            utilities.add(Utilities.WATER);
        }
        if(checkBoxInternet.isChecked())
        {
            utilities.add(Utilities.INTERNET);
        }
        if(checkBoxGas.isChecked())
        {
            utilities.add(Utilities.GAS);
        }
        return utilities;
    }

    private void initUtilities()
    {
        List<Utilities> utilities = property.getUtilities();
        for(Utilities ulti : utilities) {
            if(ulti == Utilities.ELECTRIC){
                checkBoxElectric.setChecked(true);
            } else if (ulti == Utilities.GAS){
                checkBoxGas.setChecked(true);
            } else if (ulti == Utilities.INTERNET){
                checkBoxInternet.setChecked(true);
            } else if (ulti == Utilities.WATER){
                checkBoxWater.setChecked(true);
            }
        }
    }

    private Property createNewProperty()
    {
        Geocoder geocoder = new Geocoder(this) ;
        try {
            List<Address> alist = geocoder.getFromLocationName(pAddressEditText.getText().toString(), 1);
//            List<Utilities> listU = new ArrayList<>();
//            listU.add(Utilities.ELECTRIC);
//            listU.add(Utilities.INTERNET);
            LatLng coord = new LatLng(alist.get(0).getLatitude(), alist.get(0).getLongitude());

            Property editProperty = new Property(property.getId(), pnameEditText.getText().toString(),loginLandlord.getEmail()
                    , coord, selectPropertyType,  Integer.parseInt(pBedRoomEditText.getText().toString()),
                    Integer.parseInt(pBathRoomEditText.getText().toString()), getSelectUtilities(),
                    Float.parseFloat(priceEditText.getText().toString()), Float.parseFloat(pAreaEditText.getText().toString()));
            editProperty.setPropertyDescription(pdescEditText.getText().toString());
            editProperty.setImageURLList(property.getImageURLList());
            editProperty.setStatus(property.getStatus());
            return editProperty;
        } catch (IOException e) {
            Log.e("ERROR", "createNewProperty: " + e.getMessage() );
        }

        return null;
    }

    private boolean validateInputs()
    {
        if(Objects.requireNonNull(pnameEditText.getText()).toString().trim().isEmpty())
        {
            showTextLong("Please Enter a valid property Name");
            return false;
        }
        else if(selectPropertyType == null)
        {
            showTextLong("Please Select the property type");
            return false;
        }
        else if (pAddressEditText.getText().toString().trim().isEmpty())
        {
            showTextLong("Please Select property address");
        }
        else if(getSelectUtilities().isEmpty())
        {
            showTextLong("Please select property's utilities");
            return false;
        }
        else if(pBedRoomEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid bedroom number");
            return false;
        }
        else if(pBathRoomEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid bathroom number");
            return false;
        }
        else if(pAreaEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid property area");
        }
        else if(Objects.requireNonNull(priceEditText.getText()).toString().isEmpty())
        {
            showTextLong("Please enter valid property price");
            return false;
        }
        else if(Objects.requireNonNull(pdescEditText.getText()).toString().isEmpty())
        {
            showTextLong("Please enter property description");
            return false;
        }

        else
        {
            return true;
        }
        return false;
    }

    interface DeleteCollectionCallBack
    {
        void callback( );
    }
}