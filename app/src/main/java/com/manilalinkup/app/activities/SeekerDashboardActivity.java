package com.manilalinkup.app.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.core.content.ContextCompat;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.GetSeekerJobsRequest;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.models.SeekerJobsResponse;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.models.UnreadCountResponse;
import com.manilalinkup.app.utilities.AddressAutocompleteHelper;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.ProfilePhotoCache;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SeekerNavHelper;
import com.manilalinkup.app.utilities.SessionCache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeekerDashboardActivity extends BaseActivity {

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private ImageButton btnSortFilter;
    private ActivityResultLauncher<Intent> jobPostLauncher;

    private boolean isLoading = false;
    private boolean isRefreshing = false;
    private boolean hasMorePages = true;
    private boolean isCuratedExhausted = false;
    private String lastExpiresAt = null;
    private String lastCreatedAt = null;
    private Double lastSalary = null;
    private Integer lastOffset = null;

    private String currentSortBy = null;
    private String currentOrder = null;
    private String filterLocation = null;
    private List<String> filterTags = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_dashboard);

        jobPostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String appliedJobId = result.getData().getStringExtra("JOB_ID");
                    if (appliedJobId != null) {
                        for (int i = 0; i < jobListJobCard.size(); i++) {
                            if (appliedJobId.equals(jobListJobCard.get(i).getJobId())) {
                                jobListJobCard.remove(i);
                                adapterJobPost.notifyItemRemoved(i);
                                break;
                            }
                        }
                    }
                }
            }
        );

        recyclerViewJobPost = findViewById(R.id.recycler_view_job_posts_dashboard);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        progressBarLoadMore = findViewById(R.id.progress_bar_load_more);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshJobs);

        jobListJobCard = new ArrayList<>();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, job -> {
            Intent intent = new Intent(SeekerDashboardActivity.this, SeekerJobPostActivity.class);
            intent.putExtra("JOB_ID", job.getJobId());
            intent.putExtra("JOB_TITLE", job.getJobTitle());
            intent.putExtra("EMPLOYER_NAME", job.getEmployerName());
            intent.putExtra("LOCATION", job.getJobPostLocation());
            intent.putExtra("DURATION", job.getJob_duration());
            intent.putExtra("SALARY", job.getSalary() != null ? job.getSalary() : 0.0);
            intent.putExtra("DESCRIPTION", job.getDescription());
            intent.putExtra("EXPIRES_AT", job.getExpiresAt());
            intent.putExtra("HOW_LONG_POSTED", job.getHowLongJobIsPosted());
            intent.putExtra("EMPLOYER_PHOTO", job.getEmployerProfilePicture());
            intent.putStringArrayListExtra("TAG_IDS", new ArrayList<>(job.getTagIds() != null ? job.getTagIds() : new ArrayList<>()));
            jobPostLauncher.launch(intent);
        });
        recyclerViewJobPost.setAdapter(adapterJobPost);

        recyclerViewJobPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                int lastVisible = lm.findLastVisibleItemPosition();
                int total = lm.getItemCount();
                if (!isLoading && hasMorePages && lastVisible >= total - 3) {
                    loadJobs();
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation_view_seeker);
        SeekerNavHelper.setup(this, bottomNavigationView, R.id.nav_home_seeker);

        btnSortFilter = findViewById(R.id.btn_sort_filter);
        btnSortFilter.setOnClickListener(v -> showFilterDialog());

        loadServiceTags();
        loadJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUnreadCount();
    }

    private void loadUnreadCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService apiService = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            apiService.getNotificationUnreadCount().enqueue(new Callback<UnreadCountResponse>() {
                @Override
                public void onResponse(Call<UnreadCountResponse> call, Response<UnreadCountResponse> response) {
                    if (!isFinishing() && response.isSuccessful() && response.body() != null) {
                        int count = response.body().getCount();
                        if (count > 0) {
                            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_notifications_seeker);
                            badge.setNumber(count);
                        } else {
                            bottomNavigationView.removeBadge(R.id.nav_notifications_seeker);
                        }
                    }
                }
                @Override public void onFailure(Call<UnreadCountResponse> call, Throwable t) {}
            });
        });
    }

    private void refreshJobs() {
        isRefreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        jobListJobCard.clear();
        adapterJobPost.notifyDataSetChanged();
        hasMorePages = true;
        isLoading = false;
        lastExpiresAt = null;
        lastCreatedAt = null;
        lastSalary = null;
        lastOffset = null;
        isCuratedExhausted = false;
        loadJobs();
    }

    private void loadJobs() {
        if (isLoading || !hasMorePages) return;
        isLoading = true;
        if (!isRefreshing) progressBarLoadMore.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            isLoading = false;
            progressBarLoadMore.setVisibility(View.GONE);
            if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
            return;
        }

        boolean hasCustomQuery = currentSortBy != null || filterTags != null || filterLocation != null;

        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                isLoading = false;
                progressBarLoadMore.setVisibility(View.GONE);
                if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                return;
            }

            String token = tokenTask.getResult().getToken();
            ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);
            GetSeekerJobsRequest request = buildRequest(hasCustomQuery);
            apiService.getSeekerJobs(request).enqueue(new Callback<SeekerJobsResponse>() {
                @Override
                public void onResponse(Call<SeekerJobsResponse> call, Response<SeekerJobsResponse> response) {
                    isLoading = false;
                    if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                    progressBarLoadMore.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        SeekerJobsResponse body = response.body();
                        List<JobModel> jobs = body.getData();
                        if (jobs != null && !jobs.isEmpty()) {
                            int insertStart = jobListJobCard.size();
                            for (JobModel job : jobs) jobListJobCard.add(mapToDisplayModel(job));
                            adapterJobPost.notifyItemRangeInserted(insertStart, jobs.size());
                        }
                        if (body.isHasMore() && body.getNextCursor() != null) {
                            updateCursors(body.getNextCursor(), hasCustomQuery);
                        } else if (!hasCustomQuery && !isCuratedExhausted) {
                            isCuratedExhausted = true;
                            lastExpiresAt = null;
                            lastCreatedAt = null;
                            hasMorePages = true;
                            loadJobs();
                        } else {
                            hasMorePages = false;
                        }
                    } else {
                        ErrorUtils.showErrorMessage(SeekerDashboardActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<SeekerJobsResponse> call, Throwable t) {
                    isLoading = false;
                    if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                    progressBarLoadMore.setVisibility(View.GONE);
                    ErrorUtils.showThrowableError(SeekerDashboardActivity.this, t);
                }
            });
        });
    }

    private GetSeekerJobsRequest buildRequest(boolean hasCustomQuery) {
        if (!hasCustomQuery) {
            String mode = isCuratedExhausted ? "all" : "curated";
            return new GetSeekerJobsRequest(mode, null, lastExpiresAt, lastCreatedAt);
        }

        GetSeekerJobsRequest request = new GetSeekerJobsRequest(null, null, null, null);

        if (currentSortBy != null) {
            request.setSortBy(currentSortBy);
            if (currentOrder != null) request.setOrder(currentOrder);
        }

        List<String> filterByList = new ArrayList<>();
        if (filterTags != null && !filterTags.isEmpty()) {
            filterByList.add("tags");
            request.setTags(new ArrayList<>(filterTags));
        }
        if (filterLocation != null) {
            filterByList.add("location");
            request.setLocation(filterLocation);
        }
        if (!filterByList.isEmpty()) {
            request.setFilterBy(filterByList);
        }

        if ("salary".equals(currentSortBy)) {
            request.setStartAfterSalary(lastSalary);
            request.setStartAfterCreatedAt(lastCreatedAt);
        } else if ("duration".equals(currentSortBy)) {
            request.setStartAfterOffset(lastOffset);
        } else {
            request.setStartAfterCreatedAt(lastCreatedAt);
        }

        return request;
    }

    private void updateCursors(SeekerJobsResponse.NextCursor cursor, boolean hasCustomQuery) {
        if (!hasCustomQuery) {
            lastExpiresAt = cursor.getExpiresAt();
            lastCreatedAt = cursor.getCreatedAt();
        } else if ("salary".equals(currentSortBy)) {
            lastSalary = cursor.getSalary();
            lastCreatedAt = cursor.getCreatedAt();
        } else if ("duration".equals(currentSortBy)) {
            lastOffset = cursor.getOffset();
        } else {
            lastCreatedAt = cursor.getCreatedAt();
        }
    }

    private void showFilterDialog() {
        Map<String, String> tagLabels = SessionCache.getInstance().getServiceTagLabelsById();

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sort_filter, null);

        ChipGroup chipGroupSort = dialogView.findViewById(R.id.chip_group_sort_dialog);
        Chip chipSortDefault = dialogView.findViewById(R.id.chip_sort_dialog_default);
        Chip chipSortSalary = dialogView.findViewById(R.id.chip_sort_dialog_salary);
        Chip chipSortDuration = dialogView.findViewById(R.id.chip_sort_dialog_duration);

        ChipGroup chipGroupOrder = dialogView.findViewById(R.id.chip_group_order);
        Chip chipOrderAsc = dialogView.findViewById(R.id.chip_order_asc);
        Chip chipOrderDesc = dialogView.findViewById(R.id.chip_order_desc);

        EditText etLocation = dialogView.findViewById(R.id.et_filter_location);
        ImageButton btnClearLocation = dialogView.findViewById(R.id.btn_clear_location);
        ChipGroup chipGroupTags = dialogView.findViewById(R.id.chip_group_filter_tags);
        Button btnClear = dialogView.findViewById(R.id.btn_filter_clear);
        Button btnApply = dialogView.findViewById(R.id.btn_filter_apply);

        if ("salary".equals(currentSortBy)) chipSortSalary.setChecked(true);
        else if ("duration".equals(currentSortBy)) chipSortDuration.setChecked(true);
        else chipSortDefault.setChecked(true);

        String initialOrder = currentOrder != null ? currentOrder
                : "duration".equals(currentSortBy) ? "asc" : "desc";
        if ("asc".equals(initialOrder)) chipOrderAsc.setChecked(true);
        else chipOrderDesc.setChecked(true);

        chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            boolean isDuration = checkedIds.get(0) == R.id.chip_sort_dialog_duration;
            if (isDuration) chipOrderAsc.setChecked(true);
            else chipOrderDesc.setChecked(true);
        });

        if (filterLocation != null) {
            etLocation.setText(filterLocation);
            btnClearLocation.setVisibility(View.VISIBLE);
        }

        AddressAutocompleteHelper.attachDistrictAutocomplete(etLocation);
        etLocation.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                btnClearLocation.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        btnClearLocation.setOnClickListener(v -> etLocation.setText(""));

        if (tagLabels != null) {
            LinkedHashSet<String> activeTagIds = filterTags != null
                    ? new LinkedHashSet<>(filterTags)
                    : new LinkedHashSet<>();
            for (Map.Entry<String, String> entry : tagLabels.entrySet()) {
                Chip chip = new Chip(this);
                chip.setText(entry.getValue());
                chip.setCheckable(true);
                chip.setChecked(activeTagIds.contains(entry.getKey()));
                chip.setTag(entry.getKey());
                chip.setEnsureMinTouchTargetSize(false);
                chipGroupTags.addView(chip);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnClear.setOnClickListener(v -> {
            chipSortDefault.setChecked(true);
            chipOrderDesc.setChecked(true);
            etLocation.setText("");
            for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                View child = chipGroupTags.getChildAt(i);
                if (child instanceof Chip) ((Chip) child).setChecked(false);
            }
        });

        btnApply.setOnClickListener(v -> {
            int sortCheckedId = chipGroupSort.getCheckedChipId();
            if (sortCheckedId == R.id.chip_sort_dialog_salary) currentSortBy = "salary";
            else if (sortCheckedId == R.id.chip_sort_dialog_duration) currentSortBy = "duration";
            else currentSortBy = "createdAt";

            int orderCheckedId = chipGroupOrder.getCheckedChipId();
            if (orderCheckedId == R.id.chip_order_asc) currentOrder = "asc";
            else currentOrder = "desc";

            String selectedLocation = etLocation.getText().toString().trim();
            filterLocation = selectedLocation.isEmpty() ? null : selectedLocation;

            List<String> selectedTags = new ArrayList<>();
            for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                View child = chipGroupTags.getChildAt(i);
                if (child instanceof Chip && ((Chip) child).isChecked()) {
                    selectedTags.add((String) child.getTag());
                }
            }
            filterTags = selectedTags.isEmpty() ? null : selectedTags;

            dialog.dismiss();
            updateSortFilterButton();
            refreshJobs();
        });

        dialog.show();
    }

    private void updateSortFilterButton() {
        boolean nonDefaultSort = "salary".equals(currentSortBy) || "duration".equals(currentSortBy)
                || "asc".equals(currentOrder);
        boolean hasActive = nonDefaultSort || filterLocation != null
                || (filterTags != null && !filterTags.isEmpty());
        int color = ContextCompat.getColor(this, hasActive ? R.color.manila_blue : R.color.dark_text);
        btnSortFilter.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void loadServiceTags() {
        SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
            @Override
            public void onAvailable(List<ServiceTagModel> tags) {
                adapterJobPost.setTagLabelsById(SessionCache.getInstance().getServiceTagLabelsById());
            }

            @Override
            public void onError() {
            }
        });
    }

    private JobPostDashboardModel mapToDisplayModel(JobModel job) {
        String employerName = job.getEmployer() != null ? job.getEmployer().getFullName() : "";
        String employerUid = job.getEmployer() != null ? job.getEmployer().getUid() : null;
        String photoUrl = job.getEmployer() != null ? job.getEmployer().getProfilePhoto() : null;
        if (employerUid != null && photoUrl != null) {
            ProfilePhotoCache.getInstance().put(employerUid, photoUrl);
        }
        JobPostDashboardModel model = new JobPostDashboardModel(
            job.getTitle(),
            employerName,
            job.getLocation(),
            job.getDuration(),
            photoUrl,
            getRelativeTime(job.getCreatedAt())
        );
        model.setJobId(job.getId());
        model.setEmployerUid(employerUid);
        model.setTagIds(job.getTags());
        model.setSalary(job.getSalary());
        model.setDescription(job.getDescription());
        model.setExpiresAt(job.getExpiresAt());
        model.setHasApplied(job.isHasApplied());
        model.setApplicationStatus(job.getApplicationStatus());
        model.setChatId(job.getChatId());
        return model;
    }

    private String getRelativeTime(String createdAt) {
        if (createdAt == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            Date date = sdf.parse(createdAt);
            long diffMs = System.currentTimeMillis() - date.getTime();
            long minutes = diffMs / 60000;
            if (minutes < 60) return minutes <= 1 ? "just now" : minutes + " minutes ago";
            long hours = minutes / 60;
            if (hours < 24) return hours == 1 ? "1 hour ago" : hours + " hours ago";
            long days = hours / 24;
            if (days < 30) return days == 1 ? "1 day ago" : days + " days ago";
            long months = days / 30;
            return months == 1 ? "1 month ago" : months + " months ago";
        } catch (Exception e) {
            return "";
        }
    }
}
