package com.manilalinkup.app.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.CreateJobRequest;
import com.manilalinkup.app.utilities.AddressAutocompleteHelper;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerAddJobActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewEmployer;
    TextInputLayout titleLayout, locationLayout, descriptionLayout, expiresAtLayout;
    TextInputEditText titleInput, descriptionInput, salaryInput, expiresAtInput, durationAmountInput;
    AutoCompleteTextView rateDropdown;
    ExtendedFloatingActionButton postJobButton;
    TextView greetingNameText;
    ProgressDialog progressDialog;
    EditText locationInput;

    private String formattedExpiresAt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_add_job);

        titleLayout = findViewById(R.id.text_input_layout_job_title);
        locationLayout = findViewById(R.id.text_input_layout_job_location);
        descriptionLayout = findViewById(R.id._text_input_job_description);
        expiresAtLayout = findViewById(R.id.text_input_layout_expires_at);

        titleInput = findViewById(R.id.edit_text_job_title);
        descriptionInput = findViewById(R.id.edit_text_job_description);
        salaryInput = findViewById(R.id.edit_text_salary);
        durationAmountInput = findViewById(R.id.edit_text_duration_amount);
        expiresAtInput = findViewById(R.id.edit_text_expires_at);
        rateDropdown = findViewById(R.id.auto_complete_rate);
        postJobButton = findViewById(R.id.button_post_job);
        greetingNameText = findViewById(R.id.textview_greeting_name_employer);
        locationInput = findViewById(R.id.edit_text_location);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting job...");
        progressDialog.setCancelable(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            greetingNameText.setText(currentUser.getDisplayName());
        }

        String[] durationUnits = {"hour(s)", "day(s)", "week(s)", "month(s)", "year(s)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, durationUnits);
        rateDropdown.setAdapter(adapter);

        expiresAtInput.setOnClickListener(v -> showDatePicker());
        expiresAtInput.setFocusable(false);

        AddressAutocompleteHelper.attachDistrictAutocomplete(locationInput);

        postJobButton.setOnClickListener(v -> postJob());

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_add_job);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home) {
                startActivity(new Intent(EmployerAddJobActivity.this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_notifications) {
                startActivity(new Intent(EmployerAddJobActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_chat) {
                startActivity(new Intent(EmployerAddJobActivity.this, ChatEmployerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(EmployerAddJobActivity.this, EmployerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            formattedExpiresAt = apiFormat.format(selected.getTime());
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            expiresAtInput.setText(displayFormat.format(selected.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void postJob() {
        String title = titleInput.getText() != null ? titleInput.getText().toString().trim() : "";
        String location = locationInput.getText() != null ? locationInput.getText().toString().trim() : "";
        String description = descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";
        String salaryStr = salaryInput.getText() != null ? salaryInput.getText().toString().trim() : "";
        String durationAmountStr = durationAmountInput.getText() != null ? durationAmountInput.getText().toString().trim() : "";
        String durationUnit = rateDropdown.getText().toString().trim();

        titleLayout.setError(null);
        locationLayout.setError(null);
        descriptionLayout.setError(null);
        expiresAtLayout.setError(null);

        if (title.isEmpty()) {
            titleLayout.setError("Job title is required.");
            titleInput.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            locationLayout.setError("Job location is required.");
            locationInput.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            descriptionLayout.setError("Job description is required.");
            descriptionInput.requestFocus();
            return;
        }
        if (formattedExpiresAt.isEmpty()) {
            expiresAtLayout.setError("Expiry date is required.");
            expiresAtInput.requestFocus();
            return;
        }
        if (salaryStr.isEmpty()) {
            Toast.makeText(this, "Salary amount is required.", Toast.LENGTH_SHORT).show();
            salaryInput.requestFocus();
            return;
        }
        if (durationAmountStr.isEmpty()) {
            Toast.makeText(this, "Duration amount is required.", Toast.LENGTH_SHORT).show();
            durationAmountInput.requestFocus();
            return;
        }
        if (durationUnit.isEmpty()) {
            Toast.makeText(this, "Duration unit is required.", Toast.LENGTH_SHORT).show();
            rateDropdown.requestFocus();
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid salary amount.", Toast.LENGTH_SHORT).show();
            salaryInput.requestFocus();
            return;
        }

        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
            if (tokenTask.isSuccessful()) {
                String token = tokenTask.getResult().getToken();
                String uid = user.getUid();
                String duration = durationAmountStr + " " + durationUnit;
                submitCreateJob(token, uid, title, description, location, formattedExpiresAt, duration, salary);
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitCreateJob(String token, String employerUid, String title, String description,
                                  String location, String expiresAt, String duration, double salary) {
        CreateJobRequest request = new CreateJobRequest(title, description, employerUid, expiresAt, duration, location, salary);
        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.createJob(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(EmployerAddJobActivity.this, "Job posted!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmployerAddJobActivity.this, EmployerDashboard.class));
                    finish();
                } else {
                    ErrorUtils.showErrorMessage(EmployerAddJobActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showThrowableError(EmployerAddJobActivity.this, t);
            }
        });
    }
}
