package com.team5.besthouse.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.AddPropertyActivity;
import com.team5.besthouse.activities.LandlordActivity;
import com.team5.besthouse.adapters.LandlordPropertyAdapter;
import com.team5.besthouse.adapters.PropertyAdapter;
import com.team5.besthouse.adapters.PropertyCardAdapter;
import com.team5.besthouse.constants.UnchangedValues;

import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.StoreService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandlordHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandlordHomeFragment extends Fragment {
    private RecyclerView postedPropertyView;
    private ArrayList<Property> list;
    private LandlordPropertyAdapter landlordAdapter;
    private ImageView uerImageView;
    private Context context;
    private RecyclerView listingView;
    private PropertyCardAdapter adapter2;
    private StoreService storeService;
    private FirebaseAuth firebaseAuth;
    private View progressIndicator;

    FirebaseFirestore db;
    User user;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView homeAccount;

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

        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        context = inflater.getContext();

        progressIndicator = view.findViewById(R.id.home_progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        homeAccount = view.findViewById(R.id.landlord_account);
        homeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new AccountFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content, frag);
                LandlordActivity.navigationView.setSelectedItemId(R.id.account);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        //get db instance
        db = FirebaseFirestore.getInstance();

        //get login user image and name
        firebaseAuth = FirebaseAuth.getInstance();

        // set up store service
        storeService = new StoreService(context);

        // load user profile image
        if(storeService.getStringValue(UnchangedValues.USER_IMAGE_URL_COL) != null)
        {
            Log.d("IMAGE", "onCreateView: " + storeService.getStringValue(UnchangedValues.USER_IMAGE_URL_COL));
            loadImageFromFSUrl(storeService.getStringValue(UnchangedValues.USER_IMAGE_URL_COL));
        }

        Gson gson = new Gson();
        user = gson.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Tenant.class);

        TextView name = view.findViewById(R.id.landlord_name);
        name.setText(user.getFullName());


        //Get the recycler view and
        listingView = view.findViewById(R.id.posted_property);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        listingView.setLayoutManager(linearLayoutManager);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(listingView);

        list = new ArrayList<>();
        adapter2 = new PropertyCardAdapter(inflater.getContext(), list, false);
        listingView.setAdapter(adapter2);

        //Floating action button configure
        ExtendedFloatingActionButton floatBtn = view.findViewById(R.id.float_button);
        listingView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 10)
                    floatBtn.shrink();
                else if (dy < 0)
                    floatBtn.extend();
            }
        });

        floatBtn.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(UnchangedValues.PROPERTIES_TABLE)
                .whereEqualTo(UnchangedValues.LANDLORD_EMAIL_COL, user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        if(error != null)
                        {
                            Log.w("ERROR", error);
                            return;
                        }
                        assert value != null;

                        adapter2.notifyDataSetChanged();

                        for(DocumentChange newDoc : value.getDocumentChanges()){
                            Property p = newDoc.getDocument().toObject(Property.class);

                            Log.i("Property", newDoc.getType().toString());
                            int i = list.indexOf(p);
                            if (i >= 0) {
                                Log.i("Property", "Property already exists, removing");
                                list.remove(p);
                                adapter2.notifyItemRemoved(list.indexOf(p));
                            }
                            if(newDoc.getType() != DocumentChange.Type.REMOVED){
                                list.add(p);
                                Log.i("Property", p.toString());
                                adapter2.notifyItemInserted(list.indexOf(p));
                            }
                        }
                        progressIndicator.setVisibility(View.GONE);
                    }
                });
    }

    private void loadImageFromFSUrl(String imageURL)
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        try {
            StorageReference httpsReference = firebaseStorage.getReferenceFromUrl(imageURL);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                   Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                   homeAccount.setImageBitmap(bitmap);
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



}