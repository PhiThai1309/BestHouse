package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(signupBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));


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


    private void registerNewAccount(String userId)
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
           database.collection(UnchangedValues.USERS_TABLE).document(userId).set(user)
                   .addOnSuccessListener(
                           documentReference -> {
                               // move back to login with toast successful message
                               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               intent.putExtra(UnchangedValues.ACCOUNT_CREATED_INTENT, true);
                               startActivity(intent);
                               finish();
                           }
                   ).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           signupBinding.signOutButtonTextView.setText("Sign Up");
                           signupBinding.signOutButtonImageView.setVisibility(View.VISIBLE);
                           signupBinding.signOutButtonProgressBar.setVisibility(View.GONE);
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


    private void performEmailPassAuth()
    {
        signupBinding.signOutButtonTextView.setText("Creating Account...");
        signupBinding.signOutButtonImageView.setVisibility(View.GONE);
        signupBinding.signOutButtonProgressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(signupBinding.email.getText().toString(), signupBinding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            registerNewAccount(user.getUid());
                        }
                        else
                        {
                            signupBinding.signOutButtonTextView.setText("Sign Up");
                            signupBinding.signOutButtonImageView.setVisibility(View.VISIBLE);
                            signupBinding.signOutButtonProgressBar.setVisibility(View.GONE);
                           showTextLong(task.getException().getMessage());
                        }
                    }
                });
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
            return;
        }
        else if(!Pattern.matches(UnchangedValues.PHONE_REGEX, inputPhone))
        {
            showTextLong("Please Enter Valid Phone Number");
            return;
        }
        else if(!signupBinding.radioLandlord.isChecked() && !signupBinding.radioTenant.isChecked())
        {
            showTextLong("Please Select Your Role");
            return;
        }
        performEmailPassAuth();
    }


}