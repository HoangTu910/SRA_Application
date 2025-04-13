package com.example.sraapp;

import com.example.sraapp.PerformanceMetrics;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PerformanceApi {
    @POST("metrics/latest")
    Call<MetricsResponse> getPerformanceMetrics(@Body DeviceIdRequest request);
}

