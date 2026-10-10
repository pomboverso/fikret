package com.rama.fikret.activities;

import android.widget.BaseAdapter;

/** Placeholder for the views that are not built yet (achievements). */
public class ComingSoonActivity extends ListScreenActivity {
    public static final String EXTRA_TITLE = "title";

    @Override
    protected String screenTitle() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        return title == null ? "" : title;
    }

    @Override
    protected void bindRow(android.view.View row, int position) {
        // static rows, nothing to refresh
    }

    @Override
    protected BaseAdapter createAdapter() {
        // An empty adapter makes the "coming soon" text show up.
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                return convertView;
            }
        };
    }
}
