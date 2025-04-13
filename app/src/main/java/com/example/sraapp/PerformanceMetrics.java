package com.example.sraapp;

public class PerformanceMetrics {
    private final float pdr;
    private final float averageLatency;
    private final int averagePackets;

    public PerformanceMetrics(float pdr, float averageLatency, int averagePackets) {
        this.pdr = pdr;
        this.averageLatency = averageLatency;
        this.averagePackets = averagePackets;
    }

    public float getPDR() {
        return (float) Math.round(pdr * 100) / 100;
    }

    public float getAverageLatency() {
        return (float) Math.round(averageLatency * 100) / 100;
    }

    public int getAveragePackets() {
        return averagePackets;
    }
}
