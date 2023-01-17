package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.LruCacheKt;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.adapters.LocationSuggestionAdapter;
import com.team5.besthouse.adapters.PropertyImageInsertAdapter;
import com.team5.besthouse.adapters.PropertyTypeSelectAdapter;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.interfaces.RecyclerViewInterface;
import com.team5.besthouse.interfaces.SetReceiveImageURLCallBack;
import com.team5.besthouse.models.Coordinates;
import com.team5.besthouse.models.Landlord;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyAddress;
import com.team5.besthouse.models.PropertyStatus;
import com.team5.besthouse.models.PropertyType;
import com.team5.besthouse.models.User;
import com.team5.besthouse.models.Utilities;
import com.team5.besthouse.services.StoreService;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddPropertyActivity extends AppCompatActivity implements RecyclerViewInterface{

    private ImageButton returnButton;
    private EditText pAddressEditText;
    private EditText pnameEditText, priceEditText, pdescEditText;
    private EditText pBedRoomEditText, pBathRoomEditText, pAreaEditText;
    private Button submitButton;
    private ProgressBar progressBar;

    private Spinner ptypeSpinner;
    private PropertyType selectPropertyType = PropertyType.HOUSE;
    private int currentPropertyImagePosition = -1;
    private PropertyImageInsertAdapter piiAdapter;
    private ArrayList<Bitmap> propertyImageList;
    private CheckBox checkBoxElectric, checkBoxWater, checkBoxInternet, checkBoxGas;
    private StoreService storeService;
    private Landlord loginLandlord;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        // set storage service
        storeService = new StoreService(getApplicationContext());
        Gson json = new Gson();
        loginLandlord= json.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), Landlord.class );
        //Set hint for adding property name textbox
        View pname = findViewById(R.id.property_name);
        pnameEditText = (EditText) pname.findViewById(R.id.box);
        pnameEditText.setHint("Property Name:");

        //Set hint for adding property type textbox
        View ptype = findViewById(R.id.property_type);
        ptypeSpinner = (Spinner) ptype.findViewById(R.id.box);

        //Set hint for adding property address textbox
        View pAddress = findViewById(R.id.property_address);
        pAddressEditText = (EditText) pAddress.findViewById(R.id.box);
        pAddressEditText.setHint("Address:");

        View pBedRoomEditTextView = findViewById(R.id.bedroomQuantity);
        pBedRoomEditText = (EditText) pBedRoomEditTextView.findViewById(R.id.box);

        View pBathRoomEditTextView = findViewById(R.id.bathroomQuantity);
         pBathRoomEditText= (EditText) pBathRoomEditTextView.findViewById(R.id.box);

        pAreaEditText = findViewById(R.id.propertyArea).findViewById(R.id.box);

        //Set hint for adding property price textbox
        View price = findViewById(R.id.property_price);
        priceEditText = (EditText) price.findViewById(R.id.box);
        priceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceEditText.setHint("Monthly Price:");

        //Set hint for add property description text box
       View description = findViewById(R.id.property_description);
       pdescEditText = description.findViewById(R.id.box);
       pdescEditText.setHint("Describe the Property in Detail");

       // set the submit button
        View submitButtonHolder = findViewById(R.id.progress_button);
        submitButton = submitButtonHolder.findViewById(R.id.button);

        //progress bar
        progressBar = findViewById(R.id.progress_button).findViewById(R.id.progress_bar);

        // set checkbox config
        checkBoxElectric = findViewById(R.id.electric_option_checkbox);
        checkBoxWater = findViewById(R.id.water_option_checkbox);
        checkBoxInternet = findViewById(R.id.internet_option_checkbox);
        checkBoxGas = findViewById(R.id.gas_option_checkbox);


        //config the return button
        returnButton = findViewById(R.id.returnBar).findViewById(R.id.returnButton);
        setReturnButtonAction();
        setAddAddressAction();
        initializeSpinner();
        setSpinSelectAction();
        settleRecyclerView();
        setSubmitButton();


    }


    private void initializeSpinner()
    {
        ArrayList<PropertyType> pList = new ArrayList<>();
        pList.add(PropertyType.APARTMENT);
        pList.add(PropertyType.HOUSE);
        pList.add(PropertyType.FLOOR);
        pList.add(PropertyType.ROOM);
        ptypeSpinner.setAdapter(new PropertyTypeSelectAdapter(this, pList));
    }

    /**
     *
     */
    private void settleRecyclerView() {
        try {
            propertyImageList = new ArrayList<>();
            Bitmap addIconBitmap = convertVectorDrawableToBitmap (R.drawable.ic_baseline_add_35);

            propertyImageList.add(addIconBitmap) ;
            piiAdapter = new PropertyImageInsertAdapter(this, propertyImageList, this);
            LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            RecyclerView rv = findViewById(R.id.property_image_select_recycler_view);
            rv.setLayoutManager(lm);
            rv.setAdapter(piiAdapter);
            rv.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            Log.i("ERROR", "settleRecyclerView: " + e.getMessage());
        }

    }

        private void setSpinSelectAction()
    {
        ptypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               selectPropertyType = (PropertyType) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectPropertyType = null;
            }
        });
    }

    private void setReturnButtonAction()
    {
        returnButton.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(), LandlordActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void setAddAddressAction()
    {
        pAddressEditText.setOnClickListener(v ->{
            Intent i = new Intent(getApplicationContext(), LandLordMapsActivity.class);
            i.putExtra(UnchangedValues.ACTIVITY_REQUEST_CODE, 100);
            startActivityForResult(i, 100);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK)
        {
           if(data.getExtras().get(UnchangedValues.LOCATION_ADDRESS) != null)
           {
                String pAddress = data.getExtras().get(UnchangedValues.LOCATION_ADDRESS).toString();
                pAddressEditText.setText(pAddress);
           }
        }
        else if(requestCode == 200 && resultCode == RESULT_OK)
        {
            final Uri imageUri = data.getData();

            Bitmap bitmap = convertUriToBitmap(imageUri);
           if(!piiAdapter.addNewItem(bitmap))
           {
              showTextLong("Add Image Unsuccessfully. Maximum is 3 images");
           }
        }
        else if(requestCode == 201 && resultCode == RESULT_OK)
        {
            final Uri imageUri = data.getData();
            Bitmap bitmap = convertUriToBitmap(imageUri);
            if(this.currentPropertyImagePosition != -1)
            {
                piiAdapter.replaceItem(bitmap, currentPropertyImagePosition);
            }

        }
    }

    private Bitmap convertUriToBitmap(Uri inputUriImage)
    {
        try {
            Bitmap imageBitmap  = MediaStore.Images.Media.getBitmap(this.getContentResolver(),inputUriImage );
            // resize the image

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap,200 , 200, false );
            return  imageBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * learn from https://www.geeksforgeeks.org/how-to-convert-a-vector-to-bitmap-in-android/
     * @param id
     * @return
     */
    private Bitmap convertVectorDrawableToBitmap(int id)
    {
        Drawable drawable = ContextCompat.getDrawable(this, id);
        Bitmap bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private ArrayList<String> uploadImageToFireStorage(final SetReceiveImageURLCallBack callBack)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ArrayList<String> imageURL = new ArrayList<>();
        int count = 0;
        for(Bitmap bitmap : propertyImageList)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            if(bitmap != null && count > 0) {
                StorageReference storageRef =  storage.getReference("images/").child(System.currentTimeMillis()+"_"+"currentuserid"+".JPEG");
                storageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageURL.add(taskSnapshot.getStorage().getDownloadUrl().toString());
                        if(imageURL.size() >= 3)
                        {
                           callBack.onCallback(imageURL); ;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showTextLong(e.getMessage());
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressBar.setVisibility(View.VISIBLE);
                        submitButton.setText("Uploading Data...");
                    }
                });
            }
            else {
                count++;
            }
        }
        return imageURL;
    }


    private void setSubmitButton()
    {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateInputs())
                {
                   return;
                }
               uploadImageToFireStorage(new SetReceiveImageURLCallBack() {
                    @Override
                    public void onCallback(List<String> imageURLs) {
                        if(imageURLs.size() > 0)
                        {
                            Property newProperty = createNewProperty((ArrayList<String>) imageURLs, getSelectUtilities());
                            newProperty.setStatus(PropertyStatus.AVAILABLE);
                            if(newProperty != null)
                            {
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                try {
                                    HashMap<String,String> hash = new HashMap<>();
                                    hash.put("aaa", "bbbjhjA");
                                    DocumentReference dc = firestore.collection(UnchangedValues.PROPERTY_TABLE).document();
                                    newProperty.setId(dc.getId());
                                    dc.set(newProperty)
                                        .addOnSuccessListener(
                                                documentReference -> {
                                                    // display successful message
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setText("SUBMIT");

                                                    // display successful message
                                                    showTextLong("New Property is Added");
                                                }
                                        )
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                submitButton.setText("SUBMIT");
                                                showTextLong(e.getMessage());
                                            }
                                        });
                                }
                                catch (Exception e)
                                {
                                   showTextLong(e.getMessage());
                                }

                            }
                        }
                        else
                        {
                            showTextLong("Added Property fail because of. Upload Image Fail");
                        }
                    }
                });


            }
        });
    }

    private void clearInput()
    {
       pnameEditText.setText("");
       pAddressEditText.setText("");
       pBathRoomEditText.setText("");
       pBedRoomEditText.setText("");
       pdescEditText.setText("");
       priceEditText.setText("");
       pAreaEditText.setText("");
       checkBoxInternet.setChecked(false);
       checkBoxGas.setChecked(false);
       checkBoxWater.setChecked(false);
       checkBoxElectric.setChecked(false);
       piiAdapter.clearALLItem();

    }

    private List<Utilities> getSelectUtilities()
    {

        List<Utilities> utilities = new ArrayList<>();
        if(checkBoxElectric.isChecked())
        {
           utilities.add(Utilities.ELECTRIC) ;
        }
        if(checkBoxWater.isChecked())
        {
           utilities.add(Utilities.WATER);
        }
        if(checkBoxInternet.isChecked())
        {
           utilities.add(Utilities.INTERNET);
        }
        if(checkBoxGas.isChecked())
        {
           utilities.add(Utilities.GAS);
        }
        return utilities;
    }

    private Property createNewProperty(ArrayList<String> imageURLList, List<Utilities> utilityList)
    {
       Geocoder geocoder = new Geocoder(this) ;
        try {
            List<Address> alist = geocoder.getFromLocationName(pAddressEditText.getText().toString(), 1);
            List<Utilities> listU = new ArrayList<>();
            listU.add(Utilities.ELECTRIC);
            listU.add(Utilities.INTERNET);
            PropertyAddress padress = new PropertyAddress();
            padress.setStreet(alist.get(0).getAddressLine(0));
            padress.setCoordinates(new Coordinates(alist.get(0).getLatitude(), alist.get(0).getLongitude(), 17));

            Property property = new Property(null, pnameEditText.getText().toString(),loginLandlord.getEmail()
                    ,padress, selectPropertyType,  Integer.parseInt(pBedRoomEditText.getText().toString()),
                    Integer.parseInt(pBathRoomEditText.getText().toString()), listU,
                    Float.parseFloat(priceEditText.getText().toString()), Float.parseFloat(pAreaEditText.getText().toString()));
            property.setPropertyDescription(pdescEditText.getText().toString());
            property.setImageURLList(imageURLList) ;
            return property;
        } catch (IOException e) {
            Log.e("ERRRROORRR", "createNewProperty: " + e.getMessage() );
        }

   return null;
    }

    private boolean validateInputs()
    {
        if(pnameEditText.getText().toString().trim().isEmpty())
        {
            showTextLong("Please Enter a valid property Name");
            return false;
        }
        else if(selectPropertyType == null)
        {
            showTextLong("Please Select the property type");
            return false;
        }
        else if (pAddressEditText.getText().toString().trim().isEmpty())
        {
            showTextLong("Please Select property address");
        }
        else if(getSelectUtilities().isEmpty())
        {
            showTextLong("Please select property's utilities");
            return false;
        }
        else if(pBedRoomEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid bedroom number");
            return false;
        }
        else if(pBathRoomEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid bathroom number");
            return false;
        }
        else if(pAreaEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid property area");
        }
        else if(priceEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter valid property price");
            return false;
        }
        else if(propertyImageList.size() < 3)
        {
            showTextLong("Please upload 3 images");
            return false;
        }
        else if(pdescEditText.getText().toString().isEmpty())
        {
            showTextLong("Please enter property description");
            return false;
        }

        else
        {
            return true;
        }
       return false;
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        if(position == 0) // add image
        {
            startActivityForResult(i, 200);

        }
        else
        {
            currentPropertyImagePosition = position;
            startActivityForResult(i, 201);
        }
    }
}