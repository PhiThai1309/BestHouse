package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.LocationSuggestionAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.interfaces.RecyclerViewInterface;

import java.util.ArrayList;

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

    private void setSearchAction()
    {
       searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

              if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH)
              {

                  // create the request
                  String query = searchEditText.getText().toString();
                  FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                          .setLocationBias(bounds)
                          .setOrigin(HCM_CEN_LATLNG)
                          .setCountries("VN")
                          .setSessionToken(token)
                          .setQuery(query)
                          .build();
                  lsAdapter.clearList();

                  // send the request
                  placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {

                      for(AutocompletePrediction prediction: response.getAutocompletePredictions())
                      {

                          Log.i("TESTER", prediction.getPrimaryText(null).toString());
                          Log.i("TESTER", prediction.getSecondaryText(null).toString());
                          lsAdapter.addNewItem(prediction.getFullText(null).toString());
                      }

                  }).addOnFailureListener((exception)->
                  {
                      if (exception instanceof ApiException)
                      {
                          ApiException apiException = (ApiException) exception;
                          Log.e("TESTERERROR", "Place not found: " +apiException.getStatusCode());
                      }
                  });
              }
               return false;
           }
       });
    }

    /**
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getApplicationContext(), LandLordMapsActivity.class);
        i.putExtra(UnchangedValues.LOCATION_ADDRESS, locationTextList.get(position));
        setResult(RESULT_OK, i);
        finish();
    }

}