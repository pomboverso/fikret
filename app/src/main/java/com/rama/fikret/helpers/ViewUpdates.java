package com.rama.fikret.helpers;

import android.view.View;
import android.widget.TextView;

public final class ViewUpdates {
    private ViewUpdates() {
    }

    public static void setText(TextView view, CharSequence text) {
        if (!view.getText().toString().contentEquals(text)) {
            view.setText(text);
        }
    }

    public static void setImageResource(android.widget.ImageView view, int resId) {
        if (Integer.valueOf(resId).equals(view.getTag())) {
            return;
        }
        view.setTag(resId);
        view.setImageResource(resId);
    }

    public static void setBackgroundColor(View view, int color) {
        Object last = view.getTag(com.rama.fikret.R.id.last_background);
        if (last instanceof Integer && (Integer) last == color) {
            return;
        }
        view.setTag(com.rama.fikret.R.id.last_background, color);
        view.setBackgroundColor(color);
    }
}
