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

        LinearLayout bedroom = findViewById(R.id.details_bedroom);
        LinearLayout other = findViewById(R.id.details_other);

        ImageView featureImage = bedroom.findViewById(R.id.feature_image);
        featureImage.setImageResource(R.drawable.ic_outline_single_bed_24);

        ImageView featureOther = other.findViewById(R.id.feature_image);
        featureOther.setImageResource(R.drawable.ic_outline_done_outline_24);

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

        ImageView backBtn = findViewById(R.id.details_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}