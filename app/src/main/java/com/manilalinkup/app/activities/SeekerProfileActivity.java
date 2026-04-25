package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.ExperienceAdapter;
import com.manilalinkup.app.adapters.RatingsProfileAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ExperienceModel;
import com.manilalinkup.app.models.GetRatingsRequest;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.models.SeekerProfileModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeekerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRatings;
    private RatingsProfileAdapter adapterRating;
    private final List<RatingModel> ratingProfileList = new ArrayList<>();

    private RecyclerView recyclerViewExperience;
    private ExperienceAdapter adapterExperience;
    private List<ExperienceModel> experienceList;

    private BottomNavigationView bottomNavigationView;
    private ImageView settingsIcon;
    private TextInputEditText summaryEditText;
    ImageView viewAllRatings;
    private LinearLayout layoutRatingSummary;
    private RatingBar ratingBarProfile;
    private TextView tvRatingSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_profile);
        settingsIcon = findViewById(R.id.image_view_seeker_settings_icon);
        summaryEditText = findViewById(R.id.editText_summary);
        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        recyclerViewExperience = findViewById(R.id.recycler_view_experience);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile_seeker);
        bottomNavigationView.setOnItemSelectedListener(menuItem ->  {
            if(menuItem.getItemId() == R.id.nav_home_seeker){
                startActivity(new Intent(SeekerProfileActivity.this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications_seeker) {
                //No notif yet for seeker
                startActivity(new Intent(SeekerProfileActivity.this, SeekerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_activity_seeker) {
                startActivity(new Intent(SeekerProfileActivity.this, AppliedSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat_seeker) {
                startActivity(new Intent(SeekerProfileActivity.this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        layoutRatingSummary = findViewById(R.id.layout_rating_summary);
        ratingBarProfile = findViewById(R.id.rating_bar_profile);
        tvRatingSummary = findViewById(R.id.tv_rating_summary);

        setupFeedbacksRecyclerView();
        setupExperienceRecyclerView();
        setupClickListeners();
        loadRatingStats();

        viewAllRatings = findViewById(R.id.item_card_see_more_ratings_seeker);
        viewAllRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeekerProfileActivity.this, EmployerViewAllRatings.class);
                startActivity(intent);
            }
        });

    }

    private void loadRatingStats() {
        SessionCache.getInstance().ensureUserProfile(new SessionCache.UserProfileCallback() {
            @Override
            public void onAvailable(com.manilalinkup.app.models.UserProfileModel profile) {
                SeekerProfileModel seeker = profile.getSeekers();
                if (seeker == null) return;
                Integer count = seeker.getRatingCount();
                Double avg = seeker.getBayesianAvg();
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

    private void setupFeedbacksRecyclerView() {
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRatings.setLayoutManager(horizontalLayout);

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerViewRatings.setAdapter(adapterRating);
        loadRatings();
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
                            Toast.makeText(SeekerProfileActivity.this,
                                    "Failed to load ratings", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setupExperienceRecyclerView() {
        recyclerViewExperience.setLayoutManager(new LinearLayoutManager(this));

        experienceList = new ArrayList<>();
        experienceList.add(new ExperienceModel("Barista", "Starbucks - Manila", "Jan 2023 - Present"));
        experienceList.add(new ExperienceModel("Delivery Rider", "GrabFood PH", "June 2022 - Dec 2022"));
        experienceList.add(new ExperienceModel("Service Crew", "Jollibee Padre Faura", "Nov 2021 - May 2022"));

        adapterExperience = new ExperienceAdapter(experienceList);
        recyclerViewExperience.setAdapter(adapterExperience);
    }


    private void setupClickListeners() {
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> {
                Intent intent = new Intent(SeekerProfileActivity.this, SeekerSettingsActivity.class);
                startActivity(intent);
            });
        }

        if (summaryEditText != null) {
            summaryEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    // Logic to save profile summary text
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            MenuItem profileItem = bottomNavigationView.getMenu().findItem(R.id.nav_profile);
            if (profileItem != null) {
                profileItem.setChecked(true);
            }
        }
    }
}