package com.team5.besthouse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team5.besthouse.R;
import com.team5.besthouse.activities.ContractActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.TextMessage;
import com.team5.besthouse.models.User;

import java.util.List;

public class ContractReviewAdapter extends RecyclerView.Adapter<ContractReviewAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Contract> contractList;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Constructor
    public ContractReviewAdapter(Context context, List<Contract> Chats) {
        mInflater = LayoutInflater.from(context);
        contractList = Chats;
    }

    // Create the view holder
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view
        View itemView = mInflater.inflate(R.layout.layout_card_main_chat, parent, false);
        return new TaskViewHolder(itemView);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //if task is not null
        if (contractList != null) {
            // Get the task at the position
            Contract current = contractList.get(position);
            // Set the name of the view holder

            User tenant = database.collection(UnchangedValues.USERS_TABLE)
                    .whereEqualTo(UnchangedValues.USER_EMAIL_COL, current.getTenantEmail())
                    .limit(1)
                    .get().getResult().getDocuments().get(0).toObject(User.class);

            if (tenant != null) {
                holder.name.setText(tenant.getFullName());
            }
            holder.startDate.setText(current.getStartDate().toDate().toString());
            holder.endDate.setText(current.getEndDate().toDate().toString());

            holder.cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(mInflater.getContext(), ContractActivity.class);
                    intent.putExtra("contract", current);
                    mInflater.getContext().startActivity(intent);
            });

            holder.accept.setOnClickListener(v -> {
                current.setContractStatus(ContractStatus.ACTIVE);
                database.collection(UnchangedValues.CONTRACTS_TABLE).document(current.getId()).set(current)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                contractList.remove(current);
                                for (Contract contract : contractList) {
                                    contract.setContractStatus(ContractStatus.REJECT);
                                    database.collection(UnchangedValues.CONTRACTS_TABLE).document(contract.getId()).set(contract);
                                }
                            }
                            Toast.makeText(mInflater.getContext(), "Contract Accepted!", Toast.LENGTH_LONG).show();
                        });
            });
        }
    }

    // Return the size of the data set
    @Override
    public int getItemCount() {
        if(contractList.size() == 0){
            return 0;
        }
        return contractList.size();
    }

    //TaskViewHolder class to hold the views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView startDate;
        TextView endDate;
        Button accept;
        LinearLayout cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.property_name);
            startDate = itemView.findViewById(R.id.from_date);
            endDate = itemView.findViewById(R.id.to_date);
            accept = itemView.findViewById(R.id.button);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
