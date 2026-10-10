package com.rama.fikret.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rama.fikret.R;
import com.rama.fikret.economy.GameState;
import com.rama.fikret.economy.NumberFormatter;
import com.rama.fikret.helpers.SystemBars;
import com.rama.fikret.helpers.ViewUpdates;
import com.rama.fikret.managers.FontManager;

/**
 * Shared shell for the menu screens: back button, title, live money counter and a list.
 *
 * The visible rows are refreshed ten times a second by calling {@link #bindRow} on them directly.
 * The adapter is only notified when the list itself changes (for example after a purchase),
 * because re-laying out a ListView in the middle of a press cancels the press on the buttons.
 */
public abstract class ListScreenActivity extends Activity {
    private static final long TICK_MS = 100;

    protected GameState state;
    protected ListView list;
    protected BaseAdapter adapter;
    protected int world;
    private TextView moneyText;
    private final Handler handler = new Handler();

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            state.sync();
            ViewUpdates.setText(moneyText, NumberFormatter.money(world, state.getMoney(world)));
            refreshRows();
            handler.postDelayed(this, TICK_MS);
        }
    };

    protected abstract String screenTitle();

    protected abstract BaseAdapter createAdapter();

    /** Updates the texts and colours of one row. Must not change the row's size or listeners. */
    protected abstract void bindRow(View row, int position);

    /** Optional line under the header (for example which farm the list belongs to). */
    protected String subtitle() {
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        View root = findViewById(R.id.root);
        SystemBars.applyInsets(root);
        FontManager.apply(root, FontManager.getJersey25(this));

        state = GameState.get(this);
        world = state.getCurrentWorld();
        moneyText = findViewById(R.id.money);
        list = findViewById(R.id.list);

        TextView title = findViewById(R.id.title);
        title.setText(FontManager.sanitizeForFont(screenTitle()));
        TextView subtitle = findViewById(R.id.subtitle);
        String sub = subtitle();
        subtitle.setText(FontManager.sanitizeForFont(sub));
        subtitle.setVisibility(sub.length() == 0 ? View.GONE : View.VISIBLE);

        findViewById(R.id.back).setOnClickListener(v -> finish());

        adapter = createAdapter();
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty));
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(ticker);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
        state.save();
    }

    private void refreshRows() {
        int first = list.getFirstVisiblePosition();
        for (int i = 0; i < list.getChildCount(); i++) {
            bindRow(list.getChildAt(i), first + i);
        }
    }

    protected void applyFont(View row) {
        FontManager.apply(row, FontManager.getJersey25(this));
    }
}
