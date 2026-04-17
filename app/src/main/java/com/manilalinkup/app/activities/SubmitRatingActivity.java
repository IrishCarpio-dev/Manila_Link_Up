package com.manilalinkup.app.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.models.SubmitRatingRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitRatingActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RatingBar ratingBar;
    private EditText commentInput;
    private Button submitButton;
    private TextView titleText;

    private String applicationId;
    private String counterpartName;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_rating);

        applicationId   = getIntent().getStringExtra("APPLICATION_ID");
        counterpartName = getIntent().getStringExtra("COUNTERPART_NAME");
        float existingScore   = getIntent().getFloatExtra("EXISTING_SCORE", 0f);
        String existingComment = getIntent().getStringExtra("EXISTING_COMMENT");
        boolean isLocked = getIntent().getBooleanExtra("IS_LOCKED", false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        titleText    = findViewById(R.id.text_rating_title);
        ratingBar    = findViewById(R.id.rating_bar);
        commentInput = findViewById(R.id.edit_rating_comment);
        submitButton = findViewById(R.id.btn_submit_rating);

        if (counterpartName != null) {
            titleText.setText("Rate " + counterpartName);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (existingScore > 0) {
            ratingBar.setRating(existingScore);
        }
        if (existingComment != null) {
            commentInput.setText(existingComment);
        }

        if (isLocked) {
            ratingBar.setIsIndicator(true);
            commentInput.setEnabled(false);
            submitButton.setVisibility(View.GONE);
            titleText.setText("Rating locked after 24 hours");
        } else {
            boolean isEdit = existingScore > 0;
            submitButton.setText(isEdit ? "Update rating" : "Submit rating");
            submitButton.setOnClickListener(v -> submitRating());
        }
    }

    private void submitRating() {
        float score = ratingBar.getRating();
        if (score == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = commentInput.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        progressDialog.setMessage("Submitting rating...");
        progressDialog.show();

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.submitRating(new SubmitRatingRequest(applicationId, (int) score, comment))
                    .enqueue(new Callback<ApiResponse<RatingModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<RatingModel>> call, Response<ApiResponse<RatingModel>> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(SubmitRatingActivity.this, "Rating submitted!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        ErrorUtils.showErrorMessage(SubmitRatingActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<RatingModel>> call, Throwable t) {
                    progressDialog.dismiss();
                    ErrorUtils.showThrowableError(SubmitRatingActivity.this, t);
                }
            });
        });
    }
}
