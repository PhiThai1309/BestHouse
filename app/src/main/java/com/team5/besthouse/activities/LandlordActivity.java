package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.team5.besthouse.R;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.AccountFragment;
import com.team5.besthouse.fragments.LandlordHomeFragment;
import com.team5.besthouse.services.StoreService;


public class LandlordActivity extends AppCompatActivity {
    ActionBar actionBar;
    private StoreService storeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord);

        //set color for navigation bar
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        actionBar = getSupportActionBar();
        NavigationBarView navigationView;
        storeService = new StoreService(getApplicationContext());

        navigationView = findViewById(R.id.landlord_bottom_navigation);
        navigationView.setOnItemSelectedListener(selectedListener);

        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        LandlordHomeFragment fragment = new LandlordHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        String a = storeService.getStringValue(UnchangedValues.LOGIN_USER);
        storeService.storeBooleanValue(UnchangedValues.IS_LOGIN_LANDLORD, true);

        showTextLong(a);

        FloatingActionButton fab = findViewById(R.id.float_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(LandlordActivity.this, AddPropertyActivity.class);
//               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
            }
        });
    }

    private void showTextLong(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.main:
                    LandlordHomeFragment fragment = new LandlordHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                case R.id.account:
                    AccountFragment fragment2 = new AccountFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    return true;
            }
            return false;
        }
    };
}