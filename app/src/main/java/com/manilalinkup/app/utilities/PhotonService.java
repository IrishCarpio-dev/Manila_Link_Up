package com.manilalinkup.app.utilities;

import com.manilalinkup.app.models.PhotonResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface PhotonService
{
    @GET("api/")
    Call<PhotonResponseModel> getAddress(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("location_bias_scale") Double bias,
            @Query("lang") String lang
    );
}
