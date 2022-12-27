package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationBarView;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.AccountFragment;
import com.team5.besthouse.fragments.HomeFragment;
import com.team5.besthouse.fragments.MapsFragment;
import com.team5.besthouse.R;
import com.team5.besthouse.services.StoreService;
import com.team5.besthouse.constants.UnchangedValues;

public class MainActivity extends AppCompatActivity {
    ActionBar actionBar;
    private StoreService storeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set color to the navigation bar to match with the bottom navigation view
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        actionBar = getSupportActionBar();
        NavigationBarView navigationView;
        storeService = new StoreService(getApplicationContext());
//        LinearLayout navbar = findViewById(R.id.main_navbar);
//
//        ImageView home = navbar.findViewById(R.id.home);
//        ImageView search = navbar.findViewById(R.id.search);
//        ImageView profile = navbar.findViewById(R.id.profile);
//
//        home.setImageResource(R.drawable.ic_round_home_24);
//        home.setColorFilter(getResources().getColor(R.color.md_theme_onPrimaryContainer));

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(selectedListener);


        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);


    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.main:
                    HomeFragment fragment = new HomeFragment();
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

                case R.id.search:
                    MapsFragment fragment3 = new MapsFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, fragment3, "");
                    fragmentTransaction3.commit();


                    return true;
            }
            return false;
        }
    };
}