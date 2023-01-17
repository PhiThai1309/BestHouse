package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.team5.besthouse.fragments.ChatFragment;
import com.team5.besthouse.fragments.LandlordHomeFragment;
import com.team5.besthouse.services.StoreService;


public class LandlordActivity extends BaseActivity {
    ActionBar actionBar;
    public static NavigationBarView navigationView;
    private StoreService storeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord);

        //set color for navigation bar
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

//        actionBar = getSupportActionBar();
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
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                    return true;

                case R.id.account:
                    AccountFragment fragment2 = new AccountFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction2.commit();
                    return true;

                case R.id.chat:
                    ChatFragment fragment4 = new ChatFragment();
                    FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.content, fragment4, "");
                    fragmentTransaction4.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction4.commit();
                    return true;
            }
            return false;
        }
    };
}