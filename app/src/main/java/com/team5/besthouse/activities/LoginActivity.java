package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
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
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        // set up store service
        storeService = new StoreService(getApplicationContext());

        database = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(UnchangedValues.WEB_CLIENT_DEFAULT_ID).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        firebaseAuth = FirebaseAuth.getInstance();
//        Window window = getWindow();
//        window.setStatusBarColor(Color.TRANSPARENT);

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        // check if user already login
        checkAlreadyLogin();

        // handle putExtra
        handlePutExtra();
        setMoveToSignUpAction();
        setLoginAction();
        setGoogleLoginBtn();
    }

    private void checkAlreadyLogin()
    {

        if(storeService.containValue(UnchangedValues.IS_LOGIN_LANDLORD) && storeService.containValue(UnchangedValues.LOGIN_USER))
        {
            Intent intent = new Intent(getApplicationContext(), LandlordActivity.class);
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

    private void setLoginAction(){
        loginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateLogin()){
                    performEmailPassAuth();
                }
            }
        });
    }

    private void performGetDataFromFS(String userId, final DirectUICallback direct) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        try {
            database.collection(UnchangedValues.USERS_TABLE)
                    .document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String userName = documentSnapshot.getString(UnchangedValues.USER_NAME_COL);
                            String userEmail = documentSnapshot.getString(UnchangedValues.USER_EMAIL_COL);
                            String userPhone = documentSnapshot.getString(UnchangedValues.USER_PHONE_COL);
                            String userId = documentSnapshot.getId();
                            User loginUser = null;
                            UserRole userRole = null;
                            try {
                                if (documentSnapshot.getString(UnchangedValues.USER_ROLE).compareTo("TENANT") == 0) {
                                    loginUser = new Tenant(userEmail, userName, userPhone, new ArrayList<>());
                                    userRole = UserRole.TENANT;
                                }
                                if (documentSnapshot.getString(UnchangedValues.USER_ROLE).compareTo("LANDLORD") == 0) {
                                    loginUser = new Landlord(userEmail, userName, userPhone, new ArrayList<>(), new ArrayList<>());
                                    userRole = UserRole.LANDLORD;
                                }
                            } catch (Exception e) {
                            }
                            //save data to share preference
                            if (loginUser != null) {
                                try {
                                    Gson gson = new Gson();
                                    storeService.storeStringValue(UnchangedValues.LOGIN_USER, gson.toJson(loginUser).toString());
                                    storeService.storeStringValue(UnchangedValues.USER_ID_COL, userId);
                                    direct.direct(true, userRole);
                                } catch (Exception e) {
                                    showTextLong(e.getMessage());
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            direct.direct(false, null);
                            showTextLong(e.getMessage());
                        }
                    });
        } catch (Exception e) {
            showTextLong(e.getMessage());
        }
    }

    private void performEmailPassAuth(){
        loginBinding.signInButtonTextView.setText("Validating Credential...");
        loginBinding.signInButtonImageView.setVisibility(View.GONE);
        loginBinding.signInButtonProgressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(loginBinding.email.getText().toString(), loginBinding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            performGetDataFromFS(user.getUid(), new DirectUICallback() {
                                @Override
                                public void direct(boolean isCredentialCorrected, UserRole loginUserRole) {
                                    onDirect(isCredentialCorrected,loginUserRole);
                                }
                            });
                        }
                        else
                        {
                            loginBinding.signInButtonTextView.setText("Login");
                            loginBinding.signInButtonImageView.setVisibility(View.VISIBLE);
                            loginBinding.signInButtonProgressBar.setVisibility(View.GONE);
                            showTextLong(task.getException().getMessage());
                        }
                    }
                });
    }

    private boolean validateLogin()
    {
       String inputEmail = loginBinding.email.getText().toString();
       String inputPassword = loginBinding.password.getText().toString();

       if(!Pattern.compile(UnchangedValues.EMAIL_REGEX, Pattern.CASE_INSENSITIVE).matcher(inputEmail).matches())
       {
           showTextLong("Please Enter Valid Email");
           return  false;
       }
       if(!Pattern.matches(UnchangedValues.PASSWORD_REGEX,inputPassword))
       {
           return false;
       }
        return true;
    }

    private void setGoogleLoginBtn()
    {
        loginBinding.signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) // handler login credential from google login activity
        {
            Log.d("NEW_USER", "in");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
               // credential is correct. perform the auth in fireauth
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                performGoogleAuth (account);
            }catch (Exception e)
            {
                Log.d("NEW_USER", e.toString());
            }

        }
    }

    /**
     * this method is learned from https://www.youtube.com/watch?v=gD9uQf5UU-g
     * @param account
     */
    private void performGoogleAuth(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        // check if user is new or existing
                        if(authResult.getAdditionalUserInfo().isNewUser())
                        {
                            //user is new
                            Log.d("NEW_USER", authResult.getUser().getEmail());
                            Log.d("NEW_USER", authResult.getUser().getUid());
                            onDirect(authResult.getUser());
                        }
                        else
                        {
                            Log.d("NEW_USER", authResult.getUser().getEmail());
                            Log.d("NEW_USER", authResult.getUser().getUid());
                            performGetDataFromFS(user.getUid(), new DirectUICallback() {
                                @Override
                                public void direct(boolean isCredentialCorrected, UserRole loginUserRole) {
                                    onDirect(isCredentialCorrected, loginUserRole);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("NEW_USER", e.getMessage());
                    }
                });
    }

    /**
     *
     */
    private void onDirect(FirebaseUser thirdPartyLoginUser)
    {
       if (thirdPartyLoginUser != null)
       {
          Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
          intent.putExtra("email", thirdPartyLoginUser.getEmail());
          intent.putExtra("id", thirdPartyLoginUser.getUid());
          intent.putExtra("name", thirdPartyLoginUser.getDisplayName());
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);
       }
    }

    private void onDirect(boolean isCredentialCorrected, UserRole loginUserRole)
    {
        if(isCredentialCorrected && loginUserRole != null)
        {
            Intent intent = null;
            if(loginUserRole == UserRole.LANDLORD)
            {
                intent = new Intent(getApplicationContext(), LandlordActivity.class);
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

    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}