package com.team5.besthouse.utilities;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.services.StoreService;

public class UpdateTenantLoyal {

    public UpdateTenantLoyal() {
    }

    public void increaseLoyalPointBy50(Context context, String userId)
    {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        StoreService storeService = new StoreService(context);
        int  currentLoyalPoint = storeService.getIntValue(UnchangedValues.USER_LOYAL_COL);
        fs.collection("users").document(userId).update(UnchangedValues.USER_LOYAL_COL, currentLoyalPoint + 50);
    }
}
