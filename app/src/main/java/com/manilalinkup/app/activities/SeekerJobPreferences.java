package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

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
import com.manilalinkup.app.utilities.SessionCache;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.manilalinkup.app.R;

public class SeekerJobPreferences extends BaseActivity {
    private TextInputEditText minSalaryInput;
    private EditText preferredLocationInput;
    private ChipGroup chipGroupServiceTags;
    private TextView tvServiceTagsError, greetingNameText;
    private ExtendedFloatingActionButton btnSave;

    private final List<ServiceTagModel> serviceTagList = new ArrayList<>();
    private final Set<String> selectedTagIds = new LinkedHashSet<>();
    private boolean fromSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_job_preferences);

        minSalaryInput = findViewById(R.id.edit_text_MinSalary);
        preferredLocationInput = findViewById(R.id.edit_text_location);
        chipGroupServiceTags = findViewById(R.id.chip_group_service_tags);
        tvServiceTagsError = findViewById(R.id.text_view_service_tags_error);
        greetingNameText = findViewById(R.id.textview_greeting_name_seeker);
        btnSave = findViewById(R.id.button_post_job);

        fromSettings = getIntent().getBooleanExtra("FROM_SETTINGS", false);

        TextView tvGreeting = findViewById(R.id.textview_greeting_seeker);
        android.widget.LinearLayout layoutHeaderGreeting = findViewById(R.id.layout_header_greeting);
        android.widget.ImageButton btnBack = findViewById(R.id.btn_back_preferences);
        TextView tvSkip = findViewById(R.id.text_view_skip);
        if (fromSettings) {
            tvSkip.setVisibility(View.GONE);
            tvGreeting.setVisibility(View.GONE);
            greetingNameText.setText("Update Job Preferences");
            greetingNameText.setGravity(android.view.Gravity.CENTER);
            android.widget.FrameLayout.LayoutParams fp = new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    android.view.Gravity.CENTER_VERTICAL
            );
            layoutHeaderGreeting.setLayoutParams(fp);
            layoutHeaderGreeting.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            btnSave.setText("Save");
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> finish());
        } else {
            tvSkip.setPaintFlags(tvSkip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvSkip.setOnClickListener(v -> startActivity(new Intent(SeekerJobPreferences.this, AllSetActivity.class)));

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getDisplayName() != null) {
                greetingNameText.setText(currentUser.getDisplayName());
            }
        }

        AddressAutocompleteHelper.attachDistrictAutocomplete(preferredLocationInput);

        if (fromSettings) {
            loadExistingPreferences();
        } else {
            loadServiceTags();
        }

        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadServiceTags() {
        SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
            @Override
            public void onAvailable(List<ServiceTagModel> tags) {
                serviceTagList.clear();
                serviceTagList.addAll(tags);
                refreshServiceTagsDisplay();
            }

            @Override
            public void onError() {
                Toast.makeText(SeekerJobPreferences.this, "Error loading tags", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshServiceTagsDisplay() {
        chipGroupServiceTags.removeAllViews();

        for (String tagId : selectedTagIds) {
            ServiceTagModel tag = findTagById(tagId);
            if (tag == null) continue;

            Chip chip = new Chip(this);
            chip.setText(tag.getLabel());
            chip.setChipBackgroundColorResource(R.color.manila_blue);
            chip.setTextColor(Color.WHITE);
            chip.setCheckable(false);
            chip.setCloseIconVisible(true);
            chip.setCloseIconTint(ColorStateList.valueOf(Color.WHITE));

            final String currentTagId = tagId;
            chip.setOnCloseIconClickListener(v -> {
                selectedTagIds.remove(currentTagId);
                refreshServiceTagsDisplay();
            });

            chipGroupServiceTags.addView(chip);
        }

        Chip addChip = new Chip(this);
        addChip.setText("+ Add Service");
        addChip.setChipBackgroundColorResource(android.R.color.transparent);
        addChip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.manila_blue)));

        float strokeWidthPx = 2 * getResources().getDisplayMetrics().density;
        addChip.setChipStrokeWidth(strokeWidthPx);

        addChip.setTextColor(getResources().getColor(R.color.manila_blue));
        addChip.setCheckable(false);

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

        tvServiceTagsError.setVisibility(View.GONE);

        if (selectedTagIds.isEmpty()) {
            tvServiceTagsError.setText("Please select at least one service.");
            tvServiceTagsError.setVisibility(View.VISIBLE);
            chipGroupServiceTags.requestFocus();
            return;
        }

        if (salaryStr.isEmpty() || location.isEmpty()) {
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

        showProgress("Saving preferences...");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            hideProgress();
            return;
        }
        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if(tokenTask.isSuccessful()){
                String token = tokenTask.getResult().getToken();
                submitSeekerPreference(token, location, salary);
            } else {
                hideProgress();
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExistingPreferences() {
        showProgress("Loading preferences...");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            hideProgress();
            loadServiceTags();
            return;
        }
        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                hideProgress();
                loadServiceTags();
                return;
            }
            SessionCache.getInstance().refreshUserProfile(
                    tokenTask.getResult().getToken(),
                    new SessionCache.UserProfileCallback() {
                        @Override
                        public void onAvailable(com.manilalinkup.app.models.UserProfileModel profile) {
                            hideProgress();
                            if (profile.getSeekers() != null) {
                                SeekerPreferencesModel prefs = profile.getSeekers().getPreferences();
                                if (prefs != null) preFillPreferences(prefs);
                            }
                            loadServiceTags();
                        }
                        @Override
                        public void onError() {
                            hideProgress();
                            loadServiceTags();
                        }
                    }
            );
        });
    }

    private void preFillPreferences(SeekerPreferencesModel prefs) {
        if (prefs.getPreferredSalary() != null) {
            double salary = prefs.getPreferredSalary();
            String salaryStr = (salary == Math.floor(salary))
                    ? String.valueOf((int) salary)
                    : String.valueOf(salary);
            minSalaryInput.setText(salaryStr);
        }
        if (prefs.getPreferredLocation() != null) {
            preferredLocationInput.setText(prefs.getPreferredLocation());
        }
        if (prefs.getTags() != null) {
            selectedTagIds.addAll(prefs.getTags());
        }
    }

    private void submitSeekerPreference(String token, String location, double salary) {
        SeekerPreferencesModel preference = new SeekerPreferencesModel(salary, location, new ArrayList<>(selectedTagIds));

        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.updateSeekerPreferences(preference).enqueue(new Callback<ApiResponse<SeekerPreferencesModel>>() {
            @Override
            public void onResponse(Call<ApiResponse<SeekerPreferencesModel>> call, Response<ApiResponse<SeekerPreferencesModel>> response) {
                hideProgress();
                if (isDestroyed()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(SeekerJobPreferences.this, "Preferences Saved!", Toast.LENGTH_SHORT).show();
                    if (fromSettings) {
                        finish();
                    } else {
                        startActivity(new Intent(SeekerJobPreferences.this, AllSetActivity.class));
                        finish();
                    }
                } else {
                    ErrorUtils.showErrorMessage(SeekerJobPreferences.this, response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<SeekerPreferencesModel>> call, Throwable t) {
                hideProgress();
                ErrorUtils.showThrowableError(SeekerJobPreferences.this, t);
            }
        });
    }
}