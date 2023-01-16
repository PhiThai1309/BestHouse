package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.google.android.material.elevation.SurfaceColors;
import com.team5.besthouse.R;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.Property;

public class ContractActivity extends AppCompatActivity {
    Contract contract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);


        contract = (Contract) getIntent().getSerializableExtra("contract");
    }
}