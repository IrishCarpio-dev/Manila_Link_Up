package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.google.android.material.button.MaterialButton;
import com.manilalinkup.app.R;

public class AllSetActivity extends BaseActivity {
    MaterialButton getStarted;
    ImageView imgLogo;
    TextView txtTitle, txtSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_set);
        getStarted = findViewById(R.id.material_button_get_started);
        imgLogo = findViewById(R.id.imgLogo);
        txtTitle = findViewById(R.id.txtTitle);
        txtSubtitle = findViewById(R.id.txtSubtitle);

        imgLogo.setAlpha(0f);
        imgLogo.setScaleX(0.5f);
        imgLogo.setScaleY(0.5f);

        txtTitle.setAlpha(0f);
        txtSubtitle.setAlpha(0f);

        getStarted.setAlpha(0f);
        getStarted.setTranslationY(100f);
        
        animateVibe();

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllSetActivity.this, SeekerDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void animateVibe() {
        imgLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();

        txtTitle.animate().alpha(1f).setDuration(1000).setStartDelay(400).start();
        txtSubtitle.animate().alpha(1f).setDuration(1000).setStartDelay(600).start();

        getStarted.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(1000)
                .start();
    }
}