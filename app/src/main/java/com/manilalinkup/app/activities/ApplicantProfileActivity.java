package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;

import com.manilalinkup.app.utilities.ImageUtils;
import com.manilalinkup.app.utilities.ProfilePhotoCache;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.AppliedJobModel;
import com.manilalinkup.app.models.CompletedJobsResponse;
import com.manilalinkup.app.models.GetCompletedJobsRequest;
import com.manilalinkup.app.models.MarkCompleteRequest;
import com.manilalinkup.app.models.UpdateApplicationStatusRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicantProfileActivity extends BaseActivity {

    private static final int MENU_REJECT = 1;

    private String applicationId;
    private String seekerUid;
    private String chatId;
    private String jobTitle;
    private int status;
    private boolean employerHasCompleted;

    private MaterialButton btnHire, btnComplete, btnInterview, btnChat, btnLoadMore;
    private LinearLayout containerCompletedJobs;
    private TextView tvNoCompletedJobs;
    private ProgressBar progressRatings, progressLoadMore;

    private String nextCursor = null;
    private boolean hasMore = false;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);

        applicationId = getIntent().getStringExtra("APPLICATION_ID");
        seekerUid = getIntent().getStringExtra("SEEKER_UID");
        chatId = getIntent().getStringExtra("CHAT_ID");
        jobTitle = getIntent().getStringExtra("JOB_TITLE");
        status = getIntent().getIntExtra("STATUS", 1);
        employerHasCompleted = getIntent().getBooleanExtra("EMPLOYER_HAS_COMPLETED", false);

        setupToolbar(R.id.toolbar);

        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        if (getSupportActionBar() != null) {
            String name = (firstName != null ? firstName : "") + (lastName != null ? " " + lastName : "");
            getSupportActionBar().setTitle(name.trim());
        }

        ImageView profilePhoto = findViewById(R.id.profile_photo);
        TextView tvName = findViewById(R.id.tv_name);
        ImageView ivStar = findViewById(R.id.iv_star);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvLocation = findViewById(R.id.tv_location);
        LinearLayout layoutStatusChips = findViewById(R.id.layout_status_chips);
        com.google.android.material.chip.Chip chipOpenForWork = findViewById(R.id.chip_open_for_work);

        profilePhoto.setImageResource(R.drawable.ic_person_placeholder);
        if (seekerUid != null) {
            ProfilePhotoCache.getInstance().load(seekerUid, base64 -> {
                if (base64 != null) {
                    byte[] photoBytes = ImageUtils.decodeBase64Safe(base64);
                    Glide.with(this)
                            .load(photoBytes)
                            .placeholder(R.drawable.ic_person_placeholder)
                            .circleCrop()
                            .into(profilePhoto);
                }
            });
        }

        String name = (firstName != null ? firstName : "") + (lastName != null ? " " + lastName : "");
        tvName.setText(name.trim());
        tvLocation.setText(getIntent().getStringExtra("LOCATION") != null ? getIntent().getStringExtra("LOCATION") : "");

        int ratingCount = getIntent().getIntExtra("RATING_COUNT", 0);
        double bayesianAvg = getIntent().getDoubleExtra("BAYESIAN_AVG", 0.0);
        if (ratingCount > 0) {
            tvRating.setText(String.format("%.1f", bayesianAvg));
            setStarTint(ivStar, (float) (bayesianAvg / 5.0));
        } else {
            tvRating.setText("N/A");
            setStarTint(ivStar, 0f);
        }

        boolean isOpenForWork = getIntent().getBooleanExtra("IS_OPEN_FOR_WORK", false);

        if (isOpenForWork) chipOpenForWork.setVisibility(View.VISIBLE);
        if (isOpenForWork) layoutStatusChips.setVisibility(View.VISIBLE);

        btnHire = findViewById(R.id.btn_hire);
        btnComplete = findViewById(R.id.btn_complete);
        btnInterview = findViewById(R.id.btn_interview);
        btnChat = findViewById(R.id.btn_chat);
        containerCompletedJobs = findViewById(R.id.container_completed_jobs);
        tvNoCompletedJobs = findViewById(R.id.tv_no_completed_jobs);
        progressRatings = findViewById(R.id.progress_ratings);
        progressLoadMore = findViewById(R.id.progress_load_more);
        btnLoadMore = findViewById(R.id.btn_load_more);

        btnHire.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Hire applicant?")
                        .setMessage("This will reject all other applicants and archive this job. Continue?")
                        .setPositiveButton("Hire", (d, w) -> updateStatus(5))
                        .setNegativeButton("Cancel", null)
                        .show());

        btnInterview.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Move to interview?")
                        .setMessage("This will move the applicant to the interview stage.")
                        .setPositiveButton("Confirm", (d, w) -> updateStatus(2))
                        .setNegativeButton("Cancel", null)
                        .show());

        btnComplete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Mark as complete?")
                        .setMessage("This marks the job as complete for this applicant.")
                        .setPositiveButton("Complete", (d, w) -> markComplete())
                        .setNegativeButton("Cancel", null)
                        .show());

        btnChat.setOnClickListener(v -> openChat());

        btnLoadMore.setOnClickListener(v -> loadCompletedJobs(nextCursor));

        refreshButtons();
        loadCompletedJobs(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (status == 1 || status == 2) {
            menu.add(0, MENU_REJECT, 0, "Reject")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_REJECT) {
            new AlertDialog.Builder(this)
                    .setTitle("Reject applicant?")
                    .setMessage("This will reject this applicant's application.")
                    .setPositiveButton("Reject", (d, w) -> updateStatus(3))
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshButtons() {
        btnHire.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);
        btnInterview.setVisibility(View.GONE);
        btnChat.setVisibility(View.GONE);

        switch (status) {
            case 1:
                btnHire.setVisibility(View.VISIBLE);
                btnInterview.setVisibility(View.VISIBLE);
                break;
            case 2:
                btnHire.setVisibility(View.VISIBLE);
                btnChat.setVisibility(View.VISIBLE);
                break;
            case 5:
                if (!employerHasCompleted) btnComplete.setVisibility(View.VISIBLE);
                btnChat.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateStatus(int newStatus) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress(newStatus == 5 ? "Hiring applicant..." : newStatus == 3 ? "Rejecting applicant..." : "Updating status...");

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.updateApplicationStatus(new UpdateApplicationStatusRequest(applicationId, newStatus))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        String msg = newStatus == 2 ? "Moved to interview" : newStatus == 3 ? "Applicant rejected" : "Applicant hired!";
                        Toast.makeText(ApplicantProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        status = newStatus;
                        invalidateOptionsMenu();
                        refreshButtons();
                    } else {
                        ErrorUtils.showErrorMessage(ApplicantProfileActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(ApplicantProfileActivity.this, t);
                }
            });
        });
    }

    private void markComplete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Marking as complete...");

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.markApplicationComplete(new MarkCompleteRequest(applicationId))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        Toast.makeText(ApplicantProfileActivity.this, "Marked as complete", Toast.LENGTH_SHORT).show();
                        status = 6;
                        invalidateOptionsMenu();
                        refreshButtons();
                    } else {
                        ErrorUtils.showErrorMessage(ApplicantProfileActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(ApplicantProfileActivity.this, t);
                }
            });
        });
    }

    private void openChat() {
        Intent intent = new Intent(this, ChatThreadEmployer.class);
        intent.putExtra("CHAT_ID", chatId);
        intent.putExtra("APPLICATION_ID", applicationId);
        intent.putExtra("SEEKER_UID", seekerUid);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) intent.putExtra("EMPLOYER_UID", currentUser.getUid());
        if (jobTitle != null) intent.putExtra("JOB_TITLE", jobTitle);
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String counterpartName = (firstName != null ? firstName : "") + (lastName != null ? " " + lastName : "");
        intent.putExtra("COUNTERPART_NAME", counterpartName.trim());
        startActivity(intent);
    }

    private void loadCompletedJobs(String cursor) {
        if (seekerUid == null || isLoadingMore) return;

        if (cursor == null) {
            progressRatings.setVisibility(View.VISIBLE);
        } else {
            isLoadingMore = true;
            btnLoadMore.setVisibility(View.GONE);
            progressLoadMore.setVisibility(View.VISIBLE);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            progressRatings.setVisibility(View.GONE);
            progressLoadMore.setVisibility(View.GONE);
            isLoadingMore = false;
            if (cursor == null) tvNoCompletedJobs.setVisibility(View.VISIBLE);
            return;
        }

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getCompletedJobs(new GetCompletedJobsRequest(seekerUid, null, cursor))
                    .enqueue(new Callback<CompletedJobsResponse>() {
                @Override
                public void onResponse(Call<CompletedJobsResponse> call, Response<CompletedJobsResponse> response) {
                    progressRatings.setVisibility(View.GONE);
                    progressLoadMore.setVisibility(View.GONE);
                    isLoadingMore = false;
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<AppliedJobModel> jobs = response.body().getData();
                        if (cursor == null && jobs.isEmpty()) {
                            tvNoCompletedJobs.setVisibility(View.VISIBLE);
                        } else {
                            for (AppliedJobModel job : jobs) {
                                addCompletedJobItem(job);
                            }
                        }
                        hasMore = response.body().isHasMore();
                        nextCursor = response.body().getNextCursor();
                        btnLoadMore.setVisibility(hasMore ? View.VISIBLE : View.GONE);
                    } else {
                        if (cursor == null) tvNoCompletedJobs.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<CompletedJobsResponse> call, Throwable t) {
                    progressRatings.setVisibility(View.GONE);
                    progressLoadMore.setVisibility(View.GONE);
                    isLoadingMore = false;
                    if (cursor == null) tvNoCompletedJobs.setVisibility(View.VISIBLE);
                    btnLoadMore.setVisibility(hasMore ? View.VISIBLE : View.GONE);
                }
            });
        });
    }

    private void addCompletedJobItem(AppliedJobModel application) {
        View item = getLayoutInflater().inflate(R.layout.item_completed_job_rating, containerCompletedJobs, false);

        TextView tvTitle = item.findViewById(R.id.tv_completed_job_title);
        TextView tvDescription = item.findViewById(R.id.tv_completed_job_description);
        ChipGroup chipGroupTags = item.findViewById(R.id.chip_group_completed_job_tags);

        com.manilalinkup.app.models.JobModel job = application.getJob();
        tvTitle.setText(job != null && job.getTitle() != null ? job.getTitle() : "Completed Job");

        if (job != null && job.getDescription() != null && !job.getDescription().isEmpty()) {
            tvDescription.setText(job.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        }

        List<String> tags = job != null ? job.getTags() : null;
        if (tags != null && !tags.isEmpty()) {
            chipGroupTags.setVisibility(View.VISIBLE);
            SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
                @Override
                public void onAvailable(List<com.manilalinkup.app.models.ServiceTagModel> serviceTags) {
                    Map<String, String> labelsById = SessionCache.getInstance().getServiceTagLabelsById();
                    for (String tagId : tags) {
                        String label = labelsById.get(tagId);
                        if (label == null) continue;
                        Chip chip = new Chip(ApplicantProfileActivity.this);
                        chip.setText(label);
                        chip.setChipBackgroundColorResource(R.color.manila_blue);
                        chip.setTextColor(Color.WHITE);
                        chip.setCheckable(false);
                        chip.setClickable(false);
                        chipGroupTags.addView(chip);
                    }
                }

                @Override
                public void onError() {}
            });
        }

        containerCompletedJobs.addView(item);
    }

    private void setStarTint(ImageView iv, float fraction) {
        if (iv.getDrawable() == null) return;
        Drawable d = DrawableCompat.wrap(iv.getDrawable().mutate());
        int grey   = Color.parseColor("#BDBDBD");
        int yellow = Color.parseColor("#FFC107");
        float f = Math.max(0f, Math.min(1f, fraction));
        int r = (int) (Color.red(grey)   + f * (Color.red(yellow)   - Color.red(grey)));
        int g = (int) (Color.green(grey) + f * (Color.green(yellow) - Color.green(grey)));
        int b = (int) (Color.blue(grey)  + f * (Color.blue(yellow)  - Color.blue(grey)));
        DrawableCompat.setTint(d, Color.rgb(r, g, b));
        iv.setImageDrawable(d);
    }
}
