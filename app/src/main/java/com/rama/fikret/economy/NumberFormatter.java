package com.rama.fikret.economy;

import java.util.Locale;

public final class NumberFormatter {
    private NumberFormatter() {
    }

    /**
     * One notation everywhere, no K / M / B names: plain digits below this, powers of ten from here.
     * (1,234 and 999,999 stay as digits; 1,000,000 shows as 1e6, 7.5 * 10^106 as 7.5e106.)
     */
    private static final double SCIENTIFIC_FROM = 1e6;

    /** Amount in the currency of the given world, e.g. "BRL 1.5e6". */
    public static String money(int world, double value) {
        return Worlds.currencyOf(world) + " " + number(value);
    }

    /** 7.5e106 style, for numbers whose English names nobody could read. */
    private static String scientific(double value) {
        String[] parts = String.format(Locale.US, "%.2e", value).split("e");
        return trim(parts[0]) + "e" + Integer.parseInt(parts[1]);
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
        if (Math.round(value) >= SCIENTIFIC_FROM) {
            return scientific(value);
        }
        return String.format(Locale.US, "%,d", Math.round(value));
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
