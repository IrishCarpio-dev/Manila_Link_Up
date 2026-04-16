package com.manilalinkup.app;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public final class JobPostRepository {

    private JobPostRepository() {
    }

    public static List<JobPostDashboardModel> getSeekerDashboardItems() {
        List<JobPostDashboardModel> items = new ArrayList<>();
        items.add(new JobPostDashboardModel(
                "Events/Catering Helper",
                "Eng Bee Tin",
                "Binondo, Manila",
                "March 30, 2026",
                R.drawable.chipsstarters,
                "3 days ago"
        ));
        items.add(new JobPostDashboardModel(
                "Cafe Barista",
                "Don Kopi",
                "Malate, Manila",
                "Full Time",
                R.drawable.mockdata_engbeeten,
                "7 days ago"
        ));
        items.add(new JobPostDashboardModel(
                "Store Assistant",
                "Quick Smart Express",
                "Quiapo, Manila",
                "M | W | F",
                R.drawable.sarisaristore,
                "10 days ago"
        ));
        items.add(new JobPostDashboardModel(
                "Artist Assistant",
                "BINI Mika's Company",
                "GMA, Manila",
                "T | Th | F",
                R.drawable.mikaemployer,
                "1 day ago"
        ));
        return items;
    }

    public static List<JobPostDashboardModel> getEmployerArchiveItems(Context context) {
        return new ArrayList<>(getSeekerDashboardItems());
    }
}
