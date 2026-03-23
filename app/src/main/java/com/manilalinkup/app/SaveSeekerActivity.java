package com.manilalinkup.app;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.*;

// 1. PINAKASIMPLENG STORAGE & MODEL
class JobPost {
    String title, company, date;
    JobPost(String t, String c, String d) { title = t; company = c; date = d; }
}

class JobData {
    public static List<JobPost> savedJobsList = new ArrayList<>();
}

// 2. MAIN ACTIVITY
public class SaveSeekerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_job_post); // Gamit ang binigay mong XML name

        // A. BACK BUTTON LOGIC
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // B. BOTTOM NAVIGATION HIGHLIGHT
        BottomNavigationView nav = findViewById(R.id.bottom_navigation_view);
        if (nav != null) nav.setSelectedItemId(R.id.nav_my_activity);

        // C. RECYCLERVIEW SETUP
        RecyclerView rv = findViewById(R.id.rv_saved_jobs);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new MyAdapter(JobData.savedJobsList));
        }
    }

    // 3. ADAPTER (Sa loob para gumana agad)
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        List<JobPost> list;
        MyAdapter(List<JobPost> l) { list = l; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_saved_job, p, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            JobPost j = list.get(pos);
            h.t.setText(j.title);
            h.c.setText(j.company);
            h.d.setText("Save on : " + j.date);
            h.x.setOnClickListener(v -> {
                list.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(SaveSeekerActivity.this, "Removed", Toast.LENGTH_SHORT).show();
            });
        }

        @Override public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView t, c, d; ImageView x;
            VH(View i) {
                super(i);
                t = i.findViewById(R.id.tv_title);
                c = i.findViewById(R.id.tv_company);
                d = i.findViewById(R.id.tv_save_date);
                x = i.findViewById(R.id.btn_remove);
            }
        }
    }
}