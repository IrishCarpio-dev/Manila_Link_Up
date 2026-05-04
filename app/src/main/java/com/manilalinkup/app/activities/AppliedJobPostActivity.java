package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;

import com.manilalinkup.app.utilities.ImageUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.MarkCompleteRequest;
import com.manilalinkup.app.models.WithdrawApplicationRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppliedJobPostActivity extends BaseActivity {

    private MaterialButton btnCancel;
    private MaterialButton btnChat;
    private MaterialButton btnRate;

    private String applicationId;
    private String employerName;
    private String chatId;
    private String seekerUid;
    private int currentStatus;
    private boolean seekerHasCompleted;
    private boolean isRateEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applied_job_post);

        applicationId      = getIntent().getStringExtra("APPLICATION_ID");
        employerName       = getIntent().getStringExtra("EMPLOYER_NAME");
        chatId             = getIntent().getStringExtra("CHAT_ID");
        seekerUid          = getIntent().getStringExtra("SEEKER_UID");
        currentStatus      = getIntent().getIntExtra("STATUS", 1);
        seekerHasCompleted = getIntent().getBooleanExtra("SEEKER_HAS_COMPLETED", false);
        isRateEnabled      = !getIntent().hasExtra("IS_RATE_ENABLED") || getIntent().getBooleanExtra("IS_RATE_ENABLED", false);

        String jobTitle      = getIntent().getStringExtra("JOB_TITLE");
        String location      = getIntent().getStringExtra("LOCATION");
        double salary        = getIntent().getDoubleExtra("SALARY", 0.0);
        String duration      = getIntent().getStringExtra("DURATION");
        String description   = getIntent().getStringExtra("DESCRIPTION");
        String expiresAt     = getIntent().getStringExtra("EXPIRES_AT");
        String createdAt     = getIntent().getStringExtra("CREATED_AT");
        String employerPhoto = getIntent().getStringExtra("EMPLOYER_PHOTO");

        setupToolbar(R.id.toolbar);


        TextView tvToolbarEmployerName = findViewById(R.id.text_view_employer_name_job_post);
        TextView tvJobTitle            = findViewById(R.id.text_view_employer_job_title_placeholder);
        TextView tvEmployerName        = findViewById(R.id.text_view_employer_name_placeholder);
        TextView tvLocation            = findViewById(R.id.text_view_location_placeholder);
        TextView tvSalary              = findViewById(R.id.text_view_salary_placeholder);
        TextView tvCalendar            = findViewById(R.id.text_view_calendar_placeholder);
        TextView tvAppliedDate         = findViewById(R.id.applied_date);
        TextView tvDescription         = findViewById(R.id.text_view_job_description_placeholder);
        TextView tvStatusBadge         = findViewById(R.id.applied_status_badge);
        ImageView ivEmployerPhoto      = findViewById(R.id.image_view_employee_profile_picture_placeholder);

        if (employerName != null) tvToolbarEmployerName.setText(employerName);
        if (jobTitle != null)     tvJobTitle.setText(jobTitle);
        if (employerName != null) tvEmployerName.setText(employerName);
        if (location != null)     tvLocation.setText(location);
        if (duration != null)     tvCalendar.setText(duration);
        if (description != null)  tvDescription.setText(description);

        if (salary > 0) {
            tvSalary.setText(String.format(Locale.US, "₱%.0f/hr", salary));
        } else {
            tvSalary.setVisibility(View.GONE);
            findViewById(R.id.money_logo).setVisibility(View.GONE);
        }

        if (createdAt != null) {
            tvAppliedDate.setText("Applied: " + formatDate(createdAt));
        }

        if (employerPhoto != null && !employerPhoto.isEmpty()) {
            byte[] photoBytes = ImageUtils.decodeBase64Safe(employerPhoto);
            Glide.with(this)
                    .load(photoBytes)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(ivEmployerPhoto);
        }

        applyStatusBadge(tvStatusBadge, currentStatus);

        btnCancel = findViewById(R.id.button_cancel_application);
        btnChat   = findViewById(R.id.btn_chat);
        btnRate   = findViewById(R.id.btn_rate);

        updateActionVisibility();
    }

    private void applyStatusBadge(TextView badge, int status) {
        switch (status) {
            case 2:
                badge.setText("INTERVIEW");
                badge.setBackgroundResource(R.drawable.bg_status_interview);
                badge.setTextColor(android.graphics.Color.parseColor("#1565C0"));
                break;
            case 3:
                badge.setText("REJECTED");
                badge.setBackgroundResource(R.drawable.bg_status_rejected);
                badge.setTextColor(android.graphics.Color.parseColor("#B71C1C"));
                break;
            case 5:
                badge.setText("HIRED");
                badge.setBackgroundResource(R.drawable.bg_status_accepted);
                badge.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            case 6:
                badge.setText("COMPLETED");
                badge.setBackgroundResource(R.drawable.bg_status_accepted);
                badge.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            default:
                badge.setText("PENDING");
                badge.setBackgroundResource(R.drawable.bg_status_pending);
                badge.setTextColor(android.graphics.Color.parseColor("#E65100"));
                break;
        }
    }

    private void updateActionVisibility() {
        if (currentStatus == 1 || currentStatus == 2) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText("Withdraw");
            btnCancel.setOnClickListener(v -> confirmCancel());
        } else if (currentStatus == 5 && !seekerHasCompleted) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText("Complete");
            int gray = ContextCompat.getColor(this, R.color.button_gray);
            btnCancel.setBackgroundTintList(ColorStateList.valueOf(gray));
            btnCancel.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnCancel.setStrokeWidth(0);
            btnCancel.setRippleColor(ColorStateList.valueOf(android.graphics.Color.parseColor("#40FFFFFF")));
            btnCancel.setOnClickListener(v -> confirmMarkComplete());
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        btnChat.setVisibility((currentStatus == 2 || currentStatus == 5) && chatId != null ? View.VISIBLE : View.GONE);
        btnChat.setOnClickListener(v -> openChat());

        btnRate.setVisibility(currentStatus == 6 && isRateEnabled ? View.VISIBLE : View.GONE);
        btnRate.setOnClickListener(v -> openRating());
    }

    private void confirmCancel() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Application?")
                .setMessage("This will withdraw your application and cannot be undone.")
                .setPositiveButton("Cancel Application", (d, w) -> withdrawApplication())
                .setNegativeButton("Keep", null)
                .show();
    }

    private void withdrawApplication() {
        if (applicationId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Cancelling application...");

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.withdrawApplication(new WithdrawApplicationRequest(applicationId))
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        Toast.makeText(AppliedJobPostActivity.this, "Application cancelled.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        ErrorUtils.showErrorMessage(AppliedJobPostActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(AppliedJobPostActivity.this, t);
                }
            });
        });
    }

    private void confirmMarkComplete() {
        new AlertDialog.Builder(this)
                .setTitle("Mark Job Complete?")
                .setMessage("Confirm that you have finished this job.")
                .setPositiveButton("Mark Complete", (d, w) -> markComplete())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void markComplete() {
        if (applicationId == null) return;
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
                    if (response.isSuccessful() && response.body() != null) {
                        ApplicationModel updated = response.body().getData();
                        currentStatus = updated.getStatus() != null ? updated.getStatus() : currentStatus;
                        seekerHasCompleted = true;
                        if (currentStatus == 6) isRateEnabled = true;
                        Toast.makeText(AppliedJobPostActivity.this, "Marked as complete!", Toast.LENGTH_SHORT).show();
                        TextView tvStatusBadge = findViewById(R.id.applied_status_badge);
                        applyStatusBadge(tvStatusBadge, currentStatus);
                        updateActionVisibility();
                    } else {
                        ErrorUtils.showErrorMessage(AppliedJobPostActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(AppliedJobPostActivity.this, t);
                }
            });
        });
    }

    private void openRating() {
        Intent intent = new Intent(this, SubmitRatingActivity.class);
        intent.putExtra("APPLICATION_ID", applicationId);
        intent.putExtra("COUNTERPART_NAME", employerName != null ? employerName : "Employer");
        startActivity(intent);
    }

    private void openChat() {
        Intent intent = new Intent(this, ChatThreadSeeker.class);
        intent.putExtra("CHAT_ID", chatId);
        intent.putExtra("SEEKER_UID", seekerUid);
        intent.putExtra("JOB_TITLE", getIntent().getStringExtra("JOB_TITLE"));
        intent.putExtra("COUNTERPART_NAME", employerName);
        startActivity(intent);
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
