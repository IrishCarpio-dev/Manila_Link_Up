package com.manilalinkup.app.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.models.GetProfilePhotoRequest;
import com.manilalinkup.app.models.ProfilePhotoResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePhotoCache {

    public interface PhotoCallback {
        void onResult(String base64OrNull);
    }

    private static final ProfilePhotoCache INSTANCE = new ProfilePhotoCache();
    // null value = confirmed no photo; absent = not yet fetched
    private final HashMap<String, String> cache = new HashMap<>();

    private ProfilePhotoCache() {}

    public static ProfilePhotoCache getInstance() { return INSTANCE; }

    public void put(String uid, String base64OrNull) {
        if (uid == null) return;
        cache.put(uid, base64OrNull);
    }

    public void load(String uid, PhotoCallback callback) {
        if (uid == null) { callback.onResult(null); return; }

        if (cache.containsKey(uid)) {
            callback.onResult(cache.get(uid));
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { callback.onResult(null); return; }

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getProfilePhoto(new GetProfilePhotoRequest(uid)).enqueue(new Callback<ProfilePhotoResponse>() {
                @Override
                public void onResponse(Call<ProfilePhotoResponse> call, Response<ProfilePhotoResponse> response) {
                    String photo = (response.isSuccessful() && response.body() != null)
                            ? response.body().getData() : null;
                    cache.put(uid, photo);
                    callback.onResult(photo);
                }

                @Override
                public void onFailure(Call<ProfilePhotoResponse> call, Throwable t) {
                    callback.onResult(null);
                }
            });
        }).addOnFailureListener(e -> callback.onResult(null));
    }

    public void clear() { cache.clear(); }
}
