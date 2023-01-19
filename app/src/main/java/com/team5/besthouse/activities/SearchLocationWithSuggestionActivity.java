package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.LocationSuggestionAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.LandLordMapsFragment;
import com.team5.besthouse.interfaces.RecyclerViewInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchLocationWithSuggestionActivity extends AppCompatActivity implements RecyclerViewInterface {


    private ArrayList<String> locationTextList = new ArrayList<>();
    private LocationSuggestionAdapter lsAdapter;
    private PlacesClient placesClient;
    private ImageButton returnImageButton;
    private EditText searchEditText;
    private AutocompleteSessionToken token;
    private RectangularBounds bounds;
    private ImageButton returnImageBtn;

    private final LatLng HCM_NE_LATLNG = new LatLng(10.704313625774835, 106.60751276957609);
    private final LatLng HCM_SW_LATLNG = new LatLng(10.878598802235736, 106.647395525166);
    private final LatLng HCM_CEN_LATLNG = new LatLng(10.762622, 106.660172);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location_with_suggestion);

        //retrieve the image return button
        returnImageButton = findViewById(R.id.searchBar).findViewById(R.id.returnButton);
        searchEditText = findViewById(R.id.searchBar).findViewById(R.id.box);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        returnImageBtn = findViewById(R.id.searchBar).findViewById(R.id.returnButton);
        setReturnButtonAction();
        settleRecyclerView();
        initializePlaceAPI();
        setSearchAction();
    }
    /**
     *
     */
    private void setReturnButtonAction()
    {
        returnImageBtn.setOnClickListener( v -> {
            finish();
        });
    }

    /**
     *
     */
    private void settleRecyclerView()
    {
        try {
            lsAdapter = new LocationSuggestionAdapter(getApplicationContext(), locationTextList, this);
            RecyclerView rv = findViewById(R.id.locationRecyclerView);
            LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(lm);
            rv.setAdapter(lsAdapter);
            rv.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            Log.i("ERROR", "settleRecyclerView: " + e.getMessage());
        }

    }

    /**
     *
      */
    private void initializePlaceAPI() {
        if (!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(), UnchangedValues.PLACES_API_KEY);
        }
        // create the client for the place
        placesClient = Places.createClient(getApplicationContext());
        token = AutocompleteSessionToken.newInstance();
        // bounds location for hcm city
        bounds = RectangularBounds.newInstance(
                HCM_NE_LATLNG,HCM_SW_LATLNG);

    }

    private void setSearchAction(){

            final Handler handler = new Handler();

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // create the request
                            String query = searchEditText.getText().toString() + " Thành Phố Hồ Chí Minh";

                            Geocoder geocoder = new Geocoder(getApplicationContext());

                            lsAdapter.clearList();

                            try {
                                List<Address> addresses = geocoder.getFromLocationName(query, 15);
                                for (Address address : addresses) {
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        lsAdapter.addNewItem(address.getAddressLine(i));
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }
            });
    }

    /**
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getApplicationContext(), LandLordMapsFragment.class);
        i.putExtra(UnchangedValues.LOCATION_ADDRESS, locationTextList.get(position));
        setResult(RESULT_OK, i);
        finish();
    }

}