package com.team5.besthouse.fragments.Inflate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.PropertyCardAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;
import java.util.List;

public class MorePropertyFragment extends BottomSheetDialogFragment {
    private Context context;
    private RecyclerView propertyView;
    private ArrayList<Property> list;
    private PropertyCardAdapter adapter2;
    private View progressIndicator;

    public MorePropertyFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog (savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate (R.layout.activity_more_property, null);
        bottomSheetDialog.setContentView(view);

        list = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            list = bundle.getParcelableArrayList("list");
        }

        propertyView = (RecyclerView) view.findViewById(R.id.more_property);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        //Set the layout manager
        linearLayoutManager2.setStackFromEnd(false);
        linearLayoutManager2.setReverseLayout(false);
        propertyView.setHasFixedSize(true);
        propertyView.setLayoutManager(linearLayoutManager2);

        propertyView.setNestedScrollingEnabled(false);
        adapter2 = new PropertyCardAdapter(getActivity(), list);
        propertyView.setAdapter(adapter2);

        propertyView.setHasFixedSize(true);

        return bottomSheetDialog;
    }
}