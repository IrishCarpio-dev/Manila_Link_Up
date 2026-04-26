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
    public static final String TYPE_CHAT_MESSAGE          = "chat_message";
    public static final String TYPE_NEW_APPLICANT         = "new_applicant";
    public static final String TYPE_APPLICATION_REVIEW    = "application_review";
    public static final String TYPE_INTERVIEW_SCHEDULED   = "interview_scheduled";
    public static final String TYPE_HIRED                 = "hired";
    public static final String TYPE_APPLICATION_REJECTED  = "application_rejected";
    public static final String TYPE_JOB_CLOSED            = "job_closed";
    public static final String TYPE_JOB_EXPIRED           = "job_expired";
    public static final String TYPE_RATING_RECEIVED       = "rating_received";
    public static final String TYPE_ID_APPROVED           = "id_approved";
    public static final String TYPE_PROFILE_INCOMPLETE    = "profile_incomplete";
    public static final String TYPE_PREFERENCES_INCOMPLETE = "preferences_incomplete";

    public static NotificationsModel toDisplayModel(NotificationItemModel item) {
        int icon = iconForType(item.getType());
        String timestamp = relativeTime(item.getCreatedAt());
        return new NotificationsModel(
                icon,
                item.getTitle(),
                item.getMessage(),
                timestamp,
                item.isRead(),
                item.getType(),
                item.getId()
        );
    }

    public static int iconForType(String type) {
        if (type == null) return R.drawable.notif_icon;
        switch (type) {
            case TYPE_CHAT_MESSAGE:
                return R.drawable.chat_notif_icon;
            case TYPE_NEW_APPLICANT:
            case TYPE_APPLICATION_REVIEW:
            case TYPE_PROFILE_INCOMPLETE:
                return R.drawable.people_notif_icon;
            case TYPE_INTERVIEW_SCHEDULED:
            case TYPE_PREFERENCES_INCOMPLETE:
                return R.drawable.schedule_notif_icontwo;
            case TYPE_HIRED:
                return R.drawable.ic_hired_notif;
            case TYPE_RATING_RECEIVED:
                return R.drawable.ic_rating_notif;
            case TYPE_ID_APPROVED:
                return R.drawable.ic_verified_notif;
            case TYPE_APPLICATION_REJECTED:
            case TYPE_JOB_CLOSED:
            case TYPE_JOB_EXPIRED:
                return R.drawable.ic_job_notif;
            default:
                return R.drawable.notif_icon;
        }
    }

    public static String relativeTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        Date date = tryParse(isoDate, "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
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
