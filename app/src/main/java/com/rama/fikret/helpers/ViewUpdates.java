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

    public static int themeColor(android.content.Context context, int attrOrColorRes) {
        android.util.TypedValue tv = new android.util.TypedValue();
        if (context.getTheme().resolveAttribute(attrOrColorRes, tv, true)
                && tv.type >= android.util.TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= android.util.TypedValue.TYPE_LAST_COLOR_INT) {
            return tv.data;
        }
        return color(context, attrOrColorRes);
    }

    @SuppressWarnings("deprecation")
    public static int color(android.content.Context context, int colorRes) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            return context.getResources().getColor(colorRes, context.getTheme());
        }
        return context.getResources().getColor(colorRes);
    }
}
