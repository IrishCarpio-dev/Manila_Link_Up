package com.manilalinkup.app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JobInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_information);

        // 1. Initialize Text Content
        setupJobContent();

        // 3. Button Listeners
        findViewById(R.id.button_apply).setOnClickListener(v -> {
            // Your apply logic here
        });

        findViewById(R.id.image_report).setOnClickListener(v -> {
            // Logic for the warning triangle icon
        });
    }

    private void setupJobContent() {
        // Use a single helper to set bolding for consistency
        setHtmlText(R.id.text_header_responsibilities, "<b>Key Responsibilities</b>");
        setHtmlText(R.id.text_header_overview, "<b>Job Overview</b>");
        setHtmlText(R.id.text_header_requirements, "<b>Requirements</b>");
        setHtmlText(R.id.text_resp_1, "<b>Event Setup:</b> Arrange tables, chairs, and catering equipment according to the floor plan.");
        setHtmlText(R.id.text_resp_2, "<b>Guest Service:</b> Assist in serving food and beverages professionally during the event.");
        setHtmlText(R.id.text_resp_3, "<b>Maintenance:</b> Ensure the dining area remains clean and organized throughout the duration of the shift.");

        setHtmlText(R.id.text_overview_1, "<b>Shift Schedule:</b> 8:00 AM - 5:00 PM (1-Day Engagement)");
        setHtmlText(R.id.text_overview_2, "<b>Compensation:</b> (Paid After shift)");

        setHtmlText(R.id.text_req_1, "<b>Dress Code:</b> Plain white shirt, black slacks/trousers, and closed black shoes.");
        setHtmlText(R.id.text_req_2, "<b>Documentation:</b> Please bring one (1) valid ID for building entry.");
    }

    private void setHtmlText(int viewId, String html) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        }
    }
}