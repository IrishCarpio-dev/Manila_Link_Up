package com.manilalinkup.app.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.models.UserProfileModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionCache {

    public interface ServiceTagsCallback {
        void onAvailable(List<ServiceTagModel> tags);
        void onError();
    }

    public interface UserProfileCallback {
        void onAvailable(UserProfileModel profile);
        void onError();
    }

    private static final SessionCache INSTANCE = new SessionCache();

    private UserProfileModel userProfile;
    private List<ServiceTagModel> serviceTags;

    private SessionCache() {}

    public static SessionCache getInstance() {
        return INSTANCE;
    }

    public UserProfileModel getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileModel profile) {
        this.userProfile = profile;
    }

    public List<ServiceTagModel> getServiceTags() {
        return serviceTags == null ? null : Collections.unmodifiableList(serviceTags);
    }

    public void setServiceTags(List<ServiceTagModel> tags) {
        this.serviceTags = tags == null ? null : new ArrayList<>(tags);
    }

    public Map<String, String> getServiceTagLabelsById() {
        Map<String, String> map = new HashMap<>();
        if (serviceTags != null) {
            for (ServiceTagModel tag : serviceTags) {
                if (tag.getId() != null) map.put(tag.getId(), tag.getLabel());
            }
        }
        return map;
    }

    public void clear() {
        userProfile = null;
        serviceTags = null;
    }

    public void ensureServiceTags(ServiceTagsCallback callback) {
        if (serviceTags != null) {
            callback.onAvailable(getServiceTags());
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError();
            return;
        }
        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful() || tokenTask.getResult() == null) {
                callback.onError();
                return;
            }
            fetchServiceTags(tokenTask.getResult().getToken(), callback);
        });
    }

    public void refreshServiceTags(String token, ServiceTagsCallback callback) {
        fetchServiceTags(token, callback);
    }

    public void ensureUserProfile(UserProfileCallback callback) {
        if (userProfile != null) {
            callback.onAvailable(userProfile);
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError();
            return;
        }
        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful() || tokenTask.getResult() == null) {
                callback.onError();
                return;
            }
            fetchUserProfile(tokenTask.getResult().getToken(), callback);
        });
    }

    public void refreshUserProfile(String token, UserProfileCallback callback) {
        fetchUserProfile(token, callback);
    }

    private void fetchServiceTags(String token, ServiceTagsCallback callback) {
        ApiService api = RetrofitClient.getClient(token).create(ApiService.class);
        api.getServiceTags().enqueue(new Callback<ApiResponse<List<ServiceTagModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceTagModel>>> call,
                                   Response<ApiResponse<List<ServiceTagModel>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    setServiceTags(response.body().getData());
                    callback.onAvailable(getServiceTags());
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceTagModel>>> call, Throwable t) {
                callback.onError();
            }
        });
    }

    private void fetchUserProfile(String token, UserProfileCallback callback) {
        ApiService api = RetrofitClient.getClient(token).create(ApiService.class);
        api.getUserProfile().enqueue(new Callback<ApiResponse<UserProfileModel>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfileModel>> call,
                                   Response<ApiResponse<UserProfileModel>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    setUserProfile(response.body().getData());
                    callback.onAvailable(userProfile);
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfileModel>> call, Throwable t) {
                callback.onError();
            }
        });
    }
}
