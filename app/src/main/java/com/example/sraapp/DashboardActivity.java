package com.example.sraapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private DashboardViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_dashboard);

        TextView statusCircle = findViewById(R.id.status_circle);
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        statusCircle.startAnimation(blinkAnimation);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                DashboardRepository repository = new DashboardRepository(
                        true,
                        "http://192.168.1.6:7979/api/devices/"
                );
                return (T) new DashboardViewModel(repository);
            }
        }).get(DashboardViewModel.class);

        BarChart barChart = findViewById(R.id.barChart);

        // Find the TextViews
        TextView packetsRejectedText = findViewById(R.id.packet_rejected_value);
        TextView safeCounterText = findViewById(R.id.safe_counter_value);
        TextView bruteForceText = findViewById(R.id.brute_force_detected_value);
        TextView pdrText = findViewById(R.id.pdr_value);
        TextView averageLatencyText = findViewById(R.id.average_latency_value);
        TextView averagePacketsText = findViewById(R.id.average_packets_value);
        TextView pdrLargeText = findViewById(R.id.pdrLargeText);

        LineChart pdrChart = findViewById(R.id.PDRLineChart);
        LineChart latencyChart = findViewById(R.id.avgLatencyLineChart);
        LineChart packetsChart = findViewById(R.id.avgPacketsLineChart);

        viewModel.getSecurityMetrics().observe(this, metrics -> {
            packetsRejectedText.setText(String.valueOf(metrics.getPacketsRejected()));
            safeCounterText.setText(String.valueOf(metrics.getSafeCounterActivated()));
            bruteForceText.setText(String.valueOf(metrics.getBruteForceDetected()));
        });

        viewModel.getPerformanceMetrics().observe(this, performanceMetrics -> {
            String pdr = performanceMetrics.getPDR() + "%";
            String averageLatency = performanceMetrics.getAverageLatency() + " ms";
            String averagePackets = String.valueOf(performanceMetrics.getAveragePackets());
            pdrText.setText(pdr);
            String pdrLargeTextStr = performanceMetrics.getPDR() + " Percent";
            pdrLargeText.setText(pdrLargeTextStr);
            averageLatencyText.setText(averageLatency);
            averagePacketsText.setText(averagePackets);
        });

        viewModel.getBarEntries().observe(this, entries -> {
            BarDataSet dataSet = new BarDataSet(entries, "Security Metrics");
            dataSet.setColors(
                    ContextCompat.getColor(this, R.color.packets_rejected),
                    ContextCompat.getColor(this, R.color.safe_counter),
                    ContextCompat.getColor(this, R.color.brute_force)
            );
            dataSet.setDrawValues(false);
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.5f);
            barChart.setData(barData);
            setupBarChart(barChart);
        });

        setupLineChart(pdrChart, "PDR");
        setupLineChart(latencyChart, "Latency");
        setupLineChart(packetsChart, "Packets");

        viewModel.getPdrEntries().observe(this, entries -> {
            updateLineChart(pdrChart, entries, "PDR",
                    ContextCompat.getColor(this, R.color.pdr_color));
        });

        viewModel.getLatencyEntries().observe(this, entries -> {
            updateLineChart(latencyChart, entries, "Latency",
                    ContextCompat.getColor(this, R.color.latency_color));
        });

        viewModel.getPacketsEntries().observe(this, entries -> {
            updateLineChart(packetsChart, entries, "Packets",
                    ContextCompat.getColor(this, R.color.packets_color));
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_dashboard);
        
        navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_device) {
                startActivity(new Intent(this, DeviceActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    public void setupBarChart(BarChart barChart) {
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"", "Packets", "Safe", "Brute"}));
        barChart.getXAxis().setTextSize(5f);
        barChart.getAxisLeft().setTextSize(5f);
        barChart.invalidate();
    }

    private void setupLineChart(LineChart chart, String label) {
        // Disable description
        chart.getDescription().setEnabled(false);

        // Basic chart settings
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);

        // X axis setup
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5, true);

        // Y axis setup
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);  // Remove horizontal grid lines
        leftAxis.setLabelCount(5, true);
        leftAxis.setAxisMinimum(0f);  // Set minimum value to 0

        // Disable right axis
        chart.getAxisRight().setEnabled(false);

        // Setup legend at bottom
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setKeepPositionOnRotation(true);
        
        // Tối ưu hiệu suất
        chart.setMaxVisibleValueCount(100);
        chart.setHardwareAccelerationEnabled(true);
    }

    private void updateLineChart(LineChart chart, List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(false);

        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(50);
        dataSet.setFillColor(color);
        dataSet.setCubicIntensity(0.2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}