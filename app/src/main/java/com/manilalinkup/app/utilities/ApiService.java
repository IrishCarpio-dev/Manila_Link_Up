package com.manilalinkup.app.utilities;

import com.manilalinkup.app.models.EmployerRequest;
import com.manilalinkup.app.models.SeekerRequest;
import com.manilalinkup.app.models.UserProfileModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("api/seeker/signup")
    Call<ResponseBody> registerSeeker(@Body SeekerRequest seekerRequest);

    @POST("api/employer/signup")
    Call<ResponseBody> registerEmployer(@Body EmployerRequest employerRequest);

    @Multipart
    @POST("api/employer/setupProfile")
    Call<ResponseBody> setupEmployerProfile(
        @Part MultipartBody.Part profilePhoto,
        @Part MultipartBody.Part clearance,
        @Part MultipartBody.Part validId,
        @Part("birthDate") RequestBody birthDate,
        @Part("location") RequestBody location
    );

    @GET("api/user/profile")
    Call<UserProfileModel> getUserProfile();
}
