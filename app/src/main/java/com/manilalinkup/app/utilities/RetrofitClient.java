package com.manilalinkup.app.utilities;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://10.0.2.2:8000/"; // TODO: Change BASE_URL to actual server domain

    private static OkHttpClient sharedHttpClient;

    private static OkHttpClient getHttpClient(String token) {
        if (sharedHttpClient == null) {
            sharedHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return sharedHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .method(original.method(), original.body());

                    if (token != null && !token.isBlank()) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                    }

                    return chain.proceed(requestBuilder.build());
                })
                .build();
    }

    public static Retrofit getClient(String token) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient(token))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}