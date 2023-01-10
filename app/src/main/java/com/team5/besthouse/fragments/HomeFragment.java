package com.team5.besthouse.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.PropertyAdapter;
import com.team5.besthouse.PropertyAdapter2;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.MainActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Coordinates;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyType;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.Utilities;
import com.team5.besthouse.services.StoreService;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    private Context context;
    private RecyclerView featureView;
    private RecyclerView propertyView;
    private List<Property> list;
    private PropertyAdapter adapter1;
    private PropertyAdapter2 adapter2;
    private StoreService storeService;
    private View progressIndicator;

    FirebaseFirestore db;

    private TextView tv;

    public boolean locationPermissionGranted;
    public Location lastKnownLocation;
    protected FusedLocationProviderClient fusedLocationProviderClient;
    protected LocationRequest locationRequest;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = inflater.getContext();

        progressIndicator = view.findViewById(R.id.home_progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        //get db instance

        db = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        // set up store service
        storeService = new StoreService(context);

        //Get the recycler view and
        featureView = (RecyclerView) view.findViewById(R.id.feature_property);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        featureView.setHasFixedSize(true);
        featureView.setLayoutManager(linearLayoutManager);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(featureView);

        list = new ArrayList<>();
        adapter1 = new PropertyAdapter((MainActivity) getContext(), list);
        featureView.setAdapter(adapter1);

        //add 5 static properties to list
//        list.addAll(Collections.nCopies(5, Property.STATICPROPERTY));

        //add properties from db to list
        //this does not update the recycler view
        Address address = new Address(Locale.US);

        propertyView = (RecyclerView) view.findViewById(R.id.main_property);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        //Set the layout manager
        linearLayoutManager2.setStackFromEnd(false);
        linearLayoutManager2.setReverseLayout(false);
        propertyView.setHasFixedSize(true);
        propertyView.setLayoutManager(linearLayoutManager2);

        propertyView.setNestedScrollingEnabled(false);
        adapter2 = new PropertyAdapter2((MainActivity) getContext(), list);
        propertyView.setAdapter(adapter2);

        featureView.setHasFixedSize(true);
        propertyView.setHasFixedSize(true);

        tv = view.findViewById(R.id.home_location);
        getLocationPermission();
        getDeviceLocation(tv);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationRequest = new LocationRequest();

        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        } else {
            // already permission granted
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //filter for all rents such that its end date is after today on the db side
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(UnchangedValues.PROPERTIES_TABLE)
                .orderBy("propertyName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        if(error != null)
                        {
                            Log.w("ERROR ERROR ", error);
                            return;
                        }
//                        Log.w("ERROR ERROR ", value.getDocumentChanges().get(0).getDocument().getData().toString());

                        //filter for rents that begins before today on the client side
                        assert value != null;
                        for(DocumentChange newDoc : value.getDocumentChanges()){
                            Property p = newDoc.getDocument().toObject(Property.class);
//                            Log.i("Property", p.getId() == null ? "null address!" : p.getId());
//                            Log.i("Property", newDoc.getType().toString());
                            if(newDoc.getType() == DocumentChange.Type.ADDED){
                                list.remove(p);
                                try {
                                    //get all contracts that have its end date after today and is from this property

                                    Gson gson = new Gson();
                                    User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);

//                                    Log.i(TAG, "onEvent: " + p);
                                    database.collection(UnchangedValues.CONTRACTS_TABLE)
                                            .whereEqualTo("propertyId", p.getId())
                                            .whereGreaterThanOrEqualTo("endDate", Timestamp.now())
                                            .get()
                                            .addOnCompleteListener(v -> {
                                                if (v.isSuccessful()){
                                                    boolean ok = true;
//                                                    Log.i("TAG", "onEvent: " + v.getResult().getDocuments().size());
                                                    for (QueryDocumentSnapshot document : v.getResult()) {
                                                        Contract contract = document.toObject(Contract.class);
                                                        if(contract.getStartDate().compareTo(Timestamp.now()) <= 0 && (contract.getContractStatus().equals(ContractStatus.ACTIVE) || (user.getEmail().equals(contract.getTenantEmail()) && contract.getContractStatus().equals(ContractStatus.PENDING)))){
                                                            ok = false;
                                                            break;
                                                        }
                                                    }
                                                    if (ok) {
                                                        list.add(p);
//                                                        Log.i("ADDED" , p.toString());
                                                        adapter1.notifyDataSetChanged();
                                                        adapter2.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    Log.d("ERROR 2", "Error adding object : " + newDoc.toString() + ", Exception " + e.getMessage());
                                }
                            }
                            else {
                                list.remove(p);
                                if (newDoc.getType().equals(DocumentChange.Type.MODIFIED)) {
                                    list.add(p);
                                }
                                adapter1.notifyDataSetChanged();
                                adapter2.notifyDataSetChanged();
                            }
                        }
                        progressIndicator.setVisibility(View.GONE);
                    }
                });
    }

    private void getDeviceLocation(TextView text) {
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
                        return null;
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
                            Log.i(TAG, "has lastKnownLocation");
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            Geocoder geocoder = new Geocoder(context);
                            String cityName = "Ho Chi Minh";
                            try {
                                cityName = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1).get(0).getAdminArea();
                            } catch (IOException ignored) {
                            }
                            if (lastKnownLocation != null) {
                                text.setText(cityName);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            text.setText("NONE");
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

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
}