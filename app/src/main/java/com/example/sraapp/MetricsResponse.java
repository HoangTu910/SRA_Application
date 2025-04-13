package com.example.sraapp;

import com.google.gson.annotations.SerializedName;
public class MetricsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private MetricsData data;

    public static class MetricsData {
        @SerializedName("id")
        private String id;

        @SerializedName("pdr")
        private float pdr;

        @SerializedName("avgLatency")
        private float avgLatency;

        @SerializedName("avgPacket")
        private int avgPacket;

        @SerializedName("createdAt")
        private Timestamp createdAt;

        public float getPdr() { return pdr; }
        public float getAvgLatency() { return avgLatency; }
        public int getAvgPacket() { return avgPacket; }
    }

    public static class Timestamp {
        @SerializedName("_seconds")
        private long seconds;

        @SerializedName("_nanoseconds")
        private long nanoseconds;
    }

    public boolean isSuccess() { return success; }
    public MetricsData getData() { return data; }
}
