package com.example.sraapp;

import java.util.LinkedList;
import java.util.Queue;

public class ChartDataQueue {
    private final Queue<Float> dataPoints;
    private final int maxSize;

    public ChartDataQueue(int maxSize) {
        this.dataPoints = new LinkedList<>();
        this.maxSize = maxSize;
        for(int i = 0; i < maxSize; i++) {
            dataPoints.add(0f);
        }
    }

    public void addDataPoint(float value) {
        if (dataPoints.size() >= maxSize) {
            dataPoints.remove();
        }
        dataPoints.add(value);
    }

    public Queue<Float> getDataPoints() {
        return dataPoints;
    }
}
