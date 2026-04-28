package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.ArchiveJobRequest;
import com.manilalinkup.app.models.MarkCompleteRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerViewJobPost extends BaseActivity {

    private MaterialButton viewApplicantsButton;
    private Button btnMarkComplete;
    private Button btnRate;

    private String jobId;
    private String jobTitle;
    private String description;
    private String location;
    private String duration;
    private double salary;
    private ArrayList<String> tagIds;
    private String applicationId;
    private String seekerName;
    private int currentStatus;
    private boolean employerHasCompleted;
    private boolean isOwner;
    private boolean isArchived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_job_post);

        jobId                = getIntent().getStringExtra("JOB_ID");
        applicationId        = getIntent().getStringExtra("APPLICATION_ID");
        seekerName           = getIntent().getStringExtra("SEEKER_NAME");
        currentStatus        = getIntent().getIntExtra("STATUS", 1);
        employerHasCompleted = getIntent().getBooleanExtra("EMPLOYER_HAS_COMPLETED", false);
        isOwner              = getIntent().getBooleanExtra("IS_OWNER", false);
        isArchived           = getIntent().getBooleanExtra("IS_ARCHIVED", false);

        jobTitle              = getIntent().getStringExtra("JOB_TITLE");
        String employerName   = getIntent().getStringExtra("EMPLOYER_NAME");
        location              = getIntent().getStringExtra("LOCATION");
        duration              = getIntent().getStringExtra("DURATION");
        salary                = getIntent().getDoubleExtra("SALARY", 0.0);
        description           = getIntent().getStringExtra("DESCRIPTION");
        String expiresAt      = getIntent().getStringExtra("EXPIRES_AT");
        String howLongPosted  = getIntent().getStringExtra("HOW_LONG_POSTED");
        String employerPhoto  = getIntent().getStringExtra("EMPLOYER_PHOTO");
        tagIds                = getIntent().getStringArrayListExtra("TAG_IDS");

        setupToolbar(R.id.toolbar);

        TextView tvToolbarEmployerName = findViewById(R.id.text_view_employer_name_job_post);
        TextView tvJobTitle            = findViewById(R.id.text_view_employer_job_title_placeholder);
        TextView tvEmployerName        = findViewById(R.id.text_view_employer_name_placeholder);
        TextView tvLocation            = findViewById(R.id.text_view_location_placeholder);
        TextView tvSalary              = findViewById(R.id.text_view_salary_placeholder);
        TextView tvCalendar            = findViewById(R.id.text_view_calendar_placeholder);
        TextView tvExpiresAt           = findViewById(R.id.text_view_expires_at);
        TextView tvDescription         = findViewById(R.id.text_view_job_description_placeholder);
        TextView tvHowLongPosted       = findViewById(R.id.text_view_how_long_job_post_posted_placeholder);
        ImageView ivProfilePicture     = findViewById(R.id.image_view_employee_profile_picture_placeholder);
        ChipGroup chipGroupTags        = findViewById(R.id.chip_group_tags);

        if (employerName != null) tvToolbarEmployerName.setText(employerName);
        if (jobTitle != null)     tvJobTitle.setText(jobTitle);
        if (employerName != null) tvEmployerName.setText(employerName);
        if (location != null)     tvLocation.setText(location);
        if (duration != null)     tvCalendar.setText(duration);
        if (description != null)  tvDescription.setText(description);
        if (howLongPosted != null) tvHowLongPosted.setText(howLongPosted);

        if (salary > 0) {
            tvSalary.setText(String.format(Locale.US, "â‚±%.0f/day", salary));
        } else {
            tvSalary.setVisibility(View.GONE);
            findViewById(R.id.money_logo).setVisibility(View.GONE);
        }

        if (expiresAt != null) {
            tvExpiresAt.setText("Expires: " + formatDate(expiresAt));
        } else {
            tvExpiresAt.setVisibility(View.GONE);
            findViewById(R.id.expires_logo).setVisibility(View.GONE);
        }

        if (employerPhoto != null && !employerPhoto.isEmpty()) {
            Glide.with(this)
                    .load(employerPhoto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfilePicture);
        }

        populateTags(chipGroupTags, tagIds);


        viewApplicantsButton = findViewById(R.id.button_view_applicants);
        viewApplicantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployerListOfApplicants.class);
            intent.putExtra("JOB_ID", jobId);
            intent.putExtra("JOB_TITLE", jobTitle);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnMarkComplete = findViewById(R.id.btn_mark_complete);
        btnRate         = findViewById(R.id.btn_rate);

        updateActionVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isOwner) {
            getMenuInflater().inflate(R.menu.menu_employer_view_job_post, menu);
            menu.findItem(R.id.menu_archive_job).setVisible(!isArchived);
            menu.findItem(R.id.menu_repost_job).setVisible(isArchived);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_archive_job) {
            new AlertDialog.Builder(this)
                    .setTitle("Archive Job")
                    .setMessage("Are you sure you want to archive \"" + jobTitle + "\"?")
                    .setPositiveButton("Archive", (d, w) -> archiveJob())
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        if (item.getItemId() == R.id.menu_repost_job) {
            repostJob();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void archiveJob() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Archiving job...");

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.archiveJob(new ArchiveJobRequest(jobId))
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        Toast.makeText(EmployerViewJobPost.this, "Job archived", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        ErrorUtils.showErrorMessage(EmployerViewJobPost.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(EmployerViewJobPost.this, t);
                }
            });
        });
    }

    private void repostJob() {
        Intent intent = new Intent(this, EmployerAddJobActivity.class);
        intent.putExtra("is_repost", true);
        intent.putExtra("repost_title", jobTitle);
        intent.putExtra("repost_description", description);
        intent.putExtra("repost_location", location);
        if (salary > 0) {
            intent.putExtra("repost_salary", String.valueOf((long) salary));
        }
        intent.putExtra("repost_duration", duration);
        if (tagIds != null) {
            intent.putStringArrayListExtra("repost_tag_ids", tagIds);
        }
        startActivity(intent);
    }

    private void populateTags(ChipGroup chipGroup, List<String> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
            @Override
            public void onAvailable(List<com.manilalinkup.app.models.ServiceTagModel> tags) {
                Map<String, String> labelsById = SessionCache.getInstance().getServiceTagLabelsById();
                chipGroup.removeAllViews();
                for (String id : tagIds) {
                    String label = labelsById.get(id);
                    if (label == null) continue;
                    Chip chip = new Chip(EmployerViewJobPost.this);
                    chip.setText(label);
                    chip.setChipBackgroundColorResource(R.color.manila_blue);
                    chip.setTextColor(Color.WHITE);
                    chip.setCheckable(false);
                    chip.setClickable(false);
                    chip.setFocusable(false);
                    chip.setEnsureMinTouchTargetSize(false);
                    chipGroup.addView(chip);
                }
            }

            @Override
            public void onError() {}
        });
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            Date date = sdf.parse(isoDate);
            return new SimpleDateFormat("MMM d, yyyy", Locale.US).format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }

    private void updateActionVisibility() {
        if (btnMarkComplete != null) {
            btnMarkComplete.setVisibility(currentStatus == 5 && !employerHasCompleted ? View.VISIBLE : View.GONE);
            btnMarkComplete.setOnClickListener(v -> markComplete());
        }

        if (btnRate != null) {
            btnRate.setVisibility(currentStatus == 6 ? View.VISIBLE : View.GONE);
            btnRate.setOnClickListener(v -> openRating());
        }
    }

    private void markComplete() {
        if (applicationId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Marking as complete...");

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.markApplicationComplete(new MarkCompleteRequest(applicationId))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                    hideProgress();
                    if (response.isSuccessful() && response.body() != null) {
                        ApplicationModel updated = response.body().getData();
                        currentStatus = updated.getStatus() != null ? updated.getStatus() : currentStatus;
                        employerHasCompleted = true;
                        Toast.makeText(EmployerViewJobPost.this, "Marked as complete!", Toast.LENGTH_SHORT).show();
                        updateActionVisibility();
                    } else {
                        ErrorUtils.showErrorMessage(EmployerViewJobPost.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(EmployerViewJobPost.this, t);
                }
            });
        });
    }

    private void openRating() {
        Intent intent = new Intent(this, SubmitRatingActivity.class);
        intent.putExtra("APPLICATION_ID", applicationId);
        intent.putExtra("COUNTERPART_NAME", seekerName != null ? seekerName : "Worker");
        startActivity(intent);
    }
}
