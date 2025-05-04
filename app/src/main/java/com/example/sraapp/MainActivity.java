package com.example.sraapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private DashboardFragment dashboardFragment;
    private DeviceFragment deviceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dashboardFragment = new DashboardFragment();
        deviceFragment = new DeviceFragment();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.navigation_dashboard) {
                fragment = dashboardFragment;
            } else if (item.getItemId() == R.id.navigation_device) {
                fragment = deviceFragment;
            } else {
                return false;
            }

            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
            return true;
        });

        // Set default fragment
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, dashboardFragment)
            .commit();
    }
}