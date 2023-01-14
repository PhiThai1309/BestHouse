package com.team5.besthouse.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.team5.besthouse.activities.LoginActivity;
import com.team5.besthouse.activities.MainActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.FragmentAccountBinding;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAccountBinding binding;
    private FirebaseAuth firebaseAuth;
    private StoreService storeService;


    private RecyclerView historyView;
    private List<Property> list;
    private PropertyAdapter2 adapter1;
    private LinearProgressIndicator progressIndicator;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
                                adapter1.notifyDataSetChanged();
//                                adapter2.notifyDataSetChanged();
                            }
                        }
                        progressIndicator.setVisibility(View.GONE);
                    }
                });
    }

    public void logOutMenu(){
        MaterialToolbar logout = binding.getRoot().findViewById(R.id.logout_toolbar);
        logout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (storeService.clearTheStore()) {
                    try {
                        firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut(); // sign out from firebase
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(UnchangedValues.LOGOUT_PERFORMED, "logout");
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Log.d("ErrorLogout", e.getMessage());
                        e.printStackTrace();
                        showTextLong("Error: Can't Logout");
                    }
                } else {
                    showTextLong("Error: Can't Logout");
                }
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        storeService = new StoreService(getActivity().getApplicationContext());
        logOutMenu();
//        setSignOutAction();

        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getActivity().getResources().getColor(R.color.md_theme_surfaceVariant));

        progressIndicator = binding.getRoot().findViewById(R.id.account_progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        historyView = binding.getRoot().findViewById(R.id.contract_history);

        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        historyView.setHasFixedSize(true);
        historyView.setLayoutManager(linearLayoutManager);

        adapter1 = new PropertyAdapter2((MainActivity) getContext(), list);
        historyView.setAdapter(adapter1);
        historyView.setHasFixedSize(true);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

//    private void setSignOutAction() {
//        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // make sure to clear the store service
//                if (storeService.clearTheStore()) {
//                    try {
//                        firebaseAuth = FirebaseAuth.getInstance();
//                        firebaseAuth.signOut(); // sign out from firebase
//                        Intent intent = new Intent(getActivity(), LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.putExtra(UnchangedValues.LOGOUT_PERFORMED, "logout");
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        Log.d("ErrorLogout", e.getMessage());
//                        e.printStackTrace();
//                        showTextLong("Error: Can't Logout");
//                    }
//                } else {
//                    showTextLong("Error: Can't Logout");
//                }
//            }
//        });
//    }

    private void showTextLong(String text)
    {
        Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}