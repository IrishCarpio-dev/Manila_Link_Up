package com.manilalinkup.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ProfessionalIdentity extends AppCompatActivity {

    ImageView profileImage;
    MaterialButton uploadClearancebtn, uploadIDbtn;

    AutoCompleteTextView locationDropdown;
    TextInputEditText dobEditText;

    RadioGroup salaryTypeGroup, salaryRangeGroup;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_identity);

        profileImage = findViewById(R.id.profileImage);
        uploadClearancebtn = findViewById(R.id.uploadClearancebtn);
        uploadIDbtn = findViewById(R.id.uploadIDbtn);

        locationDropdown = findViewById(R.id.locationDropdown);
        dobEditText = findViewById(R.id.dobEditText);

        salaryTypeGroup = findViewById(R.id.salaryTypeGroup);
        salaryRangeGroup = findViewById(R.id.salaryRangeGroup);

        setupImagePicker();
        setupFilePicker();
        setupLocationDropdown();
        setupSalaryTypeRadio();
        setupDobPicker();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData()!=null){
                        Uri imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri);
                    }
                });

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void setupFilePicker(){
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData()!=null){
                        Uri fileUri = result.getData().getData();
                    }
                });

        uploadClearancebtn.setOnClickListener(v -> openFilePicker());
        uploadIDbtn.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*","application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        filePickerLauncher.launch(intent);
    }

    private void setupLocationDropdown(){
        String[] locations={
                "Quiapo","Binondo","Tondo","Sampaloc","Santa Cruz",
                "San Miguel","San Andres","Malate","Ermita","Paco"
        };

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,locations);

        locationDropdown.setAdapter(adapter);
    }

    private void setupSalaryTypeRadio(){
        salaryTypeGroup.setOnCheckedChangeListener((group,checkedId)->{

            if(checkedId==R.id.radioHour){
                updateSalaryRange("Per Hour");
            }
            else if(checkedId==R.id.radioDay){
                updateSalaryRange("Per Day");
            }
            else if(checkedId==R.id.radioWeek){
                updateSalaryRange("Per Week");
            }
            else if(checkedId==R.id.radioMonth){
                updateSalaryRange("Per Month");
            }
            else if(checkedId==R.id.radioProject){
                updateSalaryRange("Per Project");
            }

        });
    }

    private void updateSalaryRange(String type){

        salaryRangeGroup.removeAllViews();

        String[] ranges;

        switch(type){

            case "Per Hour":
                ranges=new String[]{
                        "Below ₱50 / Hour",
                        "₱50 - ₱100 / Hour",
                        "₱100 - ₱150 / Hour",
                        "₱150+ / Hour"};
                break;

            case "Per Day":
                ranges=new String[]{
                        "Below ₱500 / Day",
                        "₱500 - ₱800 / Day",
                        "₱800 - ₱1200 / Day",
                        "₱1200+ / Day"};
                break;

            case "Per Week":
                ranges=new String[]{
                        "Below ₱3,000 / Week",
                        "₱3,000 - ₱5,000 / Week",
                        "₱5,000 - ₱8,000 / Week",
                        "₱8,000+ / Week"};
                break;

            case "Per Month":
                ranges=new String[]{
                        "Below ₱10,000 / Month",
                        "₱10,000 - ₱15,000 / Month",
                        "₱15,000 - ₱20,000 / Month",
                        "₱20,000 - ₱30,000 / Month",
                        "₱30,000+ / Month"};
                break;

            default:
                ranges=new String[]{
                        "Below ₱5,000",
                        "₱5,000 - ₱10,000",
                        "₱10,000 - ₱20,000"};
        }

        for(String range:ranges){
            RadioButton radio=new RadioButton(this);
            radio.setText(range);
            salaryRangeGroup.addView(radio);
        }
    }

    private void setupDobPicker(){

        dobEditText.setOnClickListener(v->{

            CalendarConstraints.Builder constraints=new CalendarConstraints.Builder();
            constraints.setEnd(MaterialDatePicker.todayInUtcMilliseconds());

            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.YEAR,-100);
            constraints.setStart(cal.getTimeInMillis());

            MaterialDatePicker<Long> picker=
                    MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select Date of Birth")
                            .setCalendarConstraints(constraints.build())
                            .build();

            picker.addOnPositiveButtonClickListener(selection->{

                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH)+1;
                int year=calendar.get(Calendar.YEAR);

                dobEditText.setText(day+"/"+month+"/"+year);
            });

            picker.show(getSupportFragmentManager(),"DOB_PICKER");
        });
    }
}