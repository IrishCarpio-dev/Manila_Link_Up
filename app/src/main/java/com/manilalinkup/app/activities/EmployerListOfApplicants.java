package com.manilalinkup.app.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.manilalinkup.app.adapters.EmployerApplicantsAdapter;
import com.manilalinkup.app.models.EmployerApplicantsModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class EmployerListOfApplicants extends AppCompatActivity {

    MaterialToolbar toolbar;
    RecyclerView applicantsRecyclerView;
    EmployerApplicantsAdapter applicantsAdapter;
    List<EmployerApplicantsModel> applicantsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_list_of_applicants);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        applicantsRecyclerView = findViewById(R.id.recycler_view_applicants);
        applicantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        applicantsModelList = new ArrayList<>();
        applicantsModelList.add(new EmployerApplicantsModel(R.drawable.frieren, "Frieren", "Chan", 1014, "Sampaloc, Manila"));
        applicantsModelList.add(new EmployerApplicantsModel(R.drawable.mikaemployer, "Mika", "Biniya", 22, "Quezon City"));
        applicantsModelList.add(new EmployerApplicantsModel(R.drawable.profpic_mock2, "Stark", "Luna", 18, "Binondo, Manila"));
        applicantsModelList.add(new EmployerApplicantsModel(R.drawable.seeker_prof_mock1, "Fern", "Rizal", 26, "Calamba, Laguna"));
        applicantsModelList.add(new EmployerApplicantsModel(R.drawable.profpicmock3, "Himmel", "Aquino", 96, "Tandang Sora, QC"));
        applicantsModelList.add(new EmployerApplicantsModel(R.drawable.profile_logo, "Gabriela", "Silang", 25, "Vigan, Ilocos Sur"));

        applicantsAdapter = new EmployerApplicantsAdapter(applicantsModelList);
        applicantsRecyclerView.setAdapter(applicantsAdapter);
    }
}