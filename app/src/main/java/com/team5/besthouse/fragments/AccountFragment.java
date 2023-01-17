package com.team5.besthouse.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.LoginActivity;
import com.team5.besthouse.adapters.ContractPartialAdapter;
import com.team5.besthouse.adapters.PropertyPartialCardAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.FragmentAccountBinding;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;
import java.util.List;

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
    private List<Contract> contractList;
    private ContractPartialAdapter adapter1;
    private LinearProgressIndicator progressIndicator;

    private RecyclerView propertyView;
    private List<Property> propertyList;
    private PropertyPartialCardAdapter adapter;

    Gson gson;
    User user;

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
        storeService = new StoreService(getContext().getApplicationContext());

        gson = new Gson();
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        //filter for all rents such that its end date is after today on the db side
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Gson gson = new Gson();
        User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class);
        String userEmail = user.getEmail();

        if (user.getRole() == UserRole.LANDLORD){
            database.collection(UnchangedValues.CONTRACTS_TABLE)
                    .whereEqualTo("landlordEmail", userEmail)
                    .addSnapshotListener(eventListener);
        }
        else {
            database.collection(UnchangedValues.CONTRACTS_TABLE)
                    .whereEqualTo("tenantEmail", userEmail)
                    .addSnapshotListener(eventListener);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Property property = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Property.class);

        database.collection(UnchangedValues.PROPERTIES_TABLE)
                .whereEqualTo("landlordEmail", userEmail)
                .addSnapshotListener(eventListenerProperty);
    }

    EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//            progressIndicator.setVisibility(View.VISIBLE);
            if(error != null)
            {
                Log.w("ERROR: ", error);
                return;
            }
            assert value != null;
            for(DocumentChange newDoc : value.getDocumentChanges()){
                Contract c = newDoc.getDocument().toObject(Contract.class);
//                            Log.i("Property", p.getId() == null ? "null address!" : p.getId());
//                            Log.i("Property", newDoc.getType().toString());
                contractList.remove(c);
                contractList.add(c);
                adapter1.notifyDataSetChanged();
            }
//            progressIndicator.setVisibility(View.GONE);
        }
    };

    EventListener<QuerySnapshot> eventListenerProperty = new EventListener<QuerySnapshot>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//            progressIndicator.setVisibility(View.VISIBLE);
            if(error != null)
            {
                Log.w("ERROR: ", error);
                return;
            }
            assert value != null;
            for(DocumentChange newDoc : value.getDocumentChanges()){
                Property p = newDoc.getDocument().toObject(Property.class);
//                            Log.i("Property", p.getId() == null ? "null address!" : p.getId());
//                            Log.i("Property", newDoc.getType().toString());
                propertyList.remove(p);
                propertyList.add(p);
                adapter.notifyDataSetChanged();
            }
//            progressIndicator.setVisibility(View.GONE);
        }
    };

    public void logOutMenu(){
        MaterialToolbar logout = binding.getRoot().findViewById(R.id.logout_toolbar);
        logout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (storeService.clearTheStore()) {
                    try {
                        tempDisconnectGoogleAccount();
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

    private void tempDisconnectGoogleAccount() {
       GoogleSignInClient mGoogleSignInAccount = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN) ;
        if(mGoogleSignInAccount != null)
        {
            mGoogleSignInAccount.signOut()
                    .addOnCompleteListener( getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("SIGNOUT", "complete");
                        }
                    });
        }

        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getActivity().getResources().getColor(R.color.md_theme_surfaceVariant));

//        progressIndicator = binding.getRoot().findViewById(R.id.account_progressBar);
//        progressIndicator.setVisibility(View.VISIBLE);

        //Contract History setup here--------------------------------------------------------
        View historyTitle = binding.getRoot().findViewById(R.id.contract_history_title);
        TextView seeMoreTitle = historyTitle.findViewById(R.id.see_more_title);
        seeMoreTitle.setText("Contract History");

        View historyWrapper = binding.getRoot().findViewById(R.id.contract_history_wrapper);
        historyView = historyWrapper.findViewById(R.id.contract_history);

        contractList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        historyView.setHasFixedSize(true);
        historyView.setLayoutManager(linearLayoutManager);

        adapter1 = new ContractPartialAdapter(getContext(), contractList);
        historyView.setAdapter(adapter1);
        historyView.setHasFixedSize(true);

        if(contractList.size() == 0) {
            TextView seeMoreButton = historyTitle.findViewById(R.id.see_more);
            seeMoreButton.setVisibility(View.GONE);
            historyView.setVisibility(View.GONE);
        }

        //Account name setup here---------------------------------------------------------
        TextView accountName = binding.getRoot().findViewById(R.id.account_name);
        accountName.setText(user.getFullName());

        //Property setup here-------------------------------------------------------------
        Gson gson = new Gson();
        User user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);
        if(user.getRole() == UserRole.TENANT) {
            LinearLayout propertyList = binding.getRoot().findViewById(R.id.property_list);
            propertyList.setVisibility(View.GONE);
        }

        View propertyLayout = binding.getRoot().findViewById(R.id.property_list_title);
        TextView propertyTitle = propertyLayout.findViewById(R.id.see_more_title);
        propertyTitle.setText("Your property");

        propertyList = new ArrayList<>();

        propertyView = binding.getRoot().findViewById(R.id.account_property);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        propertyView.setHasFixedSize(true);
        propertyView.setLayoutManager(linearLayoutManager2);

        adapter = new PropertyPartialCardAdapter(getContext(), propertyList);
        propertyView.setAdapter(adapter);
        historyView.setHasFixedSize(true);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void showTextLong(String text)
    {
        Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void propertyListing() {

    }
}