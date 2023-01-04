package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.ActivityLoginBinding;
import com.team5.besthouse.interfaces.DirectUICallback;
import com.team5.besthouse.models.Landlord;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.UserRole;
import com.team5.besthouse.services.StoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dev.chrisbanes.insetter.Insetter;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private StoreService storeService;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        // set up store service
        storeService = new StoreService(getApplicationContext());

        database = FirebaseFirestore.getInstance();

//        Window window = getWindow();
//        window.setStatusBarColor(Color.TRANSPARENT);

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        // check if user already login
        checkAlreadyLogin();

        // handle putExtra
        handlePutExtra();
        setMoveToSignUpAction();
        setLoginAction();
    }

    private void checkAlreadyLogin()
    {

        if(storeService.containValue(UnchangedValues.IS_LOGIN_LANDLORD) && storeService.containValue(UnchangedValues.LOGIN_USER))
        {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if (storeService.containValue(UnchangedValues.IS_LOGIN_TENANT) && storeService.containValue(UnchangedValues.LOGIN_USER))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
        else
        {
            return;
        }
    }

    private void setMoveToSignUpAction()
    {
        loginBinding.signupTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handlePutExtra()
    {
        Bundle b = getIntent().getExtras();

        if(b!=null)
        {
            boolean accountCreated = false;
            try{
               accountCreated = (boolean) b.get(UnchangedValues.ACCOUNT_CREATED_INTENT);
            }catch (Exception e)
            {
               e.printStackTrace();
            }

            if(accountCreated == true)
            {
                showTextLong("Your Account Is Created");
            }

            try {
               if(b.get(UnchangedValues.LOGOUT_PERFORMED) != null)
               {
                   showTextLong("Logout Successfully");
               }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setLoginAction()
    {
        loginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin(new DirectUICallback() {
                    @Override
                    public void direct(boolean isCredentialCorrected, UserRole loginUserRole) {
                        if(isCredentialCorrected && loginUserRole != null)
                        {
                            Intent intent = null;
                            if(loginUserRole == UserRole.LANDLORD)
                            {
                                intent = new Intent(getApplicationContext(), DetailActivity.class);
                            }
                            else if (loginUserRole == UserRole.TENANT)
                            {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            showTextLong("Invalid Credential!");
                            // clear input
                            loginBinding.email.getText().clear();
                            loginBinding.password.getText().clear();
                        }
                    }
                });
            }
        });
    }

    private void validateLogin(final DirectUICallback direct)
    {
       String inputEmail = loginBinding.email.getText().toString();
       String inputPassword = loginBinding.password.getText().toString();

       if(!Pattern.compile(UnchangedValues.EMAIL_REGEX, Pattern.CASE_INSENSITIVE).matcher(inputEmail).matches())
       {
           showTextLong("Please Enter Valid Email");
           return;
       }
       if(!Pattern.matches(UnchangedValues.PASSWORD_REGEX,inputPassword))
       {
           return;
       }

        // validate credential()
       try{
          database.collection(UnchangedValues.USERS_TABLE)
                  .whereEqualTo(UnchangedValues.USER_EMAIL_COL, inputEmail)
                  .whereEqualTo(UnchangedValues.USER_PASS_COL, inputPassword).get()
                  .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                          List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                          if(docList.size() > 0 && docList.get(0).exists())
                          {
                              // retrieve user data if credential is correct
                              String userName = docList.get(0).getString(UnchangedValues.USER_NAME_COL);
                              String userEmail = docList.get(0).getString(UnchangedValues.USER_EMAIL_COL);
                              String userPhone = docList.get(0).getString(UnchangedValues.USER_PHONE_COL);
                              String userId = docList.get(0).getId();
                              User loginUser = null;
                              UserRole userRole = null;

                              if(docList.get(0).getString(UnchangedValues.USER_ROLE).compareTo("TENANT") == 0)
                              {
                                  loginUser = new Tenant(userEmail, userName, userPhone, new ArrayList<>());
                                  userRole = UserRole.TENANT;
                              }
                              if(docList.get(0).getString(UnchangedValues.USER_ROLE).compareTo("LANDLORD") == 0)
                              {
                                 loginUser = new Landlord(userEmail, userName, userPhone, new ArrayList<>(), new ArrayList<>()) ;
                                 userRole = UserRole.LANDLORD;
                              }

                              //save data to share preference
                              if(loginUser != null)
                              {
                                  try{
                                      Gson gson = new Gson();
                                      storeService.storeStringValue(UnchangedValues.LOGIN_USER, gson.toJson(loginUser).toString());
                                      storeService.storeStringValue(UnchangedValues.USER_ID_COL, userId);
                                      direct.direct(true, userRole);
                                  } catch (Exception e)
                                  {
                                     showTextLong(e.getMessage());
                                  }
                              }
                          }
                          else
                          {
                              direct.direct(false, null);
                          }
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          showTextLong(e.getMessage());
                      }
                  });
       }
       catch (Exception e)
       {
           showTextLong(e.getMessage());
       }
    }

    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}