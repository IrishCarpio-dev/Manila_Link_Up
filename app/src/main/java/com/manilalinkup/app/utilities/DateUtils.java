package com.manilalinkup.app.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static String formatChatTimestamp(String raw) {
        if (raw == null || raw.isEmpty()) return "";

        SimpleDateFormat[] parsers = {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
        };

        Date date = null;
        for (SimpleDateFormat parser : parsers) {
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = parser.parse(raw);
                break;
            } catch (ParseException ignored) {}
        }

        if (date == null) return raw;

        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTime(date);

        boolean sameDay = now.get(Calendar.YEAR) == then.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR);

        if (sameDay) {
            SimpleDateFormat fmt = new SimpleDateFormat("h:mm a", Locale.US);
            return fmt.format(date);
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        boolean isYesterday = yesterday.get(Calendar.YEAR) == then.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR);

        if (isYesterday) return "Yesterday";

        if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
            return new SimpleDateFormat("MMM d", Locale.US).format(date);
        }

        return new SimpleDateFormat("MMM d, yyyy", Locale.US).format(date);
    }
}
