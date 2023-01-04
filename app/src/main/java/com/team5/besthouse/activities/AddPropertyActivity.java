package com.team5.besthouse.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;

import java.io.IOException;
import java.util.List;

public class AddPropertyActivity extends AppCompatActivity {

    ImageButton returnButton;
    private EditText pAddressEditText;
    private EditText pnameEditText, priceEditText;
    private Spinner ptypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        //Set hint for adding property name textbox
        View pname = findViewById(R.id.property_name);
        pnameEditText = (EditText) pname.findViewById(R.id.box);
        pnameEditText.setHint("Property Name:");

        //Set hint for adding property type textbox
        View ptype = findViewById(R.id.property_type);
        ptypeSpinner = (Spinner) ptype.findViewById(R.id.box);

        //Set hint for adding property address textbox
        View pAddress = findViewById(R.id.property_address);
        pAddressEditText = (EditText) pAddress.findViewById(R.id.box);
        pAddressEditText.setHint("Address:");

        //Set hint for adding property price textbox
        View price = findViewById(R.id.property_price);
        priceEditText = (EditText) price.findViewById(R.id.box);
        priceEditText.setHint("Monthly Price:");

        //config the return button
        returnButton = findViewById(R.id.returnBar).findViewById(R.id.returnButton);
        setReturnButtonAction();
        setAddAddressAction();

    }

    private void setReturnButtonAction()
    {
        returnButton.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), LandlordActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void setAddAddressAction()
    {
        pAddressEditText.setOnClickListener(v ->{
            Intent i = new Intent(getApplicationContext(), LandLordMapsActivity.class);
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



}