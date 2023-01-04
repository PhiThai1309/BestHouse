package com.team5.besthouse.activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.ActivityLandLordMapsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LandLordMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final LatLng HCM_LATLNG = new LatLng(10.762622,106.698402);
    private GoogleMap mMap;
    private ActivityLandLordMapsBinding binding;
    private ImageButton returnImageBtn;
    private EditText searchEditText;
    private Address selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLandLordMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        returnImageBtn = binding.searchBar.returnButton;
        searchEditText = binding.searchBar.box;

        setReturnButtonAction();
        setSearchBarAction();
        setSelectAddressBtnAction();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        // Add a marker in HCM and move the camera
        moveZoomMarker(HCM_LATLNG);


        //zoom control button
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //set map click action
        setMapClickAction();

    }

    /**
     *
     */
    private void setSelectAddressBtnAction()
    {
        binding.selectAddressBtn.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), AddPropertyActivity.class);
            i.putExtra(UnchangedValues.LOCATION_ADDRESS, searchEditText.getText().toString() );
            setResult(RESULT_OK, i);
            finish();
        });
    }

    /**
     *
     */
    private void setSearchBarAction()
    {
//        searchEditText.setOnClickListener(v->{
//           // move to the search location with suggestion activity
//            Intent i = new Intent(getApplicationContext(), SearchLocationWithSuggestionActivity.class);
//            startActivityForResult(i,100);
//            finish();
//
//        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    String query = searchEditText.getText().toString();
                    moveZoomMarker(getLatLngFromTextAddress(query));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     *
     */
    private void setMapClickAction()
    {
        mMap.setOnMapClickListener( latLng ->{
            moveZoomMarker(latLng);
        });
    }

    /**
     *
     */
    private void setReturnButtonAction()
    {
        returnImageBtn.setOnClickListener( v -> {

            Intent i = new Intent(getApplicationContext(), LandlordActivity.class);
            if(selectedAddress != null)
            {
                i.putExtra(UnchangedValues.LOCATION_ADDRESS, selectedAddress.getAddressLine(0));
                setResult(RESULT_OK);
            }
            else
            {
               setResult(RESULT_CANCELED);
            }
            finish();
        });
    }

    /**
     *
     * @param latLng
     */
    private void moveZoomMarker(LatLng latLng)
    {
        //clear the marker
        mMap.clear();
        // add the marker with position
        mMap.addMarker(new MarkerOptions().position(latLng).title(UnchangedValues.SELECT_LOCATION));
        // move the camera to position

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,mMap.getCameraPosition().zoom));

        //initiate the geocoder
        Geocoder geocoder = new Geocoder(getApplicationContext());
        // update content of the search bar
        try {
            List<Address> addressesList = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
            if(addressesList.size() > 0)
            {
                // update the search bar content
                searchEditText.setText(addressesList.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            Log.i(this.getClass().toString(), "moveZoomMarker: " + e.getMessage());
        }
    }

    /**
     * This method is used to get Latitude and Longitude from the Text Address
     * @param query Text Address
     * @return LatLng if the location id found and null of not found
     */
    private LatLng getLatLngFromTextAddress(String query)
    {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addressesList = geocoder.getFromLocationName(query,1);
            if(addressesList.size() > 0)
            {
                Address address = addressesList.get(0);
                return new LatLng(address.getLatitude(),address.getLongitude()) ;
            }
        } catch (IOException e) {
            Log.i(this.getClass().toString(), "getLatLngFromTextAddress: ");
        }
        return null;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK)
        {
            if(data.getExtras().get(UnchangedValues.LOCATION_ADDRESS) != null)
            {
                String query = data.getExtras().get(UnchangedValues.LOCATION_ADDRESS).toString();
                LatLng latLng = getLatLngFromTextAddress(query);
                if(latLng != null)
                {
                    moveZoomMarker(latLng);
                }
            }
        }
    }



}