package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.ActivitySignupBinding;
import com.team5.besthouse.interfaces.SetEmailExistedCallback;
import com.team5.besthouse.models.Landlord;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;

import org.checkerframework.checker.initialization.qual.Initialized;

import java.util.List;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding signupBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(signupBinding.getRoot());

        setSignUpAction();
        setActionBackToLogin();
    }

    private void setSignUpAction()
    {
        signupBinding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void setActionBackToLogin()
    {
        signupBinding.loginTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    private void registerNewAccount()
    {
        User user;
        FirebaseFirestore database;

        if(signupBinding.radioLandlord.isChecked())
        {
            user = new Landlord(signupBinding.email.getText().toString(),
                    signupBinding.password.getText().toString(),
                    signupBinding.name.getText().toString(), signupBinding.phoneNumber.getText().toString());
        }
        else
        {
           user = new Tenant(signupBinding.email.getText().toString(),
                   signupBinding.password.getText().toString(),
                   signupBinding.name.getText().toString(), signupBinding.phoneNumber.getText().toString());
        }

        database = FirebaseFirestore.getInstance();

        try {
           database.collection(UnchangedValues.USERS_TABLE).add(user)
                   .addOnSuccessListener(
                           documentReference -> {
                               // move back to login with toast successful message
                               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                               intent.putExtra(UnchangedValues.ACCOUNT_CREATED_INTENT, true);
                               startActivity(intent);
                           }
                   ).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          showTextLong(e.getMessage());
                       }
                   });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void validateInput()
    {
        String inputEmail = signupBinding.email.getText().toString();
        String inputPassword = signupBinding.password.getText().toString();
        String inputName = signupBinding.name.getText().toString();
        String inputPhone = signupBinding.phoneNumber.getText().toString();

        if(!Pattern.matches(UnchangedValues.EMAIL_REGEX, inputEmail))
        {
            showTextLong("Please Enter Valid Email");
            return;
        }
        else if (!Pattern.matches(UnchangedValues.PASSWORD_REGEX, inputPassword))
        {
            showTextLong("Please Enter Valid Password");
            return;
        }
        else if (!Pattern.matches(UnchangedValues.NAME_REGEX, inputName))
        {
            showTextLong("Please Enter Valid Name");
        }
        else if(!Pattern.matches(UnchangedValues.PHONE_REGEX, inputPhone))
        {
            showTextLong("Please Enter Valid Phone Number");
        }
        else if(!signupBinding.radioLandlord.isChecked() && !signupBinding.radioTenant.isChecked())
        {
            showTextLong("Please Select Your Role");
        }

        checkUsedEmail(inputEmail, new SetEmailExistedCallback() {

            @Override
            public void onCallback(boolean existed) {
               if(existed)
               {
                   showTextLong("Email Is Used");
               }
               else
               {
                   registerNewAccount();
               }
            }
        });

    }

    private void checkUsedEmail(String inputEmail, final SetEmailExistedCallback callback)
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference docRef = database.collection(UnchangedValues.USERS_TABLE);

        try{
            docRef.whereEqualTo(UnchangedValues.USER_EMAIL_COL, signupBinding.email.getText().toString()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                            // if email is created
                            if (docList.size() > 0 && docList.get(0).exists())
                            {
                                callback.onCallback(true);
                            }
                            else
                            {
                                callback.onCallback(false);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}