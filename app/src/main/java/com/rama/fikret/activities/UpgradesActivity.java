package com.rama.fikret.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rama.fikret.R;
import com.rama.fikret.economy.GameState;
import com.rama.fikret.economy.NumberFormatter;
import com.rama.fikret.economy.Worlds;
import com.rama.fikret.managers.FontManager;

/** The gardens of the current farm with the two things you can improve: speed and value. */
public class UpgradesActivity extends ListScreenActivity {
    private int world;

    @Override
    protected String screenTitle() {
        return getString(R.string.menu_upgrades);
    }

    @Override
    protected String subtitle() {
        Worlds.WorldDef def = Worlds.ALL[state.getCurrentWorld()];
        return def.name + " - " + def.country;
    }

    @Override
    protected BaseAdapter createAdapter() {
        world = state.getCurrentWorld();
        return new BaseAdapter() {
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
                    row = LayoutInflater.from(UpgradesActivity.this).inflate(R.layout.list_item_upgrade, parent, false);
                    applyFont(row);
                }
                bind(row, position);
                return row;
            }
        };
    }

    private void bind(View row, final int g) {
        String name = Worlds.ALL[world].gardens[g].name;
        int count = state.getCount(world, g);
        ((TextView) row.findViewById(R.id.upgrade_name)).setText(
                FontManager.sanitizeForFont(name + (count > 0 ? "  x" + count : "  (locked)")));
        ((TextView) row.findViewById(R.id.upgrade_stats)).setText(
                "Cycle " + NumberFormatter.duration(state.cycleSeconds(world, g))
                        + "  -  Harvest " + NumberFormatter.money(state.revenuePerCycle(world, g)));

        bindButton(row, R.id.speed_button, R.id.speed_label, R.id.speed_cost, "Speed", g, GameState.UPGRADE_SPEED, count);
        bindButton(row, R.id.value_button, R.id.value_label, R.id.value_cost, "Value", g, GameState.UPGRADE_VALUE, count);
    }

    private void bindButton(View row, int buttonId, int labelId, int costId, String title,
                            final int g, final int type, int count) {
        View button = row.findViewById(buttonId);
        TextView label = row.findViewById(labelId);
        TextView cost = row.findViewById(costId);

        int level = state.upgradeLevel(world, g, type);
        label.setText(title + " x2  (" + level + "/" + Worlds.UPGRADE_MAX_LEVEL + ")");

        if (state.upgradeMaxed(world, g, type)) {
            cost.setText("MAX");
            button.setBackgroundColor(getResources().getColor(R.color.surface_1));
            button.setClickable(false);
            button.setOnClickListener(null);
            return;
        }

        double price = state.upgradeCost(world, g, type);
        boolean affordable = count > 0 && state.getMoney() >= price;
        cost.setText(NumberFormatter.money(price));
        button.setBackgroundColor(getResources().getColor(affordable ? R.color.accent : R.color.surface_0));
        button.setClickable(affordable);
        button.setOnClickListener(affordable ? v -> {
            state.buyUpgrade(world, g, type);
            adapter.notifyDataSetChanged();
        } : null);
    }
}
