package com.team5.besthouse.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.ActivityLandLordMapsBinding;

public class LandLordMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final LatLng HCM_LATLNG = new LatLng(106.698402,106.698402);
    private GoogleMap mMap;
    private ActivityLandLordMapsBinding binding;
    private ImageButton returnImageBtn;
    private Address selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLandLordMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        returnImageBtn = binding.searchBar.returnButton;

        setReturnButtonAction();
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

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(HCM_LATLNG));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HCM_LATLNG, 10));

        //zoom control button
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


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

}