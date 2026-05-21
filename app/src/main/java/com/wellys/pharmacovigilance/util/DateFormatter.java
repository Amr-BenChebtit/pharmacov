package com.wellys.pharmacovigilance.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    private static final long MINUTE = 60_000L;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    /** "Il y a 2 jours", "Il y a 3 h", etc. French only — fine for the demo. */
    public static String relative(long timestamp) {
        long now = System.currentTimeMillis();
        long delta = now - timestamp;

        if (delta < MINUTE) return "À l'instant";
        if (delta < HOUR) return "Il y a " + (delta / MINUTE) + " min";
        if (delta < DAY) return "Il y a " + (delta / HOUR) + " h";
        long days = delta / DAY;
        if (days == 1) return "Hier";
        if (days < 30) return "Il y a " + days + " jours";
        return absolute(timestamp);
    }

    /** "21 mai 2026" */
    public static String absolute(long timestamp) {
        return new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH).format(new Date(timestamp));
    }

    /** "21/05/2026 14:32" */
    public static String absoluteWithTime(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(new Date(timestamp));
    }

    private DateFormatter() {}
}
