package com.manilalinkup.app.utilities;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.AppliedSeekerActivity;
import com.manilalinkup.app.activities.ChatSeekerActivity;
import com.manilalinkup.app.activities.SeekerDashboardActivity;
import com.manilalinkup.app.activities.SeekerNotificationsActivity;
import com.manilalinkup.app.activities.SeekerProfileActivity;

public class SeekerNavHelper {

    public static void setup(Activity ctx, BottomNavigationView nav, int currentItemId) {
        nav.setSelectedItemId(currentItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentItemId) return true;
            Class<?> target = null;
            if (id == R.id.nav_home_seeker) target = SeekerDashboardActivity.class;
            else if (id == R.id.nav_profile_seeker) target = SeekerProfileActivity.class;
            else if (id == R.id.nav_notifications_seeker) target = SeekerNotificationsActivity.class;
            else if (id == R.id.nav_activity_seeker) target = AppliedSeekerActivity.class;
            else if (id == R.id.nav_chat_seeker) target = ChatSeekerActivity.class;
            if (target != null) {
                ctx.startActivity(new Intent(ctx, target));
                ctx.overridePendingTransition(0, 0);
            }
            return true;
        });
    }
}
