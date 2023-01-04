package com.team5.besthouse.fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team5.besthouse.PropertyAdapter;
import com.team5.besthouse.PropertyAdapter2;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.MainActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Coordinates;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyAddress;
import com.team5.besthouse.models.PropertyType;
import com.team5.besthouse.models.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private RecyclerView featureView;
    private RecyclerView propertyView;
    private List<Property> list;
    private PropertyAdapter adapter1;
    private PropertyAdapter2 adapter2;

    FirebaseFirestore db;

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

        //get db instance

        db = FirebaseFirestore.getInstance();

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
        db.collection(UnchangedValues.PROPERTIES_TABLE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    list.add(document.toObject(Property.class));
                                } catch (Exception e) {
                                    Log.d(TAG, "Error adding object : " + document.toString() + ", Exception " + e.getMessage());
                                }

                                Log.d(TAG, "Found property " + document.getId() + " => " + document.getData());
                            }
                            adapter1.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

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


        // Inflate the layout for this fragment
        return view;
    }
}