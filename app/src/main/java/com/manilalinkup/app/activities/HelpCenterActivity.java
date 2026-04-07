package com.manilalinkup.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.FAQAdapter;
import com.manilalinkup.app.models.FAQModel;

public class HelpCenterActivity extends AppCompatActivity {

    private Button btnContactSupport;
    private ImageView btnBack;
    private RecyclerView recyclerFaq;
    private FAQAdapter faqAdapter;
    private List<FAQModel> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        // 1. Initialize Views
        btnContactSupport = findViewById(R.id.btn_contact_support);
        recyclerFaq = findViewById(R.id.recycler_faq);

        // 2. Setup RecyclerView
        recyclerFaq.setLayoutManager(new LinearLayoutManager(this));

        faqList = new ArrayList<>();
        populateFaqData();

        faqAdapter = new FAQAdapter(faqList);
        recyclerFaq.setAdapter(faqAdapter);

        // 3. Click Listeners
        btnContactSupport.setOnClickListener(v -> composeEmail());

    }

    private void populateFaqData() {
        faqList.add(new FAQModel("How do I apply for a gig?",
                "Browse the 'Gigs' tab, click on a post that interests you, and tap 'Apply'. Make sure your profile is verified first!"));

        faqList.add(new FAQModel("Why was my ID verification rejected?",
                "Verification usually fails if the photo is blurry, the ID is expired, or the name doesn't match. Please try again with a clear photo."));

        faqList.add(new FAQModel("How do I get paid?",
                "Payments are settled directly between you and the employer. We recommend using GCash or Maya for safe transactions."));

        faqList.add(new FAQModel("Can I cancel an application?",
                "Yes, go to your 'My Applications' tab and select the gig you wish to withdraw from."));
    }

    private void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@manilalinkup.ph"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Manila LinkUp Seeker");

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}