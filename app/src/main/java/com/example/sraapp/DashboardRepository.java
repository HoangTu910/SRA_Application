package com.example.sraapp;

import android.util.Log;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import com.example.sraapp.PerformanceApi;
import com.example.sraapp.MetricsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardRepository {
    private final Random random = new Random();
    private PerformanceApi api;
    private boolean useApi;

    public DashboardRepository() {
        // Default to random data
        this.useApi = false;
    }

    public DashboardRepository(boolean useApi, String baseUrl) {
        this.useApi = useApi;
        if (useApi) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
                api = retrofit.create(PerformanceApi.class);
            } catch (Exception e) {
                e.printStackTrace();
                this.useApi = false;
            }
        }
    }

    public CompletableFuture<PerformanceMetrics> getPerformanceMetrics() {
        CompletableFuture<PerformanceMetrics> future = new CompletableFuture<>();

        if (!useApi || api == null) {
            future.complete(new PerformanceMetrics(
                -1f, -1f, -1
            ));
            return future;
        }
        String deviceId = "135334160";
        DeviceIdRequest request = new DeviceIdRequest(deviceId);
        api.getPerformanceMetrics(request).enqueue(new Callback<MetricsResponse>() {
            @Override
            public void onResponse(Call<MetricsResponse> call, Response<MetricsResponse> response) {
                Log.d("API", "URL called: " + call.request().url());
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    MetricsResponse.MetricsData data = response.body().getData();
                    future.complete(new PerformanceMetrics(
                        data.getPdr(),
                        data.getAvgLatency(),
                        data.getAvgPacket()
                    ));
                } else {
                    Log.d("DashboardRepository", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MetricsResponse> call, Throwable t) {
                Log.e("DashboardRepository", "API call failed: " + t.getMessage());
                t.printStackTrace();
            }
        });

        return future;
    }

    private PerformanceMetrics getFallbackMetrics() {
        return new PerformanceMetrics(
            Math.round(random.nextFloat() * 10000) / 100f,
            Math.round(random.nextFloat() * 20000) / 100f,
            random.nextInt(100)
        );
    }

    public SecurityMetrics getSecurityMetrics() {
    // Generate random values between 5-20 for security metrics
        int packetsRejected = random.nextInt(16) + 5;
        int safeCounter = random.nextInt(16) + 5;
        int bruteForce = random.nextInt(16) + 5;

        return new SecurityMetrics(packetsRejected, safeCounter, bruteForce);
    }
}
