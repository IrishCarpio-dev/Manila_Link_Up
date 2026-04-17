package com.manilalinkup.app.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.CreateJobRequest;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.utilities.AddressAutocompleteHelper;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    TextView greetingNameText, tvServiceTagsError;
    ProgressDialog progressDialog;
    EditText locationInput;
    ChipGroup chipGroupServiceTags;

    private String formattedExpiresAt = "";
    private final List<ServiceTagModel> serviceTagList = new ArrayList<>();
    private final Set<String> selectedTagIds = new LinkedHashSet<>();

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
        chipGroupServiceTags = findViewById(R.id.chip_group_service_tags);
        tvServiceTagsError = findViewById(R.id.text_view_service_tags_error);

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

        loadServiceTags();

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

    private void loadServiceTags() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) return;
            String token = tokenTask.getResult().getToken();
            ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);
            apiService.getServiceTags().enqueue(new Callback<ApiResponse<List<ServiceTagModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ServiceTagModel>>> call, Response<ApiResponse<List<ServiceTagModel>>> response) {
                    if (!response.isSuccessful() || response.body() == null) return;
                    serviceTagList.clear();
                    serviceTagList.addAll(response.body().getData());
                    refreshServiceTagsDisplay();
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ServiceTagModel>>> call, Throwable t) {
                    Toast.makeText(EmployerAddJobActivity.this, "Failed to load service tags.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void refreshServiceTagsDisplay() {
        chipGroupServiceTags.removeAllViews();

        for (String tagId : selectedTagIds) {
            ServiceTagModel tag = null;
            for (ServiceTagModel t : serviceTagList) {
                if (t.getId().equals(tagId)) {
                    tag = t;
                    break;
                }
            }
            if (tag == null) continue;

            Chip chip = new Chip(this);
            chip.setText(tag.getLabel());

            chip.setChipBackgroundColorResource(R.color.chip_background_state_list);
            chip.setTextColor(Color.WHITE);

            chip.setTag(tag.getId());
            chip.setCheckable(true);
            chip.setChecked(true);
            chip.setCheckedIconVisible(false);
            final String idToRemove = tagId;
            chip.setOnCloseIconClickListener(v -> {
                selectedTagIds.remove(idToRemove);
                refreshServiceTagsDisplay();
            });
            chipGroupServiceTags.addView(chip);
            chip.setCloseIconTint(ColorStateList.valueOf(Color.WHITE));
        }

        Chip addChip = new Chip(this);
        addChip.setText("+");
        addChip.setCheckable(false);
        addChip.setOnClickListener(v -> showServiceTagModal());
        chipGroupServiceTags.addView(addChip);
    }

    private void showServiceTagModal() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_service_tags, null);
        ChipGroup chipGroupModal = dialogView.findViewById(R.id.chipGroupServiceTagsModal);
        ImageButton btnClose = dialogView.findViewById(R.id.btnCloseServiceTags);
        Button btnSave = dialogView.findViewById(R.id.btnSaveServiceTags);

        for (ServiceTagModel tag : serviceTagList) {
            Chip chip = new Chip(this);
            chip.setText(tag.getLabel());
            chip.setTag(tag.getId());
            chip.setCheckable(true);
            chip.setChecked(selectedTagIds.contains(tag.getId()));

            chip.setCheckedIconVisible(false);

            chip.setChipBackgroundColorResource(R.color.chip_background_state_list);
            // Safety check for older Android versions
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                chip.setTextColor(getResources().getColorStateList(R.color.chip_text_state_list, getTheme()));
            } else {
                chip.setTextColor(getResources().getColorStateList(R.color.chip_text_state_list));
            }

            chipGroupModal.addView(chip);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            selectedTagIds.clear();
            for (int i = 0; i < chipGroupModal.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupModal.getChildAt(i);
                if (chip.isChecked()) {
                    selectedTagIds.add((String) chip.getTag());
                }
            }
            dialog.dismiss();
            refreshServiceTagsDisplay();
        });

        dialog.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            formattedExpiresAt = apiFormat.format(selected.getTime());
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            expiresAtInput.setText(displayFormat.format(selected.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        dialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

        dialog.show();
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
        tvServiceTagsError.setVisibility(View.GONE);

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
        try {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(parseFormat.parse(formattedExpiresAt));
            if (!selectedDate.after(today)) {
                expiresAtLayout.setError("Expiry date must be after today.");
                expiresAtInput.requestFocus();
                return;
            }
        } catch (Exception e) {
            expiresAtLayout.setError("Invalid expiry date.");
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

        if (selectedTagIds.isEmpty()) {
            tvServiceTagsError.setText("At least one service tag is required.");
            tvServiceTagsError.setVisibility(View.VISIBLE);
            chipGroupServiceTags.requestFocus();
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
        CreateJobRequest request = new CreateJobRequest(title, description, expiresAt, duration, location, salary, new ArrayList<>(selectedTagIds));
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
