package com.manilalinkup.app;

import com.manilalinkup.app.EmployerRequest;
import com.manilalinkup.app.SeekerRequest;
import com.manilalinkup.app.UserProfileModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
public interface ApiService {
    @POST("api/seeker/signup")
    Call<ResponseBody> registerSeeker(@Body SeekerRequest seekerRequest);

    @POST("api/employer/signup")
    Call<ResponseBody> registerEmployer(@Body EmployerRequest employerRequest);

    @GET("api/user/profile")
    Call<UserProfileModel> getUserProfile();
}
