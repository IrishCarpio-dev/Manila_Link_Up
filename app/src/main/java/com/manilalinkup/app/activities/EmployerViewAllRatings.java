package com.manilalinkup.app.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.EmployerAllRatingsAdapter;
import com.manilalinkup.app.models.GetRatingsRequest;
import com.manilalinkup.app.models.RatingModel;
import com.manilalinkup.app.models.RatingsResponse;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerViewAllRatings extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmployerAllRatingsAdapter adapter;
    private final List<RatingModel> ratingsList = new ArrayList<>();

    private String nextCursor = null;
    private boolean hasMore = false;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_all_ratings);

        setupToolbar(R.id.toolbar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            nextCursor = null;
            hasMore = false;
            isLoadingMore = false;
            loadRatings(null);
        });

        recyclerView = findViewById(R.id.recycler_ratings_views);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployerAllRatingsAdapter(ratingsList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm == null) return;
                int lastVisible = lm.findLastVisibleItemPosition();
                if (!isLoadingMore && hasMore && lastVisible >= lm.getItemCount() - 3) {
                    loadRatings(nextCursor);
                }
            }
        });

        loadRatings(null);
    }

    private void loadRatings(String cursor) {
        if (isLoadingMore) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        isLoadingMore = true;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getRatings(new GetRatingsRequest(user.getUid(), null, cursor))
                    .enqueue(new Callback<RatingsResponse>() {
                        @Override
                        public void onResponse(Call<RatingsResponse> call,
                                               Response<RatingsResponse> response) {
                            isLoadingMore = false;
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                List<RatingModel> newRatings = response.body().getData();
                                if (cursor == null) {
                                    ratingsList.clear();
                                    ratingsList.addAll(newRatings);
                                    adapter.notifyDataSetChanged();
                                } else if (!newRatings.isEmpty()) {
                                    int start = ratingsList.size();
                                    ratingsList.addAll(newRatings);
                                    adapter.notifyItemRangeInserted(start, newRatings.size());
                                }
                                hasMore = response.body().isHasMore();
                                nextCursor = response.body().getNextCursor();
                            } else if (!response.isSuccessful()) {
                                ErrorUtils.showErrorMessage(EmployerViewAllRatings.this, response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<RatingsResponse> call, Throwable t) {
                            isLoadingMore = false;
                            swipeRefreshLayout.setRefreshing(false);
                            ErrorUtils.showThrowableError(EmployerViewAllRatings.this, t);
                        }
                    });
        });
    }
}
