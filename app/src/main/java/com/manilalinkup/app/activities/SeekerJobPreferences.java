package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.SeekerPreferencesModel;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.utilities.AddressAutocompleteHelper;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.manilalinkup.app.R;

public class SeekerJobPreferences extends AppCompatActivity {
    private TextInputEditText minSalaryInput, durationAmountInput;
    private AutoCompleteTextView rateDropdown;
    private EditText preferredLocationInput;
    private ChipGroup chipGroupServiceTags;
    private TextView tvServiceTagsError, greetingNameText;
    private ExtendedFloatingActionButton btnSave;
    private ProgressDialog progressDialog;

    private final List<ServiceTagModel> serviceTagList = new ArrayList<>();
    private final Set<String> selectedTagIds = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_job_preferences);

        minSalaryInput = findViewById(R.id.edit_text_MinSalary);
        durationAmountInput = findViewById(R.id.edit_text_duration_amount);
        rateDropdown = findViewById(R.id.auto_complete_rate);
        preferredLocationInput = findViewById(R.id.edit_text_location);
        chipGroupServiceTags = findViewById(R.id.chip_group_service_tags);
        tvServiceTagsError = findViewById(R.id.text_view_service_tags_error);
        greetingNameText = findViewById(R.id.textview_greeting_name_seeker);
        btnSave = findViewById(R.id.button_post_job);

        TextView tvSkip = findViewById(R.id.text_view_skip);
        tvSkip.setPaintFlags(tvSkip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeekerJobPreferences.this, SeekerDashboardActivity.class);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving preferences...");
        progressDialog.setCancelable(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            greetingNameText.setText(currentUser.getDisplayName());
        }

        String[] durationUnits = {"hour(s)", "day(s)", "week(s)", "month(s)", "year(s)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, durationUnits);
        rateDropdown.setAdapter(adapter);

        AddressAutocompleteHelper.attachDistrictAutocomplete(preferredLocationInput);

        loadServiceTags();

        btnSave.setOnClickListener(v -> savePreferences());
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
                    if (response.isSuccessful() && response.body() != null) {
                        serviceTagList.clear();
                        serviceTagList.addAll(response.body().getData());
                        refreshServiceTagsDisplay();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ServiceTagModel>>> call, Throwable t) {
                    Toast.makeText(SeekerJobPreferences.this, "Error loading tags", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void refreshServiceTagsDisplay() {
        chipGroupServiceTags.removeAllViews();

        for (String tagId : selectedTagIds) {
            ServiceTagModel tag = findTagById(tagId);
            if (tag == null) continue;

            Chip chip = new Chip(this);
            chip.setText(tag.getLabel());

            chip.setChipBackgroundColorResource(R.color.chip_background_state_list);
            chip.setTextColor(Color.WHITE);

            chip.setCheckable(true);
            chip.setChecked(true);
            chip.setCheckedIconVisible(false);

            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                selectedTagIds.remove(tagId);
                refreshServiceTagsDisplay();
            });
            chipGroupServiceTags.addView(chip);
            chip.setCloseIconTint(ColorStateList.valueOf(Color.WHITE));
        }

        // Add the "+" button to open modal
        Chip addChip = new Chip(this);
        addChip.setText("+ Add Service");
        addChip.setOnClickListener(v -> showServiceTagModal());
        chipGroupServiceTags.addView(addChip);
    }

    private void showServiceTagModal() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_service_tags, null);
        ChipGroup chipGroupModal = dialogView.findViewById(R.id.chipGroupServiceTagsModal);
        ImageButton btnClose = dialogView.findViewById(R.id.btnCloseServiceTags);
        Button btnSaveTags = dialogView.findViewById(R.id.btnSaveServiceTags);

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

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSaveTags.setOnClickListener(v -> {
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

    private ServiceTagModel findTagById(String id) {
        for (ServiceTagModel tag : serviceTagList) {
            if (tag.getId().equals(id)) return tag;
        }
        return null;
    }

    private void savePreferences() {
        String salaryStr = minSalaryInput.getText() != null ? minSalaryInput.getText().toString().trim() : "";
        String location = preferredLocationInput.getText() != null ? preferredLocationInput.getText().toString().trim() : "";
        String durationValue = durationAmountInput.getText() != null ? durationAmountInput.getText().toString().trim() : "";
        String durationUnit = rateDropdown.getText().toString().trim();

        tvServiceTagsError.setVisibility(View.GONE);

        if (selectedTagIds.isEmpty()) {
            tvServiceTagsError.setText("Please select at least one service.");
            tvServiceTagsError.setVisibility(View.VISIBLE);
            chipGroupServiceTags.requestFocus();
            return;
        }

        if (salaryStr.isEmpty() || location.isEmpty() || durationValue.isEmpty() || durationUnit.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid salary format", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            return;
        }
        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
            if(tokenTask.isSuccessful()){
                String token = tokenTask.getResult().getToken();
                String duration = durationValue + " " + durationUnit;
                submitSeekerPreference(token, location, duration, salary);
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitSeekerPreference(String token, String location, String duration, double salary) {
        SeekerPreferencesModel preference = new SeekerPreferencesModel(salary, duration, location, new ArrayList<>(selectedTagIds));

        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.updateSeekerPreferences(preference).enqueue(new Callback<ApiResponse<SeekerPreferencesModel>>() {
            @Override
            public void onResponse(Call<ApiResponse<SeekerPreferencesModel>> call, Response<ApiResponse<SeekerPreferencesModel>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(SeekerJobPreferences.this, "Preferences Saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SeekerJobPreferences.this, SeekerDashboardActivity.class));
                    finish();
                } else {
                    ErrorUtils.showErrorMessage(SeekerJobPreferences.this, response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<SeekerPreferencesModel>> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showThrowableError(SeekerJobPreferences.this, t);
            }
        });
    }
}