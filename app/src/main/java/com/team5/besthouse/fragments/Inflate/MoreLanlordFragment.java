package com.team5.besthouse.fragments.Inflate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MoreLanlordFragment extends BottomSheetDialogFragment {
    private Context context;
    private RecyclerView propertyView;
    private List<Property> list;
    private PropertyCardAdapter adapter2;
    private StoreService storeService;
    private View progressIndicator;

    FirebaseFirestore db;

    public MoreLanlordFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog (savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate (R.layout.activity_more_landlord, null);
        bottomSheetDialog.setContentView(view);

        //get db instance
        db = FirebaseFirestore.getInstance();

        // set up store service
        storeService = new StoreService(getActivity());

//        progressIndicator = view.findViewById(R.id.more_progressBar);
//        progressIndicator.setVisibility(View.VISIBLE);

        list = new ArrayList<>();

//        propertyView = (RecyclerView) view.findViewById(R.id.more_property);
//        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
//        //Set the layout manager
//        linearLayoutManager2.setStackFromEnd(false);
//        linearLayoutManager2.setReverseLayout(false);
//        propertyView.setHasFixedSize(true);
//        propertyView.setLayoutManager(linearLayoutManager2);
//
//        propertyView.setNestedScrollingEnabled(false);
//        adapter2 = new PropertyCardAdapter(getActivity(), list);
//        propertyView.setAdapter(adapter2);
//
//        propertyView.setHasFixedSize(true);

        return bottomSheetDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //filter for all rents such that its end date is after today on the db side
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(UnchangedValues.PROPERTIES_TABLE)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        progressIndicator.setVisibility(View.VISIBLE);
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
//                                                          Log.i("ADDED" , p.toString());
//                                                        adapter2.notifyDataSetChanged();
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
//                                adapter2.notifyDataSetChanged();
                            }
                        }
//                        progressIndicator.setVisibility(View.GONE);
                    }
                });
    }
}