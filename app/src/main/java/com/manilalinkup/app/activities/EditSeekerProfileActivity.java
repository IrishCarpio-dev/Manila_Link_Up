package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.SalaryRangeAdapter;
import com.manilalinkup.app.utilities.AddressAutocompleteHelper;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.ImageUploadSelection;
import com.manilalinkup.app.utilities.MultipartRequestBodyHelper;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSeekerProfileActivity extends AppCompatActivity {

    private RecyclerView rvSalaryRange;
    private SalaryRangeAdapter adapter;
    private RadioGroup rgSalaryType;
    private EditText etCustomSalary;
    private EditText locationInput;
    private ImageView profileImage;
    private ImageView clearanceImage;
    private ImageView validIdImage;
    private ImageButton btnRemove;
    private Uri profilePhotoUri;
    private Uri clearanceUri;
    private Uri validIdUri;
    private ApiService apiService;
    private MaterialButton saveBtn;
    private ProgressDialog progressDialog;
    private ImageUploadSelection selectedImageOption;
    private Button uploadClearanceButton;
    private Button replaceClearanceButton;
    private Button uploadIdButton;
    private Button replaceIdButton;
    private EditText etBirthDate;
    private final Calendar aCalendar = Calendar.getInstance();
    private String formattedDateForApi = "";
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seeker_profile);
        locationInput = findViewById(R.id.etLocation);
        etBirthDate = findViewById(R.id.etDOB);
        profileImage = findViewById(R.id.image_view_insert_photo);
        clearanceImage = findViewById(R.id.image_view_clearance_preview);
        validIdImage = findViewById(R.id.image_view_id_preview);
        saveBtn = findViewById(R.id.material_button_save);
        uploadClearanceButton = findViewById(R.id.btnUploadClearance);
        replaceClearanceButton = findViewById(R.id.btnReplaceClearance);
        uploadIdButton = findViewById(R.id.btnUploadID);
        replaceIdButton = findViewById(R.id.btnReplaceID);
        etBirthDate = findViewById(R.id.etDOB);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rvSalaryRange = findViewById(R.id.rvSalaryRange);
        rgSalaryType = findViewById(R.id.rgSalaryType);
        etCustomSalary = findViewById(R.id.etCustomSalary);
        saveBtn = findViewById(R.id.material_button_save);

        rvSalaryRange.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalaryRangeAdapter(getHourlyRanges());
        rvSalaryRange.setAdapter(adapter);

        rgSalaryType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbHour) {
                adapter.updateData(getHourlyRanges());
                etCustomSalary.setHint("(Type your preferred Salary per hour)");
            } else if (checkedId == R.id.rbDay) {
                adapter.updateData(getDailyRanges());
                etCustomSalary.setHint("(Type your preferred Salary per day)");
            } else if (checkedId == R.id.rbMonth) {
                adapter.updateData(getMonthRanges());
                etCustomSalary.setHint("(Type your preferred Salary per month)");
            }
        });

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Setting up profile...");
        progressDialog.setCancelable(false);

        // Use .attachAddressAutocomplete() for complete address autocomplete
        AddressAutocompleteHelper.attachDistrictAutocomplete(locationInput);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            aCalendar.set(Calendar.YEAR, year);
            aCalendar.set(Calendar.MONTH, month);
            aCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        etBirthDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(EditSeekerProfileActivity.this,
                    dateSetListener,
                    aCalendar.get(Calendar.YEAR),
                    aCalendar.get(Calendar.MONTH),
                    aCalendar.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });

        profileImage = findViewById(R.id.image_view_insert_photo);
        btnRemove = findViewById(R.id.btn_remove_photo);

        profileImage.setOnClickListener(v -> {
            selectedImageOption = ImageUploadSelection.PROFILE;
            selectPhoto();
        });

        uploadClearanceButton.setOnClickListener(v -> {
            selectedImageOption = ImageUploadSelection.CLEARANCE;
            selectPhoto();
        });

        replaceClearanceButton.setOnClickListener(v -> {
            selectedImageOption = ImageUploadSelection.CLEARANCE;
            selectPhoto();
        });

        uploadIdButton.setOnClickListener(v -> {
            selectedImageOption = ImageUploadSelection.ID;
            selectPhoto();
        });

        replaceIdButton.setOnClickListener(v -> {
            selectedImageOption = ImageUploadSelection.ID;
            selectPhoto();
        });

        btnRemove.setOnClickListener(v -> {
            profilePhotoUri = null;
            btnRemove.setVisibility(View.GONE);
            profileImage.setClickable(true);

            profileImage.setImageResource(R.drawable.insert_photo);
        });

        saveBtn.setOnClickListener(v -> {
            progressDialog.show();

            FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                if (tokenTask.isSuccessful()) {
                    String idToken = tokenTask.getResult().getToken();
                    setupProfile(idToken);
                }
            });
        });

    }

    private List<SalaryRangeAdapter.SalaryOption> getHourlyRanges() {
        List<SalaryRangeAdapter.SalaryOption> list = new ArrayList<>();
        list.add(new SalaryRangeAdapter.SalaryOption("Below 50/Hour", 49));
        list.add(new SalaryRangeAdapter.SalaryOption("50 - 100/Hour", 100));
        list.add(new SalaryRangeAdapter.SalaryOption("100 - 150/Hour", 150));
        list.add(new SalaryRangeAdapter.SalaryOption("150 - 200/Hour", 200));
        list.add(new SalaryRangeAdapter.SalaryOption("200 - 300/Hour", 300));
        list.add(new SalaryRangeAdapter.SalaryOption("300 - 500/Hour", 500));
        list.add(new SalaryRangeAdapter.SalaryOption("500+/Hour", 501));
        return list;
    }

    private List<SalaryRangeAdapter.SalaryOption> getDailyRanges() {
        List<SalaryRangeAdapter.SalaryOption> list = new ArrayList<>();
        list.add(new SalaryRangeAdapter.SalaryOption("Below 500/Day", 499));
        list.add(new SalaryRangeAdapter.SalaryOption("500-800/Day", 800));
        list.add(new SalaryRangeAdapter.SalaryOption("800-1200/Day", 1200));
        list.add(new SalaryRangeAdapter.SalaryOption("1200-2000/Day", 2000));
        list.add(new SalaryRangeAdapter.SalaryOption("2000-3000/Day", 3000));
        list.add(new SalaryRangeAdapter.SalaryOption("3000-5000/Day", 5000));
        list.add(new SalaryRangeAdapter.SalaryOption("5000+/Day", 5001));
        return list;
    }

    private List<SalaryRangeAdapter.SalaryOption> getMonthRanges() {
        List<SalaryRangeAdapter.SalaryOption> list = new ArrayList<>();
        list.add(new SalaryRangeAdapter.SalaryOption("Below 15,000/Month", 14999));
        list.add(new SalaryRangeAdapter.SalaryOption("15,000 - 25,000/Month", 25000));
        list.add(new SalaryRangeAdapter.SalaryOption("25,000 - 40,000/Month", 40000));
        list.add(new SalaryRangeAdapter.SalaryOption("40,000 - 60,000/Month", 60000));
        list.add(new SalaryRangeAdapter.SalaryOption("60,000 - 80,000/Month", 80000));
        list.add(new SalaryRangeAdapter.SalaryOption("80,000 - 100,000/Month", 100000));
        list.add(new SalaryRangeAdapter.SalaryOption("100,000+/Month", 100001));
        return list;
    }

    private void updateLabel() {
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        formattedDateForApi = apiDateFormat.format(aCalendar.getTime());

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        etBirthDate.setText(displayDateFormat.format(aCalendar.getTime()));
    }
    private void selectPhoto() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else galleryLauncher.launch("image/*");
                }).show();
    }

    ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    switch(selectedImageOption) {
                        case PROFILE:
                            selectProfilePhoto(uri);
                        case CLEARANCE:
                            selectValidationPhoto(uri);
                        case ID:
                            selectValidationPhoto(uri);
                        default:
                            return;
                    }
                };
            }
    );

    ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    switch(selectedImageOption) {
                        case PROFILE:
                            selectProfilePhoto(profilePhotoUri);
                        case CLEARANCE:
                            selectValidationPhoto(clearanceUri);
                        case ID:
                            selectValidationPhoto(validIdUri);
                        default:
                            return;
                    }
                }
            }
    );

    private void selectProfilePhoto(Uri uri) {
        this.profilePhotoUri = uri;

        Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(profileImage);

        btnRemove.setVisibility(View.VISIBLE);
        profileImage.setClickable(false);
    }

    private void selectValidationPhoto(Uri uri) {
        if (selectedImageOption == ImageUploadSelection.CLEARANCE) {
            this.clearanceUri = uri;

            Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(clearanceImage);

            clearanceImage.setVisibility(View.VISIBLE);
            uploadClearanceButton.setVisibility(View.GONE);
            replaceClearanceButton.setVisibility(View.VISIBLE);
        } else if (selectedImageOption == ImageUploadSelection.ID) {
            this.validIdUri = uri;

            Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(validIdImage);

            validIdImage.setVisibility(View.VISIBLE);
            uploadIdButton.setVisibility(View.GONE);
            replaceIdButton.setVisibility(View.VISIBLE);
        }
    }

    private void openCamera() {
        switch (selectedImageOption) {
            case PROFILE:
                File profileFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_profile.jpg");
                profilePhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", profileFile);
                cameraLauncher.launch(profilePhotoUri);
            case CLEARANCE:
                File clearanceFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_clearance.jpg");
                clearanceUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", clearanceFile);
                cameraLauncher.launch(clearanceUri);
            case ID:
                File idFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_valid_id.jpg");
                validIdUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", idFile);
                cameraLauncher.launch(validIdUri);
            default:
                return;
        }
    }

    private void setupProfile(String token) {
        if (formattedDateForApi.isBlank()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please fill Birth Date field.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (locationInput.getText().toString().isBlank()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please fill Location field.", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedTypeId = rgSalaryType.getCheckedRadioButtonId();
        String salaryTypeStr = "hourly";
        if (selectedTypeId == R.id.rbDay) salaryTypeStr = "daily";
        else if (selectedTypeId == R.id.rbMonth) salaryTypeStr = "monthly";

        int salaryValueInt = adapter.getSelectedValue();
        String customValue = etCustomSalary.getText().toString().trim();
        if (!customValue.isEmpty()) {
            try {
                salaryValueInt = Integer.parseInt(customValue);
            } catch (NumberFormatException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Please enter a valid number for salary", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (salaryValueInt == 0) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please select or enter a salary range", Toast.LENGTH_SHORT).show();
            return;
        }

        if (clearanceUri == null || validIdUri == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please upload all required documents", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part profilePhoto = MultipartRequestBodyHelper.prepareImagePart(this, profilePhotoUri, "profilePhoto");
        MultipartBody.Part clearance = MultipartRequestBodyHelper.prepareImagePart(this, clearanceUri, "clearance");
        MultipartBody.Part validId = MultipartRequestBodyHelper.prepareImagePart(this, validIdUri, "validId");

        RequestBody birthDate = MultipartRequestBodyHelper.createPartFromString(formattedDateForApi);
        RequestBody location = MultipartRequestBodyHelper.createPartFromString(locationInput.getText().toString());
        RequestBody salaryValue = MultipartRequestBodyHelper.createPartFromString(String.valueOf(salaryValueInt));
        RequestBody salaryType = MultipartRequestBodyHelper.createPartFromString(salaryTypeStr);

        sendProfileToApi(token, profilePhoto, clearance, validId, birthDate, location, salaryValue, salaryType);
    }

    private void sendProfileToApi(
            String token,
            MultipartBody.Part profilePhoto,
            MultipartBody.Part clearance,
            MultipartBody.Part validId,
            RequestBody birthDate,
            RequestBody location,
            RequestBody salaryValue,
            RequestBody salaryType
    ) {
        apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.setupSeekerProfile(
                profilePhoto, clearance, validId,
                birthDate, location, salaryValue, salaryType
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(EditSeekerProfileActivity.this, "Profile Setup Complete!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditSeekerProfileActivity.this, SeekerDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    ErrorUtils.showErrorMessage(EditSeekerProfileActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showThrowableError(EditSeekerProfileActivity.this, t);
            }
        });
    }
}