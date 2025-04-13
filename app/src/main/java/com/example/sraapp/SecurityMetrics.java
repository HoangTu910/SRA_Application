package com.example.sraapp;

public class SecurityMetrics {
    private final int packetsRejected;
    private final int safeCounterActivated;
    private final int bruteForceDetected;

    public SecurityMetrics(int packetsRejected, int safeCounterActivated, int bruteForceDetected) {
        this.packetsRejected = packetsRejected;
        this.safeCounterActivated = safeCounterActivated;
        this.bruteForceDetected = bruteForceDetected;
    }

    public int getPacketsRejected() {
        return packetsRejected;
    }

    public int getSafeCounterActivated() {
        return safeCounterActivated;
    }

    public int getBruteForceDetected() {
        return bruteForceDetected;
    }
}
