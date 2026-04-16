package com.manilalinkup.app.utilities;

import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicantModel;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.AppliedJobModel;
import com.manilalinkup.app.models.ApplyJobRequest;
import com.manilalinkup.app.models.ArchiveJobRequest;
import com.manilalinkup.app.models.ChatListItemModel;
import com.manilalinkup.app.models.ChatMessageModel;
import com.manilalinkup.app.models.CreateJobRequest;
import com.manilalinkup.app.models.EmployerRequest;
import com.manilalinkup.app.models.GetApplicantsRequest;
import com.manilalinkup.app.models.GetAppliedJobsRequest;
import com.manilalinkup.app.models.GetChatsRequest;
import com.manilalinkup.app.models.GetJobsRequest;
import com.manilalinkup.app.models.GetMessagesRequest;
import com.manilalinkup.app.models.GetRatingsRequest;
import com.manilalinkup.app.models.HideChatRequest;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.MarkCompleteRequest;
import com.manilalinkup.app.models.MarkReadRequest;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.models.RegisterDeviceRequest;
import com.manilalinkup.app.models.SeekerRequest;
import com.manilalinkup.app.models.SendMessageRequest;
import com.manilalinkup.app.models.SubmitRatingRequest;
import com.manilalinkup.app.models.UnregisterDeviceRequest;
import com.manilalinkup.app.models.UpdateApplicationStatusRequest;
import com.manilalinkup.app.models.UserProfileModel;
import com.manilalinkup.app.models.WithdrawApplicationRequest;

import java.util.List;

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

    // Auth
    @POST("api/seeker/signup")
    Call<ResponseBody> registerSeeker(@Body SeekerRequest seekerRequest);

    @Multipart
    @POST("api/seeker/setupProfile")
    Call<ResponseBody> setupSeekerProfile(
            @Part MultipartBody.Part profilePhoto,
            @Part MultipartBody.Part clearance,
            @Part MultipartBody.Part validId,
            @Part("address") RequestBody address,
            @Part("birthDate") RequestBody birthDate,
            @Part("location") RequestBody location
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
    Call<ApiResponse<UserProfileModel>> getUserProfile();

    // Jobs
    @POST("api/jobs")
    Call<ResponseBody> createJob(@Body CreateJobRequest createJobRequest);

    @POST("api/jobs/list")
    Call<ApiResponse<List<JobModel>>> getJobs(@Body GetJobsRequest getJobsRequest);

    @POST("api/jobs/archive")
    Call<ResponseBody> archiveJob(@Body ArchiveJobRequest archiveJobRequest);

    // Applications
    @POST("api/jobs/apply")
    Call<ApiResponse<ApplicationModel>> applyJob(@Body ApplyJobRequest request);

    @POST("api/jobs/withdraw")
    Call<ResponseBody> withdrawApplication(@Body WithdrawApplicationRequest request);

    @POST("api/jobs/applicants")
    Call<ApiResponse<List<ApplicantModel>>> getApplicants(@Body GetApplicantsRequest request);

    @POST("api/seeker/appliedJobs")
    Call<ApiResponse<List<AppliedJobModel>>> getAppliedJobs(@Body GetAppliedJobsRequest request);

    @POST("api/applications/updateStatus")
    Call<ApiResponse<ApplicationModel>> updateApplicationStatus(@Body UpdateApplicationStatusRequest request);

    @POST("api/applications/markComplete")
    Call<ApiResponse<ApplicationModel>> markApplicationComplete(@Body MarkCompleteRequest request);

    // Chat
    @POST("api/chats/list")
    Call<ApiResponse<List<ChatListItemModel>>> getChats(@Body GetChatsRequest request);

    @POST("api/chats/messages")
    Call<ApiResponse<List<ChatMessageModel>>> getMessages(@Body GetMessagesRequest request);

    @POST("api/chats/send")
    Call<ApiResponse<ChatMessageModel>> sendMessage(@Body SendMessageRequest request);

    @POST("api/chats/markRead")
    Call<ResponseBody> markChatRead(@Body MarkReadRequest request);

    @POST("api/chats/hide")
    Call<ResponseBody> hideChat(@Body HideChatRequest request);

    // Ratings
    @POST("api/ratings")
    Call<ApiResponse<RatingModel>> submitRating(@Body SubmitRatingRequest request);

    @POST("api/ratings/list")
    Call<ApiResponse<List<RatingModel>>> getRatings(@Body GetRatingsRequest request);

    // Devices (FCM)
    @POST("api/devices/register")
    Call<ResponseBody> registerDevice(@Body RegisterDeviceRequest request);

    @POST("api/devices/unregister")
    Call<ResponseBody> unregisterDevice(@Body UnregisterDeviceRequest request);
}
