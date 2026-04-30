package com.manilalinkup.app.utilities;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.ChatEmployerActivity;
import com.manilalinkup.app.activities.EmployerAddJobActivity;
import com.manilalinkup.app.activities.EmployerDashboard;
import com.manilalinkup.app.activities.EmployerNotificationsActivity;
import com.manilalinkup.app.activities.EmployerProfileActivity;

public class EmployerNavHelper {

    public static void setup(Activity ctx, BottomNavigationView nav, int currentItemId) {
        nav.setSelectedItemId(currentItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentItemId) return true;
            Class<?> target = null;
            if (id == R.id.nav_home) target = EmployerDashboard.class;
            else if (id == R.id.nav_profile) target = EmployerProfileActivity.class;
            else if (id == R.id.nav_notifications) target = EmployerNotificationsActivity.class;
            else if (id == R.id.nav_add_job) target = EmployerAddJobActivity.class;
            else if (id == R.id.nav_chat) target = ChatEmployerActivity.class;
            if (target != null) {
                ctx.startActivity(new Intent(ctx, target));
                ctx.overridePendingTransition(0, 0);
            }
            return true;
        });
    }
}
