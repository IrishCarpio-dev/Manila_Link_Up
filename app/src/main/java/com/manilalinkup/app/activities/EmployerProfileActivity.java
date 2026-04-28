package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.EmployerProfileModel;
import com.manilalinkup.app.models.GetRatingsRequest;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.models.UserProfileModel;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.RatingsProfileAdapter;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.EmployerNavHelper;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import static com.manilalinkup.app.utilities.RetrofitClient.BASE_URL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerProfileActivity extends BaseActivity {

    private RecyclerView recyclerViewRatings;
    private RatingsProfileAdapter adapterRating;
    private final List<RatingModel> ratingProfileList = new ArrayList<>();
    MaterialButton viewArchivedJobs;
    BottomNavigationView bottomNavigationViewEmployer;
    private ImageView settingsIcon;
    ImageView viewAllRatings;
    private LinearLayout layoutRatingSummary;
    private RatingBar ratingBarProfile;
    private TextView tvRatingSummary;
    private TextView tvName;
    private TextView tvLocation;
    private ImageView ivProfilePic;
    private ImageView ivVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_profile);

        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        settingsIcon = findViewById(R.id.image_view_employer_settings_icon);
        layoutRatingSummary = findViewById(R.id.layout_rating_summary);
        ratingBarProfile = findViewById(R.id.rating_bar_profile);
        tvRatingSummary = findViewById(R.id.tv_rating_summary);
        tvName = findViewById(R.id.text_view_employer_name_profile);
        tvLocation = findViewById(R.id.text_view_location_employer_profile);
        ivProfilePic = findViewById(R.id.image_view_employee_profile_picture_placeholder);
        ivVerification = findViewById(R.id.verification_checkmark_blue);

        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerProfileActivity.this, EmployerSettingsActivity.class);
            startActivity(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRatings.setLayoutManager(layoutManager);

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerViewRatings.setAdapter(adapterRating);
        loadRatings();
        loadProfileData();

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        EmployerNavHelper.setup(this, bottomNavigationViewEmployer, R.id.nav_profile);

        viewAllRatings = findViewById(R.id.item_card_see_more_ratings_arrow);
        viewAllRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerProfileActivity.this, EmployerViewAllRatings.class);
                startActivity(intent);
            }
        });

        viewArchivedJobs = findViewById(R.id.button_view_archive_jobs);
        viewArchivedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerProfileActivity.this, EmployerViewArchivedJobs.class);
                startActivity(intent);
            }
        });
    }

    private void loadProfileData() {
        SessionCache.getInstance().ensureUserProfile(new SessionCache.UserProfileCallback() {
            @Override
            public void onAvailable(UserProfileModel profile) {
                EmployerProfileModel employer = profile.getEmployers();
                if (employer == null) return;

                tvName.setText(employer.getFullName() != null ? employer.getFullName() : "");
                tvLocation.setText(employer.getAddress() != null ? employer.getAddress() : "");

                ivVerification.setVisibility(Boolean.TRUE.equals(employer.getVerified()) ? View.VISIBLE : View.GONE);

                if (employer.getProfilePhotoUrl() != null) {
                    Glide.with(EmployerProfileActivity.this)
                            .load(BASE_URL + employer.getProfilePhotoUrl())
                            .placeholder(R.drawable.ic_person_placeholder)
                            .into(ivProfilePic);
                }

                Integer count = employer.getRatingCount();
                Double avg = employer.getBayesianAvg();
                if (count != null && count > 0 && avg != null) {
                    ratingBarProfile.setRating(avg.floatValue());
                    tvRatingSummary.setText(String.format("%.1f (%d %s)",
                            avg, count, count == 1 ? "rating" : "ratings"));
                    layoutRatingSummary.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {}
        });
    }

    private void loadRatings() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getRatings(new GetRatingsRequest(user.getUid(), null, null))
                    .enqueue(new Callback<ApiResponse<List<RatingModel>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<RatingModel>>> call,
                                               Response<ApiResponse<List<RatingModel>>> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                ratingProfileList.clear();
                                ratingProfileList.addAll(response.body().getData());
                                adapterRating.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<RatingModel>>> call, Throwable t) {
                            Toast.makeText(EmployerProfileActivity.this,
                                    "Failed to load ratings", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}