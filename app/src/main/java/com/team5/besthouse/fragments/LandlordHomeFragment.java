package com.team5.besthouse.fragments;

import android.location.Address;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team5.besthouse.R;
import com.team5.besthouse.activities.LandlordActivity;
import com.team5.besthouse.adapters.LandlordPropertyAdapter;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyType;
import com.team5.besthouse.models.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandlordHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandlordHomeFragment extends Fragment {
    private RecyclerView postedPropertyView;
    private List<Property> list;
    private LandlordPropertyAdapter landlordAdapter;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landlord_home, container, false);

        list = new ArrayList<>();
        Address address = new Address(Locale.US);

        postedPropertyView = (RecyclerView) view.findViewById(R.id.posted_property);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postedPropertyView.setHasFixedSize(true);
        postedPropertyView.setLayoutManager(linearLayoutManager);

        postedPropertyView.setNestedScrollingEnabled(false);
        landlordAdapter = new LandlordPropertyAdapter((LandlordActivity) getContext(), list);
        postedPropertyView.setAdapter(landlordAdapter);
        // Inflate the layout for this fragment
        return view;
    }
}