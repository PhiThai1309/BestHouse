package com.team5.besthouse.fragments;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

import com.google.android.material.elevation.SurfaceColors;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.MainActivity;
import com.team5.besthouse.adapters.LocationSuggestionAdapter;
import com.team5.besthouse.adapters.PropertiesSuggestionAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.FragmentAccountBinding;
import com.team5.besthouse.interfaces.RecyclerViewInterface;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Coordinates;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyAddress;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

public class MapsFragment extends Fragment implements RecyclerViewInterface {
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    protected LocationRequest locationRequest;
    protected FusedLocationProviderClient fusedLocationProviderClient;
    protected LocationCallback locationCallback;
    private FragmentAccountBinding binding;
    private GoogleMap map;
    private StoreService storeService;
    private PlacesClient placesClient;

    private SearchView searchView;
    private PropertiesSuggestionAdapter lsAdapter;
    RecyclerView rv;

    private Location lastKnownLocation;
    private ArrayList<Property> properties;

    private AutocompleteSessionToken token;

    public LatLng defaultLocation = new LatLng(-34, 151);

    public boolean locationPermissionGranted;

    private final LatLng HCM_NE_LATLNG = new LatLng(10.704313625774835, 106.60751276957609);
    private final LatLng HCM_SW_LATLNG = new LatLng(10.878598802235736, 106.647395525166);
    private final LatLng HCM_CEN_LATLNG = new LatLng(10.762622, 106.660172);

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }

        private void updateLocationUI() {
            if (map == null) {
                return;
            }
            try {
                if (locationPermissionGranted) {
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    getLocationPermission();
                }
            } catch (SecurityException e)  {
                Log.e("Exception: %s", e.getMessage());
            }
        }
    };

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            Log.i(TAG, "getting device location");
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        return this;
                    }

                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }
                });
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.i(TAG, "has lastKnownLocation");

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 17));

                                lsAdapter.setCurrentLocation(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                                lsAdapter.sortProperties(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                            }
                            else {
                                Log.i(TAG, "no lastKnownLocation");
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(
                                            new LatLng(
                                                    Coordinates.STATICCOORD().getLatitude(),
                                                    Coordinates.STATICCOORD().getLongitude()),
                                            17));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);

                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.Googlemap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        properties = new ArrayList<>();
        // set up store service
        storeService = new StoreService(view.getContext());

        locationRequest = new LocationRequest.Builder(30000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        if (!Places.isInitialized()){
            Places.initialize(view.getContext(), UnchangedValues.PLACES_API_KEY);
        }
        token = AutocompleteSessionToken.newInstance();

        placesClient = Places.createClient(view.getContext());

        rv = view.findViewById(R.id.listPlaces);

        settleRecyclerView();


        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        } else {
            // already permission granted
        }

        searchView = view.findViewById(R.id.search_view);
        rv.setVisibility(View.GONE);

        final Handler handler = new Handler();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit: " + query);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onSearchViewChange(query);
                    }
                }, 400);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.i(TAG, "onQueryTextChange: " + query);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onSearchViewChange(query);
                    }
                }, 400);
                return false;
                }
            });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            Log.i(TAG, "onFocusChange: " + hasFocus);
            if (!hasFocus){
                rv.clearAnimation();
                rv.setVisibility(View.GONE);
            }
            else {
                rv.setVisibility(View.VISIBLE);
            }
        });

//        // Construct a PlacesClient
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                Location location = locationResult.getLastLocation();
//
//            }
//        };
//        updateGPS();
        // initializing our search view.
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onSearchViewChange(String query) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addressesList = geocoder.getFromLocationName(query,1);
            LatLng coord;
            if(addressesList.size() > 0) {
                Address address = addressesList.get(0);
                coord = new LatLng(address.getLatitude(), address.getLongitude());
            }
            else {
                coord = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                Log.i(TAG, "onSearchViewChange: no address found. Hiding places");
                rv.setVisibility(View.GONE);
                return;
            }
            lsAdapter.sortProperties(coord);
            rv.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            Log.i(this.getClass().toString(), "getLatLngFromTextAddress: ");
        }
    }

//    private void updateGPS() {
//        // get permissions from the user to track GPS / get the current location from the fused client:
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Context) getActivity());
//
//        if (ActivityCompat.checkSelfPermission((Context) getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // user provided the permission
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) getActivity(), new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    updateUIValue(location);
//                }
//            });
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
//                ActivityCompat.requestPermissions((Activity) getActivity(),
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSIONS_FINE_LOCATION);
//            }
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        //filter for all rents such that its end date is after today on the db side
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(UnchangedValues.PROPERTIES_TABLE)
                .orderBy("propertyName")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    @SuppressLint("NotifyDataSetChanged")
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                                for(DocumentSnapshot newDoc : task.getResult().getDocuments()){
                                    Property p = newDoc.toObject(Property.class);

                                    if (p == null || p.getId() == null) {
                                        Log.i("Property", "null address found");
                                        continue;
                                    }
                                    Log.i("Property", p.getId());
                                    //get all contracts that have its end date after today and is from this property
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);
                                    database.collection(UnchangedValues.CONTRACTS_TABLE)
                                            .whereEqualTo("propertyId", p.getId())
                                            .whereGreaterThanOrEqualTo("endDate", Timestamp.now())
                                            .get()
                                            .addOnCompleteListener(v -> {
                                                if (v.isSuccessful()){
                                                    boolean ok = true;
                                                    for (QueryDocumentSnapshot document : v.getResult()) {
                                                        Contract contract = document.toObject(Contract.class);
                                                        if(contract.getStartDate().compareTo(Timestamp.now()) <= 0 && (contract.getContractStatus().equals(ContractStatus.ACTIVE) || (user.getEmail().equals(contract.getTenantEmail()) && contract.getContractStatus().equals(ContractStatus.PENDING)))){
                                                            ok = false;
                                                            break;
                                                        }
                                                    }
                                                    if (ok) {
                                                        lsAdapter.addNewItem(p);

                                                        map.addMarker(new MarkerOptions().position(p.getcoordinates()).title(p.getPropertyName()));
                                                        Log.i("MARKER" , p.toString());
                                                    }
                                                }
                                            });
                                }
                        }
                        else {
                            Toast.makeText(getContext(), "Error loading houses. Please contact admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUIValue(Location location) {
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch ((requestCode)) {
//            case PERMISSIONS_FINE_LOCATION:
//                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    updateGPS();
//                } else {
//                    Toast.makeText((Context) getActivity(), "Permission nedded", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }

    private void settleRecyclerView(){
        lsAdapter = new PropertiesSuggestionAdapter(getContext(), properties, this, new LatLng(Coordinates.STATICCOORD().getLatitude(), Coordinates.STATICCOORD().getLongitude()));
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(lm);
        rv.setAdapter(lsAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission((Context) getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void moveZoomMarker(LatLng latLng){
        // move the camera to position

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,map.getCameraPosition().zoom));
    }

    @Override
    public void onItemClick(int position) {
        moveZoomMarker(properties.get(position).getcoordinates());
        searchView.clearFocus();
        rv.setVisibility(View.GONE);
    }
}