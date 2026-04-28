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
        return new NotificationsModel(
                icon,
                item.getTitle(),
                item.getBody(),
                timestamp,
                item.isRead(),
                item.getType(),
                item.getId()
        );
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
            case TYPE_REJECTED:
            case TYPE_JOB_FILLED:
            case TYPE_JOB_EXPIRING:
            case TYPE_JOB_COMPLETED:
                return R.drawable.ic_job_notif;
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
