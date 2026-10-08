package com.rama.fikret.economy;

import java.util.Locale;

public final class NumberFormatter {
    private NumberFormatter() {
    }

    private static final String[] SUFFIXES = {
            "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc"
    };

    public static String money(double value) {
        return Worlds.CURRENCY + number(value);
    }

    public static String number(double value) {
        if (Double.isNaN(value) || value < 0) {
            value = 0;
        }
        if (Double.isInfinite(value)) {
            return "inf";
        }
        if (value < 1000) {
            if (value < 10) {
                return trim(String.format(Locale.US, "%.2f", value));
            }
            if (value < 100) {
                return trim(String.format(Locale.US, "%.1f", value));
            }
            return String.format(Locale.US, "%.0f", value);
        }
        int tier = (int) Math.floor(Math.log10(value) / 3);
        if (tier >= SUFFIXES.length) {
            return String.format(Locale.US, "%.2e", value);
        }
        double scaled = value / Math.pow(1000, tier);
        return trim(String.format(Locale.US, "%.2f", scaled)) + SUFFIXES[tier];
    }

    private static String trim(String s) {
        if (s.indexOf('.') < 0) {
            return s;
        }
        int end = s.length();
        while (end > 0 && s.charAt(end - 1) == '0') {
            end--;
        }
        if (end > 0 && s.charAt(end - 1) == '.') {
            end--;
        }
        return s.substring(0, end);
    }

    /** h:mm:ss, or m:ss, or 0.0s for very short times. */
    public static String duration(double seconds) {
        if (seconds < 0) {
            seconds = 0;
        }
        if (seconds < 10) {
            return String.format(Locale.US, "%.1fs", seconds);
        }
        long total = (long) Math.ceil(seconds);
        long h = total / 3600;
        long m = (total % 3600) / 60;
        long s = total % 60;
        if (h > 0) {
            return String.format(Locale.US, "%d:%02d:%02d", h, m, s);
        }
        return String.format(Locale.US, "%d:%02d", m, s);
    }
}
