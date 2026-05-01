package com.manilalinkup.app.utilities;

import com.manilalinkup.app.R;
import com.manilalinkup.app.models.NotificationItemModel;
import com.manilalinkup.app.models.NotificationsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NotificationUtils {

    // Notification type constants — keep in sync with the Laravel backend
    public static final String TYPE_NEW_APPLICANT        = "new_applicant";
    public static final String TYPE_JOB_EXPIRING         = "job_expiring";
    public static final String TYPE_VERIFIED             = "verified";
    public static final String TYPE_VERIFICATION_REJECTED = "verification_rejected";
    public static final String TYPE_INTERVIEW_OFFER      = "interview_offer";
    public static final String TYPE_HIRED                = "hired";
    public static final String TYPE_REJECTED             = "rejected";
    public static final String TYPE_JOB_FILLED           = "job_filled";
    public static final String TYPE_JOB_COMPLETED        = "job_completed";
    public static final String TYPE_RATING_RECEIVED      = "rating_received";
    public static final String TYPE_NEW_MATCHING_JOB     = "new_matching_job";
    public static final String TYPE_PREFERENCES_NUDGE    = "preferences_nudge";

    public static NotificationsModel toDisplayModel(NotificationItemModel item) {
        int icon = iconForType(item.getType());
        String timestamp = relativeTime(item.getCreatedAt());
        String title = buildTitle(item);
        String description = buildDescription(item);
        String jobId = firstDataValue(item, "jobId", "job_id");
        return new NotificationsModel(icon, title, description, timestamp, item.isRead(), item.getType(), item.getId(), jobId);
    }

    private static String buildTitle(NotificationItemModel item) {
        if (item.getType() == null) return safe(item.getTitle());
        switch (item.getType()) {
            case TYPE_RATING_RECEIVED:
                return "New Rating Notification";
            case TYPE_JOB_EXPIRING:
                return "Expiring Soon";
            case TYPE_JOB_COMPLETED:
                return "You got a new rating!";
            case TYPE_INTERVIEW_OFFER:
                return "Interview Offer";
            case TYPE_HIRED:
                return "You're Hired!";
            case TYPE_JOB_FILLED:
                return "Position Filled";
            case TYPE_NEW_MATCHING_JOB:
                return "New Job Match";
            default:
                return safe(item.getTitle());
        }
    }

    private static String buildDescription(NotificationItemModel item) {
        if (item.getType() == null) return safe(item.getBody());
        switch (item.getType()) {
            case TYPE_NEW_APPLICANT: {
                String name = firstDataValue(item, "seekerName", "seeker_name", "applicantName", "applicant_name");
                String jobTitle = item.getDataValue("jobTitle");
                if (name != null && !name.isEmpty() && jobTitle != null && !jobTitle.isEmpty())
                    return name + " applied for " + jobTitle;
                if (name != null && !name.isEmpty()) return name + " applied";
                return safe(item.getBody());
            }
            case TYPE_RATING_RECEIVED: {
                String name = firstDataValue(item, "raterName", "rater_name", "seekerName", "seeker_name", "applicantName", "applicant_name");
                String jobTitle = item.getDataValue("jobTitle");
                String jobId = firstDataValue(item, "jobId", "job_id");
                String jobRef = (jobTitle != null && !jobTitle.isEmpty()) ? jobTitle : jobId;
                if (name != null && !name.isEmpty() && jobRef != null && !jobRef.isEmpty())
                    return "You received a rating from " + name + " for " + jobRef + ".";
                if (name != null && !name.isEmpty())
                    return "You received a rating from " + name + ".";
                return safe(item.getBody());
            }
            case TYPE_JOB_EXPIRING: {
                String jobTitle = item.getDataValue("jobTitle");
                if (jobTitle != null && !jobTitle.isEmpty()) return jobTitle + " is about to expire.";
                return "Your job is about to expire.";
            }
            case TYPE_JOB_COMPLETED: {
                String name = firstDataValue(item, "seekerName", "seeker_name", "applicantName", "applicant_name");
                String jobTitle = item.getDataValue("jobTitle");
                if (name != null && !name.isEmpty() && jobTitle != null && !jobTitle.isEmpty())
                    return name + " from " + jobTitle + " gave you a rating.";
                if (name != null && !name.isEmpty()) return name + " gave you a rating.";
                return safe(item.getBody());
            }
            case TYPE_INTERVIEW_OFFER: {
                String employer = firstDataValue(item, "employerName", "employer_name");
                String jobTitle = item.getDataValue("jobTitle");
                if (employer != null && !employer.isEmpty() && jobTitle != null && !jobTitle.isEmpty())
                    return employer + " wants to interview you for " + jobTitle + ".";
                if (employer != null && !employer.isEmpty())
                    return employer + " wants to interview you.";
                return safe(item.getBody());
            }
            case TYPE_HIRED: {
                String employer = firstDataValue(item, "employerName", "employer_name");
                String jobTitle = item.getDataValue("jobTitle");
                if (employer != null && !employer.isEmpty() && jobTitle != null && !jobTitle.isEmpty())
                    return "Congratulations! " + employer + " hired you for " + jobTitle + ".";
                if (employer != null && !employer.isEmpty())
                    return "Congratulations! " + employer + " hired you.";
                return safe(item.getBody());
            }
            case TYPE_JOB_FILLED: {
                String jobTitle = item.getDataValue("jobTitle");
                if (jobTitle != null && !jobTitle.isEmpty()) return jobTitle + " has been filled.";
                return safe(item.getBody());
            }
            case TYPE_NEW_MATCHING_JOB: {
                String jobTitle = item.getDataValue("jobTitle");
                if (jobTitle != null && !jobTitle.isEmpty()) return jobTitle + " matches your preferences.";
                return safe(item.getBody());
            }
            default:
                return safe(item.getBody());
        }
    }

    private static String firstDataValue(NotificationItemModel item, String... keys) {
        for (String key : keys) {
            String v = item.getDataValue(key);
            if (v != null && !v.isEmpty()) return v;
        }
        return null;
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }

    public static int iconForType(String type) {
        if (type == null) return R.drawable.notif_icon;
        switch (type) {
            case TYPE_NEW_APPLICANT:
                return R.drawable.people_notif_icon;
            case TYPE_INTERVIEW_OFFER:
            case TYPE_PREFERENCES_NUDGE:
                return R.drawable.schedule_notif_icontwo;
            case TYPE_HIRED:
                return R.drawable.ic_hired_notif;
            case TYPE_RATING_RECEIVED:
                return R.drawable.ic_rating_notif;
            case TYPE_VERIFIED:
            case TYPE_VERIFICATION_REJECTED:
                return R.drawable.ic_verified_notif;
            case TYPE_JOB_FILLED:
                return R.drawable.ic_job_notif;
            case TYPE_JOB_EXPIRING:
                return R.drawable.ic_job_expiring_notif;
            case TYPE_JOB_COMPLETED:
                return R.drawable.ic_job_completed_notif;
            case TYPE_NEW_MATCHING_JOB:
                return R.drawable.people_notif_icon;
            default:
                return R.drawable.notif_icon;
        }
    }

    public static String relativeTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        Date date = tryParse(isoDate, "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        if (date == null) date = tryParse(isoDate, "yyyy-MM-dd'T'HH:mm:ssXXX");
        if (date == null) date = tryParse(isoDate, "yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (date == null) date = tryParse(isoDate, "yyyy-MM-dd HH:mm:ss");
        if (date == null) return isoDate;

        long diff    = System.currentTimeMillis() - date.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours   = TimeUnit.MILLISECONDS.toHours(diff);
        long days    = TimeUnit.MILLISECONDS.toDays(diff);

        if (minutes < 1)  return "Just now";
        if (minutes < 60) return minutes + "m ago";
        if (hours < 24)   return hours + "h ago";
        if (days == 1)    return "Yesterday";
        if (days < 7)     return days + "d ago";
        return new SimpleDateFormat("MMM d", Locale.US).format(date);
    }

    private static Date tryParse(String value, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}
