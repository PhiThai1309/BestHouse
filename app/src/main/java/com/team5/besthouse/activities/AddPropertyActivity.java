package com.team5.besthouse.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.LocationSuggestionAdapter;
import com.team5.besthouse.adapters.PropertyImageInsertAdapter;
import com.team5.besthouse.adapters.PropertyTypeSelectAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.interfaces.RecyclerViewInterface;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyType;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPropertyActivity extends AppCompatActivity {

    ImageButton returnButton;
    private EditText pAddressEditText;
    private EditText pnameEditText, priceEditText;
    private Spinner ptypeSpinner;
    private String selectPropertyType;
    private PropertyImageInsertAdapter piiAdapter;
    private ArrayList<Bitmap> propertyImageList;
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
        priceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceEditText.setHint("Monthly Price:");

        //config the return button
        returnButton = findViewById(R.id.returnBar).findViewById(R.id.returnButton);
        setReturnButtonAction();
        setAddAddressAction();
        initializeSpinner();
//        setSpinSelectAction();
        settleRecyclerView();


    }


    private void initializeSpinner()
    {
        ArrayList<PropertyType> pList = new ArrayList<>();
        pList.add(PropertyType.APARTMENT);
        pList.add(PropertyType.HOUSE);
        pList.add(PropertyType.FLOOR);
        pList.add(PropertyType.ROOM);
        ptypeSpinner.setAdapter(new PropertyTypeSelectAdapter(this, pList));
    }

    /**
     *
     */
    private void settleRecyclerView() {
        try {
            propertyImageList = new ArrayList<>();
//            Bitmap addIconBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_baseline_add_35);
//            propertyImageList.add(addIconBitmap);
            piiAdapter = new PropertyImageInsertAdapter(this, propertyImageList);
            LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            RecyclerView rv = findViewById(R.id.property_image_select_recycler_view);
            rv.setLayoutManager(lm);
            rv.setAdapter(piiAdapter);
            rv.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            Log.i("ERROR", "settleRecyclerView: " + e.getMessage());
        }

    }

        private void setSpinSelectAction()
    {
//        ptypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//               selectPropertyType = (String) parent.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                selectPropertyType = null;
//            }
//        });
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

    private Bitmap convertUriToBitmap(Uri inputUriImage)
    {
        try {
            Bitmap imageBitmap  = MediaStore.Images.Media.getBitmap(this.getContentResolver(),inputUriImage );
            // resize the image
//            ImageView iv = findViewById(R.id.cardViewImage) ;
//            imageBitmap = Bitmap.createScaledBitmap(imageBitmap,iv.getWidth() , iv.getHeight(), false );
            return  imageBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }



}