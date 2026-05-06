package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import android.util.Base64;
import com.manilalinkup.app.utilities.ImageUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApplyJobRequest;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.models.UserProfileModel;
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

public class SeekerJobPostActivity extends BaseActivity {

    private MaterialButton btnApply;

    private String jobId;
    private String jobTitle;
    private String employerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_job_post);

        jobId                    = getIntent().getStringExtra("JOB_ID");
        jobTitle                 = getIntent().getStringExtra("JOB_TITLE");
        employerName             = getIntent().getStringExtra("EMPLOYER_NAME");
        String location          = getIntent().getStringExtra("LOCATION");
        String duration          = getIntent().getStringExtra("DURATION");
        double salary            = getIntent().getDoubleExtra("SALARY", 0.0);
        String description       = getIntent().getStringExtra("DESCRIPTION");
        String expiresAt         = getIntent().getStringExtra("EXPIRES_AT");
        String howLongPosted     = getIntent().getStringExtra("HOW_LONG_POSTED");
        String employerPhoto     = getIntent().getStringExtra("EMPLOYER_PHOTO");
        ArrayList<String> tagIds = getIntent().getStringArrayListExtra("TAG_IDS");

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
            tvSalary.setText(String.format(Locale.US, "₱%.0f/hr", salary));
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
            byte[] photoBytes = ImageUtils.decodeBase64Safe(employerPhoto);
            Glide.with(this)
                    .load(photoBytes)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(ivProfilePicture);
        }

        populateTags(chipGroupTags, tagIds);


        btnApply = findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> confirmAndApply());
    }

    private void confirmAndApply() {
        new AlertDialog.Builder(this)
                .setTitle("Apply for Job")
                .setMessage("Are you sure you want to apply for this job?")
                .setPositiveButton("Apply", (d, w) -> applyForJob())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyForJob() {
        if (jobId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Submitting application...");

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.applyJob(new ApplyJobRequest(jobId))
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        Toast.makeText(SeekerJobPostActivity.this, "Application submitted!", Toast.LENGTH_SHORT).show();
                        btnApply.setEnabled(false);
                        btnApply.setText("Applied");
                        btnApply.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFBDBDBD));
                        btnApply.setTextColor(0xFF757575);
                        Intent result = new Intent();
                        result.putExtra("JOB_ID", jobId);
                        setResult(RESULT_OK, result);
                    } else {
                        ErrorUtils.showErrorMessage(SeekerJobPostActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(SeekerJobPostActivity.this, t);
                }
            });
        });
    }

    private void populateTags(ChipGroup chipGroup, List<String> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
            @Override
            public void onAvailable(List<ServiceTagModel> tags) {
                Map<String, String> labelsById = SessionCache.getInstance().getServiceTagLabelsById();
                chipGroup.removeAllViews();
                for (String id : tagIds) {
                    String label = labelsById.get(id);
                    if (label == null) continue;
                    Chip chip = new Chip(SeekerJobPostActivity.this);
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
            String datePart = isoDate.split("T")[0];
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(datePart);
            return new SimpleDateFormat("MMM d, yyyy", Locale.US).format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }
}
