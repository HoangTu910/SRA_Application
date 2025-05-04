package com.example.sraapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sraapp.DeviceAdapter;
import com.example.sraapp.Device;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DeviceActivity extends AppCompatActivity {
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.deviceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup Add Device Button
        findViewById(R.id.addDeviceButton).setOnClickListener(v -> showAddDeviceDialog());

        // Setup Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_device);
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void showAddDeviceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_device, null);
        EditText nameInput = dialogView.findViewById(R.id.deviceNameInput);

        new AlertDialog.Builder(this)
            .setTitle("Add New Device")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String name = nameInput.getText().toString();
                if (!name.isEmpty()) {
                    deviceAdapter.addDevice(new Device(nameInput));
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
