package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.team5.besthouse.R;

public class AddPropertyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        //Set hint for adding property name textbox
        View pname = findViewById(R.id.property_name);
        EditText pnameEditText = (EditText) pname.findViewById(R.id.box);
        pnameEditText.setHint("Property Name:");

        //Set hint for adding property type textbox
        View ptype = findViewById(R.id.property_type);
        EditText ptypeEditText = (EditText) ptype.findViewById(R.id.box);
        ptypeEditText.setHint("Property Type:");

        //Set hint for adding property address textbox
        View pAddress = findViewById(R.id.property_address);
        EditText pAddressEditText = (EditText) pAddress.findViewById(R.id.box);
        pAddressEditText.setHint("Address:");

        //Set hint for adding property price textbox
        View price = findViewById(R.id.property_price);
        EditText priceEditText = (EditText) price.findViewById(R.id.box);
        priceEditText.setHint("Monthly Price:");
    }
}