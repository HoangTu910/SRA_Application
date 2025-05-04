package com.example.sraapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;

public class DashboardFragment extends Fragment {
    private DashboardViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        TextView statusCircle = view.findViewById(R.id.status_circle);
        Animation blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink);
        statusCircle.startAnimation(blinkAnimation);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                DashboardRepository repository = new DashboardRepository(
                    true,
                    "http://192.168.1.6:7979/api/devices/"
                );
                return (T) new DashboardViewModel(repository);
            }
        }).get(DashboardViewModel.class);

        // Initialize charts and views
        BarChart barChart = view.findViewById(R.id.barChart);
        TextView packetsRejectedText = view.findViewById(R.id.packet_rejected_value);
        TextView safeCounterText = view.findViewById(R.id.safe_counter_value);
        TextView bruteForceText = view.findViewById(R.id.brute_force_detected_value);
        TextView pdrText = view.findViewById(R.id.pdr_value);
        TextView averageLatencyText = view.findViewById(R.id.average_latency_value);
        TextView averagePacketsText = view.findViewById(R.id.average_packets_value);
        TextView pdrLargeText = view.findViewById(R.id.pdrLargeText);

        LineChart pdrChart = view.findViewById(R.id.PDRLineChart);
        LineChart latencyChart = view.findViewById(R.id.avgLatencyLineChart);
        LineChart packetsChart = view.findViewById(R.id.avgPacketsLineChart);

        // Observe security metrics
        viewModel.getSecurityMetrics().observe(getViewLifecycleOwner(), metrics -> {
            packetsRejectedText.setText(String.valueOf(metrics.getPacketsRejected()));
            safeCounterText.setText(String.valueOf(metrics.getSafeCounterActivated()));
            bruteForceText.setText(String.valueOf(metrics.getBruteForceDetected()));
        });

        // Observe performance metrics
        viewModel.getPerformanceMetrics().observe(getViewLifecycleOwner(), performanceMetrics -> {
            String pdr = performanceMetrics.getPDR() + "%";
            String averageLatency = performanceMetrics.getAverageLatency() + " ms";
            String averagePackets = String.valueOf(performanceMetrics.getAveragePackets());
            pdrText.setText(pdr);
            pdrLargeText.setText(performanceMetrics.getPDR() + " Percent");
            averageLatencyText.setText(averageLatency);
            averagePacketsText.setText(averagePackets);
        });

        // Setup bar chart
        viewModel.getBarEntries().observe(getViewLifecycleOwner(), entries -> {
            BarDataSet dataSet = new BarDataSet(entries, "Security Metrics");
            dataSet.setColors(
                requireContext().getColor(R.color.packets_rejected),
                requireContext().getColor(R.color.safe_counter),
                requireContext().getColor(R.color.brute_force)
            );
            dataSet.setDrawValues(false);
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.5f);
            barChart.setData(barData);
            setupBarChart(barChart);
        });

        // Setup line charts
        setupLineChart(pdrChart, "PDR");
        setupLineChart(latencyChart, "Latency");
        setupLineChart(packetsChart, "Packets");

        // Observe line chart data
        viewModel.getPdrEntries().observe(getViewLifecycleOwner(), entries -> {
            updateLineChart(pdrChart, entries, "PDR",
                requireContext().getColor(R.color.pdr_color));
        });

        viewModel.getLatencyEntries().observe(getViewLifecycleOwner(), entries -> {
            updateLineChart(latencyChart, entries, "Latency",
                requireContext().getColor(R.color.latency_color));
        });

        viewModel.getPacketsEntries().observe(getViewLifecycleOwner(), entries -> {
            updateLineChart(packetsChart, entries, "Packets",
                requireContext().getColor(R.color.packets_color));
        });

        return view;
    }

    private void setupBarChart(BarChart barChart) {
        // ...existing code from DashboardActivity...
    }

    private void setupLineChart(LineChart chart, String label) {
        // ...existing code from DashboardActivity...
    }

    private void updateLineChart(LineChart chart, List<Entry> entries, String label, int color) {
        // ...existing code from DashboardActivity...
    }
}
