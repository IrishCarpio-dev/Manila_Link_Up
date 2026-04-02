package com.manilalinkup.app;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotonClient {
    private static PhotonClient instance;
    private final PhotonService service;

    private PhotonClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://photon.komoot.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(PhotonService.class);
    }

    public static synchronized PhotonClient getInstance() {
        if (instance == null) instance = new PhotonClient();
        return instance;
    }

    public void search(String query, Callback<PhotonResponseModel> callback) {
        service.getAddress(
                query,
                5,
                14.5995,
                120.9842,
                0.9,
                "en"
        ).enqueue(callback);
    }
}
