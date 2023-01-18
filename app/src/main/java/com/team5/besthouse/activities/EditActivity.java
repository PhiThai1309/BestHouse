package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

public class EditActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Property property;
    private StoreService storeService;

    private EditText pAddressEditText;
    private TextInputEditText pnameEditText, priceEditText, pdescEditText;
    private EditText pBedRoomEditText, pBathRoomEditText, pAreaEditText;
    private Spinner ptypeSpinner;

    View progressIndicator;
    Gson gson;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

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
        View ptype = findViewById(R.id.property_type);
        ptypeSpinner = (Spinner) ptype.findViewById(R.id.box);

        //Set hint for adding property address textbox
        View pAddress = findViewById(R.id.property_address);
        TextInputLayout pAddressWrapper = pAddress.findViewById(R.id.textInput_wrapper);
        pAddressEditText = (EditText) pAddress.findViewById(R.id.box);
        pAddressWrapper.setHint("Address:");
        pAddressEditText.setText(property.getAddress(this));

        View pBedRoomEditTextView = findViewById(R.id.bedroomQuantity);
        pBedRoomEditText = (EditText) pBedRoomEditTextView.findViewById(R.id.box);

        View pBathRoomEditTextView = findViewById(R.id.bathroomQuantity);
        pBathRoomEditText= (EditText) pBathRoomEditTextView.findViewById(R.id.box);

        pAreaEditText = findViewById(R.id.propertyArea).findViewById(R.id.box);

        //Set hint for adding property price textbox
        View price = findViewById(R.id.property_price);
        TextInputLayout priceWrapper= price.findViewById(R.id.textInput_wrapper);
        priceEditText = price.findViewById(R.id.box);
        priceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceWrapper.setHint("Monthly Price:");

        //Set hint for add property description text box
        View description = findViewById(R.id.property_description);
        pdescEditText = description.findViewById(R.id.box);
        TextInputLayout desWrapper = findViewById(R.id.descInput_wrapper);
        desWrapper.setHint("Describe the Property in Detail");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}