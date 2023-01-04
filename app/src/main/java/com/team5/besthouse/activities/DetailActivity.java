package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Property;
import com.team5.besthouse.models.PropertyAddress;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Property property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();

        property = (Property) getIntent().getSerializableExtra("property");

        if (property == null) property = Property.STATICPROPERTY;

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        //first feature
        LinearLayout bedroom = findViewById(R.id.details_bedroom);
        ImageView featureBedroom = bedroom.findViewById(R.id.feature_image);
        featureBedroom.setImageResource(R.drawable.ic_outline_single_bed_24);
        TextView bedroomText = bedroom.findViewById(R.id.feature_text);
        bedroomText.setText(property.getBedrooms() + " Bedrooms");

        //Second feature
        LinearLayout bathroom = findViewById(R.id.details_bathroom);
        ImageView featureBathroom = bathroom.findViewById(R.id.feature_image);
        featureBathroom.setImageResource(R.drawable.ic_outline_bathtub_24);
        TextView bathroomText = bathroom.findViewById(R.id.feature_text);
        bathroomText.setText(property.getBathrooms() + " Bathrooms");

        //Last feature
        LinearLayout other = findViewById(R.id.details_other);
        ImageView featureOther = other.findViewById(R.id.feature_image);
        featureOther.setImageResource(R.drawable.ic_outline_square_foot_24);
        TextView otherText = other.findViewById(R.id.feature_text);
        otherText.setText((int) property.getArea() + "m^2 Square foot");

        TextView desc = findViewById(R.id.details_desc);

        Button newPropertyButton = findViewById(R.id.createPropertyBtn);

        TextView nameText = findViewById(R.id.details_name);

        TextView locationText = findViewById(R.id.details_address);

        //End of id get

        nameText.setText(property.getPropertyName());

        PropertyAddress address = property.getAddress();
        String location = address.getStreet() + ", " + address.getCity() + ", " + address.getWard();
        locationText.setText(location);

        newPropertyButton.setOnClickListener(v -> {
            Property property = Property.STATICPROPERTY;
            db.collection(UnchangedValues.PROPERTIES_TABLE).add(property);

            Toast.makeText(this, "New property added!", Toast.LENGTH_SHORT).show();
        });

        TextView price = findViewById(R.id.details_price);
        price.setText((int) property.getMonthlyPrice() + ".000 VND / Month");

        ImageView backBtn = findViewById(R.id.details_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}