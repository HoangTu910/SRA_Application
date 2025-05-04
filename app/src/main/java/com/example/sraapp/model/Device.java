package com.example.sraapp.model;

public class Device {
    private String name;
    private String status;

    public Device(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() { return name; }
    public String getStatus() { return status; }
}
