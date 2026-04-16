package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class SeekerJobDetailsActivity extends AppCompatActivity {
    private JobPostDashboardModel currentJobPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_job_details);

        currentJobPost = JobPostDashboardModel.fromIntent(getIntent());
        if (currentJobPost == null && !JobPostRepository.getSeekerDashboardItems().isEmpty()) {
            currentJobPost = JobPostRepository.getSeekerDashboardItems().get(0);
        }

        bindCurrentJob();

        MaterialButton btnArchive = findViewById(R.id.button_archive);
        MaterialButton btnEdit = findViewById(R.id.button_edit);

        if (btnArchive != null) {
            btnArchive.setText("Apply");
            btnArchive.setOnClickListener(v -> {
                if (currentJobPost == null) {
                    return;
                }

                Toast.makeText(this, "Application sent.", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnEdit != null) {
            btnEdit.setVisibility(View.GONE);
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_activity);
            bottomNavigation.setOnItemSelectedListener(item -> true);
        }
    }

    private void bindCurrentJob() {
        if (currentJobPost == null) {
            return;
        }

        ImageView employerProfileImage = findViewById(R.id.image_employer_profile_detail);
        TextView employerName = findViewById(R.id.text_employer_name);
        TextView employerLocationHeader = findViewById(R.id.text_employer_location_header);
        TextView jobTitle = findViewById(R.id.text_job_title_detail);
        TextView postedAgo = findViewById(R.id.text_job_posted_ago_detail);
        TextView jobEmployerName = findViewById(R.id.text_job_employer_detail);
        TextView jobLocation = findViewById(R.id.text_job_location_detail);
        TextView jobDuration = findViewById(R.id.text_job_duration_detail);

        if (employerProfileImage != null) {
            employerProfileImage.setImageResource(currentJobPost.getEmployerProfilePicture());
        }
        if (employerName != null) {
            employerName.setText(currentJobPost.getEmployerName());
        }
        if (employerLocationHeader != null) {
            employerLocationHeader.setText(currentJobPost.getJobPostLocation());
        }
        if (jobTitle != null) {
            jobTitle.setText(currentJobPost.getJobTitle());
        }
        if (postedAgo != null) {
            postedAgo.setText(currentJobPost.getHowLongJobIsPosted());
        }
        if (jobEmployerName != null) {
            jobEmployerName.setText(currentJobPost.getEmployerName());
        }
        if (jobLocation != null) {
            jobLocation.setText(currentJobPost.getJobPostLocation());
        }
        if (jobDuration != null) {
            jobDuration.setText(currentJobPost.getJobDuration());
        }
    }
}
