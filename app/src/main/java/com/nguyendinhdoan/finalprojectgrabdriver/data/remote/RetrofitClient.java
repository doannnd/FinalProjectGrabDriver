package com.nguyendinhdoan.finalprojectgrabdriver.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit mRetrofit;

    public static Retrofit getClient(String baseUrl) {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
