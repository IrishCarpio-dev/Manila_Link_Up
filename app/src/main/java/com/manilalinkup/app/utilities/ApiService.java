package com.manilalinkup.app.utilities;

import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ArchiveJobRequest;
import com.manilalinkup.app.models.CreateJobRequest;
import com.manilalinkup.app.models.EmployerRequest;
import com.manilalinkup.app.models.GetJobsRequest;
import com.manilalinkup.app.models.JobModel;
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

    @Multipart
    @POST("api/seeker/setupProfile")
    Call<ResponseBody> setupSeekerProfile(
            @Part MultipartBody.Part profilePhoto,
            @Part MultipartBody.Part clearance,
            @Part MultipartBody.Part validId,
            @Part("birthDate") RequestBody birthDate,
            @Part("location") RequestBody location,
            @Part("salaryValue") RequestBody salaryValue,
            @Part("salaryType") RequestBody salaryType
    );

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

    // Jobs
    @POST("api/jobs")
    Call<ResponseBody> createJob(@Body CreateJobRequest createJobRequest);

    @POST("api/jobs/list")
    Call<ApiResponse<JobModel>> getJobs(@Body GetJobsRequest getJobsRequest);

    @POST("api/jobs/archive")
    Call<ResponseBody> archiveJob(@Body ArchiveJobRequest archiveJobRequest);
}
