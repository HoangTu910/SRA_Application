package com.example.sraapp;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {
    private final DashboardRepository repository;
    private final MutableLiveData<SecurityMetrics> securityMetrics = new MutableLiveData<>();
    private final MutableLiveData<PerformanceMetrics> performanceMetrics = new MutableLiveData<>();
    private final MutableLiveData<List<BarEntry>> barEntries = new MutableLiveData<>();
    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private static final int CHART_MAX_SIZE = 120;
    private final ChartDataQueue pdrQueue = new ChartDataQueue(CHART_MAX_SIZE);
    private final ChartDataQueue latencyQueue = new ChartDataQueue(CHART_MAX_SIZE);
    private final ChartDataQueue packetsQueue = new ChartDataQueue(CHART_MAX_SIZE);
    private final MutableLiveData<List<Entry>> pdrEntries = new MutableLiveData<>();
    private final MutableLiveData<List<Entry>> latencyEntries = new MutableLiveData<>();
    private final MutableLiveData<List<Entry>> packetsEntries = new MutableLiveData<>();
    private final int UPDATE_INTERVAL = 1000;
    private static final int ANIMATION_DURATION = 500;

    public int getAnimationDuration() {
        return ANIMATION_DURATION;
    }

    public DashboardViewModel(DashboardRepository repository) {
        this.repository = repository;
        startPeriodicUpdates();
    }
    private void startPeriodicUpdates() {
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchSecurityMetrics();
                fetchPerformanceMetrics();
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        updateHandler.removeCallbacksAndMessages(null);
    }
    public LiveData<SecurityMetrics> getSecurityMetrics() {
        return securityMetrics;
    }

    public LiveData<PerformanceMetrics> getPerformanceMetrics() {
        return performanceMetrics;
    }

    public LiveData<List<BarEntry>> getBarEntries() {
        return barEntries;
    }

    public LiveData<List<Entry>> getPdrEntries() { return pdrEntries; }
    public LiveData<List<Entry>> getLatencyEntries() { return latencyEntries; }
    public LiveData<List<Entry>> getPacketsEntries() { return packetsEntries; }

    private void fetchSecurityMetrics() {
        // Use an ExecutorService to run the data fetching on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                SecurityMetrics metrics = (SecurityMetrics) repository.getSecurityMetrics();
                securityMetrics.postValue(metrics);

                // Prepare bar chart entries
                List<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(1f, metrics.getPacketsRejected()));
                entries.add(new BarEntry(2f, metrics.getSafeCounterActivated()));
                entries.add(new BarEntry(3f, metrics.getBruteForceDetected()));
                barEntries.postValue(entries);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle error (e.g., post an error state)
            } finally {
                executor.shutdown();
            }
        });
    }

    private void fetchPerformanceMetrics() {
        repository.getPerformanceMetrics()
                .thenAcceptAsync(metrics -> {
                    try {
                        // Update LiveData with new metrics
                        performanceMetrics.postValue(metrics);

                        // Update queues with new data
                        pdrQueue.addDataPoint(metrics.getPDR());
                        latencyQueue.addDataPoint(metrics.getAverageLatency());
                        packetsQueue.addDataPoint(metrics.getAveragePackets());

                        // Update line chart entries
                        updateLineChartEntries();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, executor);  // Use executor for background processing
    }
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private void updateLineChartEntries() {
        // Convert queue data to entries
        List<Entry> pdrList = new ArrayList<>();
        List<Entry> latencyList = new ArrayList<>();
        List<Entry> packetsList = new ArrayList<>();

        int index = 0;
        for (Float value : pdrQueue.getDataPoints()) {
            pdrList.add(new Entry(index, value));
            index++;
        }

        index = 0;
        for (Float value : latencyQueue.getDataPoints()) {
            latencyList.add(new Entry(index, value));
            index++;
        }

        index = 0;
        for (Float value : packetsQueue.getDataPoints()) {
            packetsList.add(new Entry(index, value));
            index++;
        }

        pdrEntries.postValue(pdrList);
        latencyEntries.postValue(latencyList);
        packetsEntries.postValue(packetsList);
    }
}
