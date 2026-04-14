package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.utilities.JobPostRepository;

import java.util.List;

public class SeekerJobPostActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    private JobPostDashboardModel selectedJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_job_post);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        selectedJob = JobPostDashboardModel.fromIntent(getIntent());
        if (selectedJob == null) {
            List<JobPostDashboardModel> fallbackJobs = JobPostRepository.getSeekerDashboardJobs(this);
            if (!fallbackJobs.isEmpty()) {
                selectedJob = fallbackJobs.get(0);
            }
        }

        bindJobDetails();
        setupActions();
    }

    private void bindJobDetails() {
        if (selectedJob == null) {
            return;
        }

        TextView toolbarTitle = findViewById(R.id.text_view_employer_name_job_post);
        TextView jobTitle = findViewById(R.id.text_view_employer_job_title_placeholder);
        TextView employerName = findViewById(R.id.text_view_employer_name_placeholder);
        TextView location = findViewById(R.id.text_view_location_placeholder);
        TextView duration = findViewById(R.id.text_view_calendar_placeholder);
        TextView postedAgo = findViewById(R.id.text_view_how_long_job_post_posted_placeholder);
        TextView salary = findViewById(R.id.text_view_salary_placeholder);
        TextView description = findViewById(R.id.text_view_job_description_placeholder);
        ImageView employerProfilePicture = findViewById(R.id.image_view_employee_profile_picture_placeholder);

        toolbarTitle.setText(selectedJob.getEmployerName());
        jobTitle.setText(selectedJob.getJobTitle());
        employerName.setText(selectedJob.getEmployerName());
        location.setText(selectedJob.getJobPostLocation());
        duration.setText(selectedJob.getJob_duration());
        postedAgo.setText(selectedJob.getHowLongJobIsPosted());
        salary.setText("Salary to be discussed");
        description.setText("This opportunity from " + selectedJob.getEmployerName()
                + " is also visible in the employer archive and seeker dashboard. "
                + "Work setup: " + selectedJob.getJob_duration()
                + ". Location: " + selectedJob.getJobPostLocation() + ".");

        Glide.with(this)
                .load(selectedJob.getEmployerProfilePicture())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder)
                .centerCrop()
                .into(employerProfilePicture);
    }

    private void setupActions() {
        MaterialButton applyButton = findViewById(R.id.btn_apply);
        MaterialButton saveButton = findViewById(R.id.btn_save);

        applyButton.setOnClickListener(v ->
                Toast.makeText(this, "Application sent.", Toast.LENGTH_SHORT).show()
        );

        saveButton.setOnClickListener(v ->
                Toast.makeText(this, "Job saved.", Toast.LENGTH_SHORT).show()
        );
    }
}
