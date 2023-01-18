package com.team5.besthouse.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.databinding.ActivitySignupBinding;
import com.team5.besthouse.interfaces.SetEmailExistedCallback;
import com.team5.besthouse.interfaces.SetReceiveImageURLCallBack;
import com.team5.besthouse.models.Landlord;
import com.team5.besthouse.models.Tenant;
import com.team5.besthouse.models.User;

import org.checkerframework.checker.initialization.qual.Initialized;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity {

    private ActivitySignupBinding signupBinding;
    private FirebaseAuth firebaseAuth;
    private String thirdPartyLoginEmail;
    private String thirdPartyLoginId;
    private String thirdPartyLoginName;
    private Bitmap selectedProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(signupBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();


        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        Intent intent = getIntent();
        if(intent.getExtras()!=null)
        {
            thirdPartyLoginId = intent.getStringExtra("id");
            thirdPartyLoginEmail = intent.getStringExtra("email");
            thirdPartyLoginName = intent.getStringExtra("name");
            setProvideAdditionDataView(thirdPartyLoginEmail, thirdPartyLoginName);

        }

            setSignUpAction();

        setActionBackToLogin();
        seActionSelectProfileImage();
    }

    /**
     * learn from https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
     */
    private void seActionSelectProfileImage(){
        signupBinding.selectImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*") ;
                intent.setAction(Intent.ACTION_GET_CONTENT);
                processSelectImage.launch(intent);

            }
        });
    }

    /**
     * learn from https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
     */
    private ActivityResultLauncher<Intent> processSelectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if(result.getResultCode() == Activity.RESULT_OK)
                {
                    Intent data = result.getData();
                    // process the image
                    if(data != null && data.getData() != null)
                    {
                        Uri selectedProfileImage = data.getData();
                        Bitmap imageBitMap;
                        try {
                            imageBitMap = convertUriToBitmap(selectedProfileImage);
                            // load the image to the UI
                            signupBinding.avatarViewImage.setImageBitmap(imageBitMap);
                            signupBinding.textSelectImage.setVisibility(View.GONE);
                            this.selectedProfileImage = imageBitMap;
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            } );

    private ArrayList<String> uploadImageToFireStorage(final SetReceiveImageURLCallBack callBack)
    {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        ArrayList<String> imageURL = new ArrayList<>();

            Bitmap bitmap = selectedProfileImage;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            if(bitmap != null) {
                StorageReference storageRef =  storage.getReference("users/").child(System.currentTimeMillis()+"_"+FirebaseAuth.getInstance().getCurrentUser().getUid()+ ".JPEG");
                storageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                           taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {

                                   if(uri != null)
                                   {
                                       imageURL.add(uri.toString());
                                       callBack.onCallback(imageURL); ;
                                   }
                               }
                           });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showTextLong(e.getMessage());
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        signupBinding.signOutButtonProgressBar.setVisibility(View.VISIBLE);
                        signupBinding.signOutButtonTextView.setText("Uploading Data...");
                    }
                });
            }
        return imageURL;
    }

    private Bitmap convertUriToBitmap(Uri inputUriImage)
    {
        try {
            Bitmap imageBitmap  = MediaStore.Images.Media.getBitmap(this.getContentResolver(),inputUriImage );
            // resize the image

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap,200 , 200, false );
            return  imageBitmap;
        } catch (IOException e) {
            Log.d("NULL_BITMAP", e.getMessage() + "");
        }
        return null;
    }

    private void setProvideAdditionDataView(String providedEmail, String providedName)
    {
       signupBinding.signupTitle.setText("Please Provided More Info");
       signupBinding.signOutButtonTextView.setText("Confirm");
        Uri avatarImage = firebaseAuth.getCurrentUser().getPhotoUrl();
       if(avatarImage != null)
       {
           try {
               Thread thread = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       selectedProfileImage = getBitmapFromURL(avatarImage.toString());
                   }
               });
               thread.start();
               thread.join();
           } catch (InterruptedException e) {
               e.printStackTrace();
               return;
           }

           //display provided user image
           if(this.selectedProfileImage != null)
           {
               signupBinding.avatarViewImage.setImageBitmap(this.selectedProfileImage);
               signupBinding.textSelectImage.setVisibility(View.GONE);
           }
           else
           {
               Log.d("NULL_BITMAP", "null");
           }


       }

       //display providedName
       signupBinding.name.setText(providedName);
       signupBinding.name.setKeyListener(null);
       //display provided Email
        signupBinding.email.setText(providedEmail);
        signupBinding.email.setKeyListener(null);
        //disable pass word edit
        signupBinding.linearSignup.getChildAt(4).setVisibility(View.GONE);
    }

    private void setSignUpAction()
    {
        signupBinding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    validateInput();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void setActionBackToLogin()
    {

        signupBinding.loginTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempDisconnectGoogleAccount();
                tempDisconnectFacebookAccount();
                deleteFireAuthUser();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void deleteFireAuthUser()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ACCOUNT DELETE", task.toString());
                            }
                        }
                    });
        }
    }


    private void registerNewAccount(String userId, String imageUrl)
    {
        User user;
        FirebaseFirestore database;

        if(signupBinding.radioLandlord.isChecked())
        {
            user = new Landlord(signupBinding.email.getText().toString(),
                    signupBinding.password.getText().toString(),
                    signupBinding.name.getText().toString(), signupBinding.phoneNumber.getText().toString(),imageUrl);
        }
        else
        {
           user = new Tenant(signupBinding.email.getText().toString(),
                   signupBinding.password.getText().toString(),
                   signupBinding.name.getText().toString(), signupBinding.phoneNumber.getText().toString(),imageUrl);
        }

        database = FirebaseFirestore.getInstance();

        try {
           database.collection(UnchangedValues.USERS_TABLE).document(userId).set(user)
                   .addOnSuccessListener(
                           documentReference -> {
                               // move back to login with toast successful message
                               if(thirdPartyLoginId == null)
                               {
                                   Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   intent.putExtra(UnchangedValues.ACCOUNT_CREATED_INTENT, true);
                                   startActivity(intent);
                               }
                               else {
                                   Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                   intent.putExtra("userid", thirdPartyLoginId);
                                   setResult(RESULT_OK, intent);
                                   finish();
                               }


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


    private void performEmailPassAuth(String imageURL)
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
                            registerNewAccount(user.getUid(), imageURL);
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

    private void validateInput() throws InterruptedException {
        String inputEmail = signupBinding.email.getText().toString();
        String inputPassword = signupBinding.password.getText().toString();
        String inputName = signupBinding.name.getText().toString();
        String inputPhone = signupBinding.phoneNumber.getText().toString();
        if(selectedProfileImage == null)
        {
            showTextLong("Please Upload Profile Image");
            return;
        }
        else if(!Pattern.matches(UnchangedValues.EMAIL_REGEX, inputEmail) && thirdPartyLoginId == null)
        {
            showTextLong("Please Enter Valid Email");
            return;
        }
        else if (!Pattern.matches(UnchangedValues.PASSWORD_REGEX, inputPassword) && thirdPartyLoginId == null)
        {
            showTextLong("Please Enter Valid Password");
            return;
        }
        else if (!Pattern.matches(UnchangedValues.NAME_REGEX, inputName) && thirdPartyLoginId == null)
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
        uploadImageToFireStorage(new SetReceiveImageURLCallBack() {
            @Override
            public void onCallback(List<String> imageURLs) {
                if(thirdPartyLoginId == null && imageURLs.size() > 0)
                {
                    performEmailPassAuth(imageURLs.get(0));
                }
                else if(thirdPartyLoginId != null && imageURLs.size() > 0)
                {
                    registerNewAccount(thirdPartyLoginId, imageURLs.get(0));
                }
                else
                {
                    showTextLong("Upload data error. Unable to register account.");
                    firebaseAuth.signOut();
                    tempDisconnectFacebookAccount();
                    tempDisconnectFacebookAccount();
                }
            }
        });
    }

    private void tempDisconnectGoogleAccount() {
        GoogleSignInClient mGoogleSignInAccount = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN) ;
        if(mGoogleSignInAccount != null)
        {
            mGoogleSignInAccount.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }

    }

    private void tempDisconnectFacebookAccount() {
        LoginManager.getInstance().logOut();

    }

    /**
     * learn from https://stackoverflow.com/questions/8992964/android-load-from-url-to-bitmap/8993175#8993175
     * @param src
     * @return
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}