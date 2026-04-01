package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class EditSeekerProfileActivity extends AppCompatActivity {

    private RecyclerView rvSalaryRange;
    private SalaryRangeAdapter adapter;
    private RadioGroup rgSalaryType;
    private EditText etCustomSalary;
    private Button btnSave;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seeker_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 1. Initialize Views
        rvSalaryRange = findViewById(R.id.rvSalaryRange);
        rgSalaryType = findViewById(R.id.rgSalaryType);
        etCustomSalary = findViewById(R.id.etCustomSalary);
        btnSave = findViewById(R.id.material_button_save);

        // 2. Set up RecyclerView
        rvSalaryRange.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalaryRangeAdapter(getHourlyRanges());
        rvSalaryRange.setAdapter(adapter);

        // 3. Handle RadioGroup Logic
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

        // 4. Set Click Listener
        btnSave.setOnClickListener(v -> saveToFirebase());

    }

    private void saveToFirebase() {
        // Determine the type (String)
        int selectedTypeId = rgSalaryType.getCheckedRadioButtonId();
        String salaryType = "hourly";
        if (selectedTypeId == R.id.rbDay) salaryType = "daily";
        else if (selectedTypeId == R.id.rbMonth) salaryType = "monthly";

        // Get the integer value from the RecyclerView selection
        int salaryValue = adapter.getSelectedValue();

        // Check if user typed something custom
        String customValue = etCustomSalary.getText().toString().trim();
        if (!customValue.isEmpty()) {
            try {
                salaryValue = Integer.parseInt(customValue);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validation
        if (salaryValue == 0) {
            Toast.makeText(this, "Please select or enter a salary range", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();

        // 2. Navigation Logic
        // Intent(Current Screen Context, Destination Class)
        Intent intent = new Intent(EditSeekerProfileActivity.this, SeekerDashboardActivity.class);

        // 3. Optional: Clear the Activity Task
        // This prevents the user from going back to the Edit Profile screen when they press the back button from the dashboard
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // 4. Close this activity
        finish();

        // TODO: Insert Firebase DatabaseReference code here
    }

    // --- Helper Methods for Data ---
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
}