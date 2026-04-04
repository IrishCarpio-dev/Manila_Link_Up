package com.manilalinkup.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditEmployerProfileActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_employer_profile);
        locationInput = findViewById(R.id.etLocation);
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
            DatePickerDialog dialog = new DatePickerDialog(EditEmployerProfileActivity.this,
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
            Toast.makeText(this, "Please fill Birth Date field.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (locationInput.getText().toString().isBlank()) {
            Toast.makeText(this, "Please fill Location field.", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part profilePhoto = null;
        MultipartBody.Part clearance = null;
        MultipartBody.Part validId = null;

        if (clearanceUri == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please upload Clearance image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validIdUri == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please upload valid ID image", Toast.LENGTH_SHORT).show();
            return;
        }

        profilePhoto = MultipartRequestBodyHelper.prepareImagePart(EditEmployerProfileActivity.this, profilePhotoUri, "profilePhoto");
        clearance = MultipartRequestBodyHelper.prepareImagePart(EditEmployerProfileActivity.this, clearanceUri, "clearance");
        validId = MultipartRequestBodyHelper.prepareImagePart(EditEmployerProfileActivity.this, validIdUri, "validId");

        if (clearance == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error processing Clearance image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validId == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error processing valid ID image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody birthDate = MultipartRequestBodyHelper.createPartFromString(etBirthDate.getText().toString());
        RequestBody location = MultipartRequestBodyHelper.createPartFromString(locationInput.getText().toString());

        sendProfileToApi(
                token,
                profilePhoto,
                clearance,
                validId,
                birthDate,
                location
        );
    }

    private void sendProfileToApi(
            String token,
            MultipartBody.Part profilePhoto,
            MultipartBody.Part clearance,
            MultipartBody.Part validId,
            RequestBody birthDate,
            RequestBody location
    ) {
        apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.setupEmployerProfile(
                profilePhoto,
                clearance,
                validId,
                birthDate,
                location
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    startActivity(new Intent(EditEmployerProfileActivity.this, EmployerDashboard.class));
                    finish();
                } else {
                    ErrorUtils.showErrorMessage(EditEmployerProfileActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showThrowableError(EditEmployerProfileActivity.this, t);
            }
        });
    }

}