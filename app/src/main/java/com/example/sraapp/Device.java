package com.example.sraapp;

import android.widget.EditText;

public class Device {
    private String name;

    public Device(EditText name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
