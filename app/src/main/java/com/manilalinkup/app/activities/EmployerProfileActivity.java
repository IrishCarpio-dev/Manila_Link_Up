package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.EmployerProfileModel;
import com.manilalinkup.app.models.GetRatingsRequest;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.RatingsProfileAdapter;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRatings;
    private RecyclerView recyclerViewAllJobsPosted;
    private RatingsProfileAdapter adapterRating;
    private JobPostDashboardAdapter adapterAllJobPost;
    private final List<RatingModel> ratingProfileList = new ArrayList<>();
    private List<JobPostDashboardModel> allJobsPostedList;
    MaterialButton viewArchivedJobs;
    BottomNavigationView bottomNavigationViewEmployer;
    private ImageView settingsIcon;
    ImageView viewAllRatings;
    private LinearLayout layoutRatingSummary;
    private RatingBar ratingBarProfile;
    private TextView tvRatingSummary;

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

        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerProfileActivity.this, EmployerSettingsActivity.class);
            startActivity(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRatings.setLayoutManager(layoutManager);

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerViewRatings.setAdapter(adapterRating);
        loadRatings();
        loadRatingStats();

        recyclerViewAllJobsPosted = findViewById(R.id.recycler_view_employer_jobs_posted);
        recyclerViewAllJobsPosted.setLayoutManager(new LinearLayoutManager(this));

        allJobsPostedList = new ArrayList<JobPostDashboardModel>();
        allJobsPostedList.add(new JobPostDashboardModel(
                "Events/Catering Helper",
                "Eng Bee Tin",
                "Binondo, Manila",
                "March 30, 2026",
                "https://en.wikipedia.org/wiki/Eng_Bee_Tin",
                "3 days ago"
        ));
        allJobsPostedList.add(new JobPostDashboardModel(
                "Cafe Barista",
                "Don Kopi",
                "Malate, Manila",
                "Full Time",
                "https://www.freepik.com/vectors/coffee-shop-logo-design",
                "7 days ago"
        ));

        allJobsPostedList.add(new JobPostDashboardModel(
                "Store Assistant",
                "Quick Smart Express",
                "Quiapo, Manila",
                "M | W | F",
                "https://venngage.com/templates/logos/market-store-creative-logo-fc8535df-be09-4c80-8ea5-a69a34b2318e",
                "10 days ago"
        ));

        allJobsPostedList.add(new JobPostDashboardModel(
                "Artist Assistant",
                "BINI Mika's Company",
                "GMA, Manila",
                "T | Th | F",
                "https://www.thebeautyedit.ph/people/bini-members-and-their-beauty-looks/",
                "1 day ago"
        ));


        adapterAllJobPost = new JobPostDashboardAdapter(allJobsPostedList, true, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(EmployerProfileActivity.this, EmployerViewJobPost.class);
                //not yet tested - irish
                startActivity(intent);
            }
            @Override
            public void onRemoveClick(JobPostDashboardModel job, int position) {
            }
        });
        recyclerViewAllJobsPosted.setAdapter(adapterAllJobPost);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_profile);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem ->  {
            if(menuItem.getItemId() == R.id.nav_home){
                startActivity(new Intent(EmployerProfileActivity.this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications) {
                startActivity(new Intent(EmployerProfileActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_add_job) {
                startActivity(new Intent(EmployerProfileActivity.this, EmployerAddJobActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat) {
                startActivity(new Intent(EmployerProfileActivity.this, ChatEmployerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

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

    private void loadRatingStats() {
        SessionCache.getInstance().ensureUserProfile(new SessionCache.UserProfileCallback() {
            @Override
            public void onAvailable(com.manilalinkup.app.models.UserProfileModel profile) {
                EmployerProfileModel employer = profile.getEmployers();
                if (employer == null) return;
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