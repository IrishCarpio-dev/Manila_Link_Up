package com.manilalinkup.app.utilities;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.LoginActivity;
import com.manilalinkup.app.activities.MainActivity;
import com.manilalinkup.app.models.UnregisterDeviceRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutHelper {
    public static void logout(Activity activity, FirebaseAuth mAuth) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            finalPerformSignOut(activity, mAuth);
            return;
        }

        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                finalPerformSignOut(activity, mAuth);
                return;
            }

            String idToken = tokenTask.getResult().getToken();

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(fcmTask -> {
                if (!fcmTask.isSuccessful()) {
                    finalPerformSignOut(activity, mAuth);
                    return;
                }

                String fcmToken = fcmTask.getResult();
                ApiService api = RetrofitClient.getClient(idToken).create(ApiService.class);

                api.unregisterDevice(new UnregisterDeviceRequest(fcmToken))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                finalPerformSignOut(activity, mAuth);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                finalPerformSignOut(activity, mAuth);
                            }
                        });
            });
        });
    }

    private static void finalPerformSignOut(Activity activity, FirebaseAuth mAuth) {
        SessionCache.getInstance().clear();
        ProfilePhotoCache.getInstance().clear();

        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getClient(activity, gso).signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}
