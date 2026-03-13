package com.manilalinkup.app;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @POST("api/register-seeker")
    Call<ResponseBody> registerSeeker(@Body SeekerRequest seekerRequest);
}
