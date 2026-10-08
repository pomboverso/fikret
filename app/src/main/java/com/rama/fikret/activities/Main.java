package com.rama.fikret.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rama.fikret.R;
import com.rama.fikret.economy.GameState;
import com.rama.fikret.economy.NumberFormatter;
import com.rama.fikret.economy.Worlds;
import com.rama.fikret.game.Ability;
import com.rama.fikret.helpers.SystemBars;
import com.rama.fikret.managers.FontManager;
import com.rama.fikret.managers.PrefsManager;

/**
 * Home screen: the gardens of the farm you are currently in.
 */
public class Main extends Activity {
    private static final long TICK_MS = 100;
    private static final int[] MULTIPLIERS = {1, 10, 100, GameState.BUY_MAX};

    private static int multiplierIndex = 0;

    private GameState state;
    private TextView moneyText;
    private TextView worldName;
    private Button multiplierButton;
    private BaseAdapter adapter;
    private int world = -1;
    private final Handler handler = new Handler();

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            state.sync();
            refresh();
            handler.postDelayed(this, TICK_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View root = findViewById(R.id.root);
        SystemBars.applyInsets(root);
        FontManager.apply(root, FontManager.getJersey25(this));

        state = GameState.get(this);
        moneyText = findViewById(R.id.money);
        worldName = findViewById(R.id.world_name);
        multiplierButton = findViewById(R.id.multiplier);

        findViewById(R.id.menu).setOnClickListener(v -> showMenu());
        multiplierButton.setOnClickListener(v -> {
            multiplierIndex = (multiplierIndex + 1) % MULTIPLIERS.length;
            refresh();
        });

        adapter = new GardenAdapter();
        ListView list = findViewById(R.id.garden_list);
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        state.sync();
        handler.post(ticker);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
        state.save();
    }

    private void refresh() {
        world = state.getCurrentWorld();
        Worlds.WorldDef def = Worlds.ALL[world];
        worldName.setText(FontManager.sanitizeForFont(def.name));
        moneyText.setText(NumberFormatter.money(state.getMoney()));
        multiplierButton.setText(multiplierLabel());
        adapter.notifyDataSetChanged();
    }

    private static String multiplierLabel() {
        int m = MULTIPLIERS[multiplierIndex];
        return m == GameState.BUY_MAX ? "Max" : "x" + m;
    }

    // ---- Menu ---------------------------------------------------------------------------------

    private void showMenu() {
        final Dialog dialog = new Dialog(this, R.style.AppDialog);
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_menu, null);
        FontManager.apply(content, FontManager.getJersey25(this));
        dialog.setContentView(content);

        bindMenuItem(dialog, content, R.id.menu_map, new Intent(this, GameActivity.class));
        bindMenuItem(dialog, content, R.id.menu_managers, new Intent(this, ManagersActivity.class));
        bindMenuItem(dialog, content, R.id.menu_upgrades, new Intent(this, UpgradesActivity.class));
        bindMenuItem(dialog, content, R.id.menu_ascent, comingSoon(R.string.menu_ascent));
        bindMenuItem(dialog, content, R.id.menu_achievements, comingSoon(R.string.menu_achievements));
        bindMenuItem(dialog, content, R.id.menu_teleport, new Intent(this, TeleportActivity.class));

        // Teleport is a debug aid for now; later it only opens up once the nightmare world is reached.
        boolean teleportAvailable = TELEPORT_ALWAYS_AVAILABLE
                || PrefsManager.getInstance(this).hasAbility(Ability.TELEPORT_HOME);
        int visibility = teleportAvailable ? View.VISIBLE : View.GONE;
        content.findViewById(R.id.menu_teleport).setVisibility(visibility);
        content.findViewById(R.id.menu_teleport_gap).setVisibility(visibility);

        dialog.show();
    }

    private static final boolean TELEPORT_ALWAYS_AVAILABLE = true;

    private Intent comingSoon(int titleRes) {
        return new Intent(this, ComingSoonActivity.class)
                .putExtra(ComingSoonActivity.EXTRA_TITLE, getString(titleRes));
    }

    private void bindMenuItem(final Dialog dialog, View content, int id, final Intent target) {
        content.findViewById(id).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(target);
        });
    }

    // ---- Gardens list -------------------------------------------------------------------------

    private final class GardenAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Worlds.GARDENS_PER_WORLD;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = LayoutInflater.from(Main.this).inflate(R.layout.list_item_garden, parent, false);
                FontManager.apply(row, FontManager.getJersey25(Main.this));
            }
            bind(row, position);
            return row;
        }

        private void bind(View row, final int g) {
            final int w = world < 0 ? state.getCurrentWorld() : world;
            int count = state.getCount(w, g);
            boolean unlocked = state.isUnlocked(w, g);

            ((ImageView) row.findViewById(R.id.garden_icon))
                    .setImageResource(unlocked ? R.drawable.px_lock_open : R.drawable.px_lock);
            ((TextView) row.findViewById(R.id.garden_count))
                    .setText(count + " / " + GameState.nextMilestone(count));

            FrameLayout harvest_btn = row.findViewById(R.id.harvest_btn);
            ProgressBar bar = row.findViewById(R.id.progress_bar);
            bar.setProgress((int) Math.round(state.progress(w, g) * 1000));
            ((TextView) row.findViewById(R.id.product_value)).setText(
                    count > 0 ? NumberFormatter.money(state.revenuePerCycle(w, g)) : "Locked");
            ((TextView) row.findViewById(R.id.duration)).setText(
                    state.isRunning(w, g) && state.cycleSeconds(w, g) >= 0.25
                            ? NumberFormatter.duration(state.timeLeft(w, g)) : NumberFormatter.duration(state.cycleSeconds(w, g)));

            int multiplier = MULTIPLIERS[multiplierIndex];
            GameState.Quote quote = state.quote(w, g, multiplier);
            boolean affordable = unlocked && quote.amount > 0 && quote.cost <= state.getMoney();
            String label = multiplier == GameState.BUY_MAX
                    ? "Buy Max" + (quote.amount > 0 ? " (" + quote.amount + ")" : "")
                    : "Buy x" + multiplier;
            ((TextView) row.findViewById(R.id.buy_label)).setText(label);
            ((TextView) row.findViewById(R.id.buy_cost)).setText(NumberFormatter.money(quote.cost));

            View buy = row.findViewById(R.id.buy_button);
            buy.setBackgroundColor(getResources().getColor(affordable ? R.color.accent : R.color.disabled));
            buy.setOnClickListener(v -> {
                if (state.buy(w, g, MULTIPLIERS[multiplierIndex])) {
                    refresh();
                }
            });

            harvest_btn.setOnClickListener(v -> {
                if (state.harvest(w, g)) {
                    refresh();
                }
            });
        }
    }
}
