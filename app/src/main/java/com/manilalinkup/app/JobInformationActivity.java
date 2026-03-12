package com.manilalinkup.app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class JobInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_information);

        // Toolbar back navigation
        MaterialToolbar toolbar = findViewById(R.id.toolbar_job_info);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Key Responsibilities
        setHtmlText(R.id.text_resp_1, "<b>Event Setup:</b> Arrange tables, chairs, and catering equipment according to the floor plan.");
        setHtmlText(R.id.text_resp_2, "<b>Guest Service:</b> Assist in serving food and beverages professionally during the event.");
        setHtmlText(R.id.text_resp_3, "<b>Maintenance:</b> Ensure the dining area remains clean and organized throughout the duration of the shift.");

        // Job Overview
        setHtmlText(R.id.text_overview_1, "<b>Shift Schedule:</b> 8:00 AM - 5:00 PM (1-Day Engagement)");
        setHtmlText(R.id.text_overview_2, "<b>Compensation:</b> (Paid After shift)");

        // Requirements
        setHtmlText(R.id.text_req_1, "<b>Dress Code:</b> Plain white shirt, black slacks/trousers, and closed black shoes.");
        setHtmlText(R.id.text_req_2, "<b>Documentation:</b> Please bring one (1) valid ID for building entry.");
        // Bold section headers
        setHtmlText(R.id.text_header_responsibilities, "<b>Key Responsibilities</b>");
        setHtmlText(R.id.text_header_overview, "<b>Job Overview</b>");
        setHtmlText(R.id.text_header_requirements, "<b>Requirements</b>");
    }

    private void setHtmlText(int viewId, String html) {
        TextView textView = findViewById(viewId);
        textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
    }
}