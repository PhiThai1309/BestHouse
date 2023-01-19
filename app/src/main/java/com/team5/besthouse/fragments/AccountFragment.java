package com.team5.besthouse.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.LoginActivity;
import com.team5.besthouse.adapters.ContractAdapter;
import com.team5.besthouse.adapters.PropertyCardAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.FragmentAccountBinding;
import com.team5.besthouse.fragments.Inflate.MoreContractFragment;
import com.team5.besthouse.fragments.Inflate.MorePropertyFragment;
import com.team5.besthouse.interfaces.GetBitMapCallBack;
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
    private ArrayList<Contract> contractList;
    private ContractAdapter adapter1;

    private RecyclerView propertyView;
    private ArrayList<Property> propertyList;
    private PropertyCardAdapter adapter;

    private View historyTitle, historyWrapper, propertyTitle, propertyWrapper;

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
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        //filter for all rents such that its end date is after today on the db side
        FirebaseFirestore database = FirebaseFirestore.getInstance();
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

        database.collection(UnchangedValues.PROPERTIES_TABLE)
                .whereEqualTo("landlordEmail", userEmail)
                .addSnapshotListener(eventListenerProperty);

    }

    EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error != null)
            {
                Log.w("ERROR: ", error);
                return;
            }
            assert value != null;
            for(DocumentChange newDoc : value.getDocumentChanges()){
                Contract p = newDoc.getDocument().toObject(Contract.class);
                contractList.remove(p);
                if (newDoc.getType() != DocumentChange.Type.REMOVED){
                    contractList.add(p);
                }
                adapter1.notifyDataSetChanged();
            }

            historyTitle = binding.getRoot().findViewById(R.id.contract_history_title);
            historyWrapper = binding.getRoot().findViewById(R.id.contract_history_wrapper);
            ImageView seeMoreBtn = historyTitle.findViewById(R.id.see_more);
            TextView noneData = historyWrapper.findViewById(R.id.display_none);

            if(contractList.isEmpty()) {
                seeMoreBtn.setVisibility(View.GONE);
                historyView.setVisibility(View.GONE);
            } else if(contractList.size() <= 5) {
                seeMoreBtn.setVisibility(View.GONE);
                noneData.setVisibility(View.GONE);
            } else {
                noneData.setVisibility(View.GONE);
            }
        }
    };

    EventListener<QuerySnapshot> eventListenerProperty = new EventListener<QuerySnapshot>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error != null)
            {
                Log.w("ERROR: ", error);
                return;
            }
            assert value != null;
            for(DocumentChange newDoc : value.getDocumentChanges()){
                Property p = newDoc.getDocument().toObject(Property.class);
                propertyList.remove(p);
                propertyList.add(p);
                adapter.notifyDataSetChanged();
            }

            propertyTitle = binding.getRoot().findViewById(R.id.property_list_title);
            propertyWrapper = binding.getRoot().findViewById(R.id.property_list);
            ImageView seeMoreButton = propertyTitle.findViewById(R.id.see_more);
            TextView noData = propertyWrapper.findViewById(R.id.display_none);

            if(propertyList.isEmpty()) {
                seeMoreButton.setVisibility(View.GONE);
                propertyView.setVisibility(View.GONE);
            } else if(propertyList.size() <= 5) {
                seeMoreButton.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
            } else {
                noData.setVisibility(View.GONE);
            }
        }
    };

    public void logOutMenu(){
        MaterialToolbar logout = binding.getRoot().findViewById(R.id.logout_toolbar);
        logout.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (storeService.clearTheStore()) {
                    try {
                        LoginManager.getInstance().logOut();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        storeService = new StoreService(getActivity().getApplicationContext());
        logOutMenu();

        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        //Contract History setup here--------------------------------------------------------
        historyTitle = binding.getRoot().findViewById(R.id.contract_history_title);
        TextView seeMoreTitle = historyTitle.findViewById(R.id.see_more_title);
        seeMoreTitle.setText("Contract History");

        historyWrapper = binding.getRoot().findViewById(R.id.contract_history_wrapper);
        historyView = historyWrapper.findViewById(R.id.recycler_view);

//        ImageView moreContract = historyTitle.findViewById(R.id.see_more);
        historyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list",  contractList);

                MoreContractFragment bottomDialogFragment = new MoreContractFragment();
                bottomDialogFragment.setArguments(bundle);
                bottomDialogFragment.show(requireActivity().getSupportFragmentManager(), "ActionBottomDialogFragment.TAG");
            }
        });
        contractList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        historyView.setHasFixedSize(true);
        historyView.setLayoutManager(linearLayoutManager);

        //limits max 5 items
        adapter1 = new ContractAdapter(getContext(), contractList, 5);
        historyView.setAdapter(adapter1);
        historyView.setHasFixedSize(true);

        // user image set up
        ImageView userImage = binding.getRoot().findViewById(R.id.account_image);
        String imageURL = storeService.getStringValue(UnchangedValues.USER_IMAGE_URL_COL);
        if(!imageURL.isEmpty())
        {
            loadImageFromFSUrl(user.getImageUrl(), new GetBitMapCallBack() {
                @Override
                public void getBitMap(Bitmap bitmap) {
                    userImage.setImageBitmap(bitmap);
                }
            });
        }

        //Account name setup here---------------------------------------------------------
        TextView accountName = binding.getRoot().findViewById(R.id.account_name);
        accountName.setText(user.getFullName());

        TextView accountType = binding.getRoot().findViewById(R.id.account_type);
        accountType.setText(String.valueOf(user.getRole()));

        //Property setup here-------------------------------------------------------------
        View propertyWrapper = binding.getRoot().findViewById(R.id.property_list);

        View propertyLayout = binding.getRoot().findViewById(R.id.property_list_title);
        TextView propertyTitle = propertyLayout.findViewById(R.id.see_more_title);

        LinearLayout pointView = binding.getRoot().findViewById(R.id.point_section);
        TextView point = pointView.findViewById(R.id.point);

        if(user.getRole() == UserRole.TENANT) {
            propertyWrapper.setVisibility(View.GONE);
            propertyTitle.setVisibility(View.GONE);
            point.setText(String.valueOf(storeService.getIntValue(UnchangedValues.USER_LOYAL_COL)) + " points");
        } else {
            View divider = binding.getRoot().findViewById(R.id.line);
            divider.setVisibility(View.GONE);
            pointView.setVisibility(View.GONE);
        }

        propertyTitle.setText("Your property");

        propertyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list",  propertyList);

                MorePropertyFragment bottomDialogFragment = new MorePropertyFragment();
                bottomDialogFragment.setArguments(bundle);
                bottomDialogFragment.show(requireActivity().getSupportFragmentManager(), "ActionBottomDialogFragment.TAG");
            }
        });

        propertyList = new ArrayList<>();

        propertyView = propertyWrapper.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        propertyView.setHasFixedSize(true);
        propertyView.setLayoutManager(linearLayoutManager2);
        adapter = new PropertyCardAdapter(getContext(), propertyList, 5);
        propertyView.setAdapter(adapter);
        historyView.setHasFixedSize(true);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    private void loadImageFromFSUrl(String imageURL, final GetBitMapCallBack getBitMapCallBack)
    {
         FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    try {
        StorageReference httpsReference = firebaseStorage.getReferenceFromUrl(imageURL);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    getBitMapCallBack.getBitMap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
    }

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

    }

    private void showTextLong(String text)
    {
        Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}