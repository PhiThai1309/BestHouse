package com.team5.besthouse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.navigation.NavigationBarView;
import com.team5.besthouse.broadcastreceiverandservice.AutoLogoutService;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.fragments.AccountFragment;
import com.team5.besthouse.fragments.ChatFragment;
import com.team5.besthouse.fragments.TenantHomeFragment;
import com.team5.besthouse.fragments.MapsFragment;
import com.team5.besthouse.R;
import com.team5.besthouse.services.StoreService;

public class MainActivity extends BaseActivity {
    public static NavigationBarView navigationView;
    private StoreService storeService;

    public boolean locationPermissionGranted;
    public Location lastKnownLocation;
    protected FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeService = new StoreService(getApplicationContext());

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(selectedListener);


        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        TenantHomeFragment fragment = new TenantHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

        String a = storeService.getStringValue(UnchangedValues.LOGIN_USER);

        //mark user is login
        storeService.storeBooleanValue(UnchangedValues.IS_LOGIN_TENANT, true);

//        showTextLong(a);
    }



    private void showTextLong(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }


    private final NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.main:
                    TenantHomeFragment fragment = new TenantHomeFragment();
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

                case R.id.search:
                    MapsFragment fragment3 = new MapsFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, fragment3, "");
                    fragmentTransaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction3.commit();
                    return true;
            }
            return false;
        }
    };


}