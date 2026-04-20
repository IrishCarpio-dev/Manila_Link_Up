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

        // If no user is logged in, skip straight to the cleanup
        if (user == null) {
            finalPerformSignOut(activity, mAuth);
            return;
        }

        // STEP 1: Get the ID Token to talk to your Laravel Backend
        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                finalPerformSignOut(activity, mAuth);
                return;
            }

            String idToken = tokenTask.getResult().getToken();

            // STEP 2: Get FCM Token to unregister notifications
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(fcmTask -> {
                if (!fcmTask.isSuccessful()) {
                    finalPerformSignOut(activity, mAuth);
                    return;
                }

                String fcmToken = fcmTask.getResult();
                ApiService api = RetrofitClient.getClient(idToken).create(ApiService.class);

                // STEP 3: Tell Laravel this device is logging out
                api.unregisterDevice(new UnregisterDeviceRequest(fcmToken))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                // ONLY NOW we move to sign out
                                finalPerformSignOut(activity, mAuth);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                // Even if API fails, we must sign out locally
                                finalPerformSignOut(activity, mAuth);
                            }
                        });
            });
        });
    }

    private static void finalPerformSignOut(Activity activity, FirebaseAuth mAuth) {
        // STEP 4: Clear your local SessionCache
        SessionCache.getInstance().clear();

        // STEP 5: Sign out from Firebase
        mAuth.signOut();

        // STEP 6: Sign out from Google (THIS FIXES THE ACCOUNT PICKER)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getClient(activity, gso).signOut().addOnCompleteListener(task -> {
            // STEP 7: Finally, redirect to Login once everything above is DONE
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}
