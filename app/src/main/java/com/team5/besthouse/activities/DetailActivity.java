package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.team5.besthouse.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        LinearLayout bedroom = findViewById(R.id.details_bedroom);
        LinearLayout other = findViewById(R.id.details_other);

        ImageView featureImage = bedroom.findViewById(R.id.feature_image);
        featureImage.setImageResource(R.drawable.ic_outline_single_bed_24);

        ImageView featureOther = other.findViewById(R.id.feature_image);
        featureOther.setImageResource(R.drawable.ic_outline_done_outline_24);
    }
}