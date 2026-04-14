package com.manilalinkup.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.manilalinkup.app.R;

public class EditInformationActivity extends AppCompatActivity {

    private static final String PREFS_EDIT_INFORMATION = "edit_information_prefs";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";

    private static final String DEFAULT_FIRST_NAME = "Juan";
    private static final String DEFAULT_LAST_NAME = "Dela Cruz";
    private static final String DEFAULT_DATE_OF_BIRTH = "01/15/2002";

    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText dateOfBirthInput;
    private TextInputLayout firstNameLayout;
    private TextInputLayout lastNameLayout;
    private TextInputLayout dateOfBirthLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_information);

        sharedPreferences = getSharedPreferences(PREFS_EDIT_INFORMATION, MODE_PRIVATE);

        firstNameInput = findViewById(R.id.edit_text_first_name);
        lastNameInput = findViewById(R.id.edit_text_last_name);
        dateOfBirthInput = findViewById(R.id.edit_text_dob);

        firstNameLayout = findViewById(R.id.text_input_layout_first_name);
        lastNameLayout = findViewById(R.id.text_input_layout_last_name);
        dateOfBirthLayout = findViewById(R.id.text_input_layout_dob);

        seedProtectedFieldsIfEmpty();
        bindProtectedField(firstNameInput, firstNameLayout, sharedPreferences.getString(KEY_FIRST_NAME, DEFAULT_FIRST_NAME));
        bindProtectedField(lastNameInput, lastNameLayout, sharedPreferences.getString(KEY_LAST_NAME, DEFAULT_LAST_NAME));
        bindProtectedField(dateOfBirthInput, dateOfBirthLayout, sharedPreferences.getString(KEY_DATE_OF_BIRTH, DEFAULT_DATE_OF_BIRTH));

        MaterialButton btnSave = findViewById(R.id.material_button_save_info);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                Toast.makeText(this, "Information Saved Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void seedProtectedFieldsIfEmpty() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean hasChanges = false;

        if (TextUtils.isEmpty(sharedPreferences.getString(KEY_FIRST_NAME, ""))) {
            editor.putString(KEY_FIRST_NAME, DEFAULT_FIRST_NAME);
            hasChanges = true;
        }

        if (TextUtils.isEmpty(sharedPreferences.getString(KEY_LAST_NAME, ""))) {
            editor.putString(KEY_LAST_NAME, DEFAULT_LAST_NAME);
            hasChanges = true;
        }

        if (TextUtils.isEmpty(sharedPreferences.getString(KEY_DATE_OF_BIRTH, ""))) {
            editor.putString(KEY_DATE_OF_BIRTH, DEFAULT_DATE_OF_BIRTH);
            hasChanges = true;
        }

        if (hasChanges) {
            editor.apply();
        }
    }

    private void bindProtectedField(TextInputEditText inputEditText, TextInputLayout inputLayout, String value) {
        inputEditText.setText(value);
        inputEditText.setFocusable(false);
        inputEditText.setFocusableInTouchMode(false);
        inputEditText.setCursorVisible(false);
        inputEditText.setLongClickable(false);
        inputEditText.setTextIsSelectable(false);

        View.OnTouchListener touchListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showLockedFieldPopup();
            }
            return true;
        };

        inputEditText.setOnTouchListener(touchListener);
        inputEditText.setOnClickListener(v -> showLockedFieldPopup());

        if (inputLayout != null) {
            inputLayout.setOnTouchListener(touchListener);
            inputLayout.setOnClickListener(v -> showLockedFieldPopup());
        }
    }

    private void showLockedFieldPopup() {
        Toast.makeText(this, "This field is already filled and cannot be edited.", Toast.LENGTH_SHORT).show();
    }
}
