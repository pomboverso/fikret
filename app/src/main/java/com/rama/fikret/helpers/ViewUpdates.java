package com.rama.fikret.helpers;

import android.view.View;
import android.widget.TextView;

/**
 * Helpers for updating views that live inside a ListView every few milliseconds.
 *
 * Touching a view (new text, new background) can request a layout, and a ListView that lays out
 * its rows in the middle of a press cancels that press. Only writing values that actually changed
 * keeps buttons clickable while the numbers tick.
 */
public final class ViewUpdates {
    private ViewUpdates() {
    }

    public static void setText(TextView view, CharSequence text) {
        if (!view.getText().toString().contentEquals(text)) {
            view.setText(text);
        }
    }


    /** Sets an image only when it differs from the last one set through this helper. */
    public static void setImageResource(android.widget.ImageView view, int resId) {
        if (Integer.valueOf(resId).equals(view.getTag())) {
            return;
        }
        view.setTag(resId);
        view.setImageResource(resId);
    }

    /** Sets a solid background only when it differs from the last one set through this helper. */
    public static void setBackgroundColor(View view, int color) {
        Object last = view.getTag(com.rama.fikret.R.id.last_background);
        if (last instanceof Integer && (Integer) last == color) {
            return;
        }
        view.setTag(com.rama.fikret.R.id.last_background, color);
        view.setBackgroundColor(color);
    }
}
