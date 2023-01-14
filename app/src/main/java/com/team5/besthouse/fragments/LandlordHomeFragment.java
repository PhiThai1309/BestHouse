package com.team5.besthouse.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.team5.besthouse.activities.AddPropertyActivity;
import com.team5.besthouse.activities.LandlordActivity;
import com.team5.besthouse.activities.MainActivity;
import com.team5.besthouse.adapters.LandlordPropertyAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandlordHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandlordHomeFragment extends Fragment {
    private RecyclerView postedPropertyView;
    private List<Property> list;
    private LandlordPropertyAdapter landlordAdapter;

    private Context context;
    private RecyclerView featureView;
    private RecyclerView propertyView;
    private PropertyAdapter adapter1;
    private PropertyAdapter2 adapter2;
    private StoreService storeService;
    private View progressIndicator;

    FirebaseFirestore db;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LandlordHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LandlordHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandlordHomeFragment newInstance(String param1, String param2) {
        LandlordHomeFragment fragment = new LandlordHomeFragment();
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
//                                                        adapter1.notifyDataSetChanged();
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
//                                adapter1.notifyDataSetChanged();
                                adapter2.notifyDataSetChanged();
                            }
                        }
                        progressIndicator.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landlord_home, container, false);

        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        context = inflater.getContext();

        progressIndicator = view.findViewById(R.id.home_progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        //get db instance

        db = FirebaseFirestore.getInstance();

        // set up store service
        storeService = new StoreService(context);

        //Get the recycler view and
        featureView = (RecyclerView) view.findViewById(R.id.posted_property);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        featureView.setHasFixedSize(true);
        featureView.setLayoutManager(linearLayoutManager);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(featureView);

        list = new ArrayList<>();
        adapter2 = new PropertyAdapter2((LandlordActivity) getContext(), list);
        featureView.setAdapter(adapter2);
        featureView.setHasFixedSize(true);

        FloatingActionButton fab = view.findViewById(R.id.float_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPropertyActivity.class);
//               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}