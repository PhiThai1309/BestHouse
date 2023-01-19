package com.team5.besthouse.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.ChatAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.FragmentChatBinding;
import com.team5.besthouse.models.Chat;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentChatBinding binding;
    private StoreService storeService;
    private ProgressBar progressIndicator;
    private RecyclerView chatView;
    private ArrayList<Chat> listChats;
    private ChatAdapter adapter1;

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        storeService = new StoreService(inflater.getContext());

        //Set color to the navigation bar to match with the bottom navigation view
        getActivity().getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
//
        progressIndicator = binding.getRoot().findViewById(R.id.account_progressBar);
        progressIndicator.setVisibility(View.VISIBLE);

        chatView = binding.getRoot().findViewById(R.id.chat_recycler_view);

        listChats = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        //Set the layout manager
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        chatView.setLayoutManager(linearLayoutManager);

        adapter1 = new ChatAdapter(getContext(), listChats);
        chatView.setAdapter(adapter1);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

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
    }

    EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            progressIndicator.setVisibility(View.VISIBLE);
            if(error != null)
            {
                Log.w("ERROR: ", error);
                return;
            }
            assert value != null;
            for(DocumentChange newDoc : value.getDocumentChanges()){
                Contract contract = newDoc.getDocument().toObject(Contract.class);

                listChats.removeIf(chat -> chat.getContractId().equals(contract.getId()));

                adapter1.notifyDataSetChanged();

                if (newDoc.getType() != DocumentChange.Type.REMOVED){
                    database.collection(UnchangedValues.CHATS_TABLE)
                            .whereEqualTo(UnchangedValues.CONTRACTS_ID_COL, contract.getId())
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Chat chat = document.toObject(Chat.class);
                                        listChats.add(chat);
                                        adapter1.notifyItemInserted(listChats.indexOf(chat));
                                    }
                                    progressIndicator.setVisibility(View.GONE);
                                }
                            });
                }
            }
            progressIndicator.setVisibility(View.GONE);
        }
    };

}