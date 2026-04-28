package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.EmployerAllRatingsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.GetRatingsRequest;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerViewAllRatings extends BaseActivity {

    private RecyclerView recyclerView;
    private EmployerAllRatingsAdapter adapter;
    private final List<RatingModel> ratingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_all_ratings);

        setupToolbar(R.id.toolbar);

        recyclerView = findViewById(R.id.recycler_ratings_views);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployerAllRatingsAdapter(ratingsList);
        recyclerView.setAdapter(adapter);

        loadRatings();
    }

    private void loadRatings() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getRatings(new GetRatingsRequest(user.getUid(), null, null))
                    .enqueue(new Callback<ApiResponse<List<RatingModel>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<RatingModel>>> call,
                                               Response<ApiResponse<List<RatingModel>>> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                ratingsList.clear();
                                ratingsList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<RatingModel>>> call, Throwable t) {
                            Toast.makeText(EmployerViewAllRatings.this,
                                    "Failed to load ratings", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
