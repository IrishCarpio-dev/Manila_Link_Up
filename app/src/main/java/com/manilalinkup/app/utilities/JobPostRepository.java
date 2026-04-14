package com.manilalinkup.app.utilities;

import android.content.Context;

import com.manilalinkup.app.R;
import com.manilalinkup.app.models.JobPostDashboardModel;

import java.util.ArrayList;

public final class JobPostRepository {

    private JobPostRepository() {
    }

    public static ArrayList<JobPostDashboardModel> getSeekerDashboardJobs(Context context) {
        return new ArrayList<>(getSharedArchiveJobs(context));
    }

    public static ArrayList<JobPostDashboardModel> getEmployerArchiveJobs(Context context) {
        return new ArrayList<>(getSharedArchiveJobs(context));
    }

    private static ArrayList<JobPostDashboardModel> getSharedArchiveJobs(Context context) {
        ArrayList<JobPostDashboardModel> jobs = new ArrayList<>();

        jobs.add(new JobPostDashboardModel(
                "Events/Catering Helper",
                "Eng Bee Tin",
                "Binondo, Manila",
                "March 30, 2026",
                drawableUri(context, R.drawable.mockdata_engbeeten),
                "3 days ago"
        ));

        jobs.add(new JobPostDashboardModel(
                "Cafe Barista",
                "Don Kopi",
                "Malate, Manila",
                "Full Time",
                drawableUri(context, R.drawable.chipsstarters),
                "7 days ago"
        ));

        jobs.add(new JobPostDashboardModel(
                "Store Assistant",
                "Quick Smart Express",
                "Quiapo, Manila",
                "M | W | F",
                drawableUri(context, R.drawable.sarisaristore),
                "10 days ago"
        ));

        jobs.add(new JobPostDashboardModel(
                "Artist Assistant",
                "BINI Mika's Company",
                "GMA, Manila",
                "T | Th | F",
                drawableUri(context, R.drawable.mikaemployer),
                "1 day ago"
        ));

        return jobs;
    }

    private static String drawableUri(Context context, int drawableResId) {
        String drawableName = context.getResources().getResourceEntryName(drawableResId);
        return "android.resource://" + context.getPackageName() + "/drawable/" + drawableName;
    }
}
