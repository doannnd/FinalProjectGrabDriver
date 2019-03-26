package com.nguyendinhdoan.finalprojectgrabdriver.common;

import com.nguyendinhdoan.finalprojectgrabdriver.data.remote.IGoogleAPI;
import com.nguyendinhdoan.finalprojectgrabdriver.data.remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static final String baseUrl = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseUrl).create(IGoogleAPI.class);
    }
}
