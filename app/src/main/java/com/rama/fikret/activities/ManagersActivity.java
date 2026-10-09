package com.rama.fikret.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rama.fikret.R;
import com.rama.fikret.economy.NumberFormatter;
import com.rama.fikret.economy.Worlds;
import com.rama.fikret.game.BirdIcon;
import com.rama.fikret.helpers.ViewUpdates;
import com.rama.fikret.managers.FontManager;

/**
 * One row per manager of the current farm: picture, name + description, and the rescue button.
 * Rows 1-10 are the garden managers; row 11 is the one that opens the way to the next world.
 */
public class ManagersActivity extends ListScreenActivity {
    private int world;

    @Override
    protected String screenTitle() {
        return getString(R.string.menu_managers);
    }

    @Override
    protected String subtitle() {
        return Worlds.ALL[state.getCurrentWorld()].name;
    }

    @Override
    protected BaseAdapter createAdapter() {
        world = state.getCurrentWorld();
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return Worlds.GARDENS_PER_WORLD + 1;
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
                    row = LayoutInflater.from(ManagersActivity.this).inflate(R.layout.list_item_manager, parent, false);
                    applyFont(row);
                    ((ImageView) row.findViewById(R.id.manager_picture)).setImageDrawable(BirdIcon.drawable(getResources()));
                    wireListener(row);
                }
                bindRow(row, position);
                return row;
            }
        };
    }

    /** Set once per row view; the position is read from the row's tag at click time. */
    private void wireListener(final View row) {
        row.findViewById(R.id.rescue_button).setOnClickListener(v -> {
            int position = (Integer) row.getTag();
            boolean hired = position == Worlds.GARDENS_PER_WORLD
                    ? state.hireTravelManager(world)
                    : state.hireManager(world, position);
            if (hired) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void bindRow(View row, int position) {
        row.setTag(position);
        Worlds.WorldDef def = Worlds.ALL[world];
        boolean travel = position == Worlds.GARDENS_PER_WORLD;

        String name;
        String description;
        boolean hired;
        double cost;
        if (travel) {
            name = "Guide Bird";
            description = "Leads you to the next world.";
            hired = state.hasTravelManager(world);
            cost = state.travelManagerCost(world);
        } else {
            String garden = def.gardens[position].name;
            name = garden + " supervisor";
            description = "Harvests your " + garden + " automatically.";
            hired = state.hasManager(world, position);
            cost = state.managerCost(world, position);
        }

        ViewUpdates.setText((TextView) row.findViewById(R.id.manager_name), FontManager.sanitizeForFont(name));
        ViewUpdates.setText((TextView) row.findViewById(R.id.manager_description), FontManager.sanitizeForFont(description));

        View button = row.findViewById(R.id.rescue_button);
        TextView label = row.findViewById(R.id.rescue_label);
        TextView costText = row.findViewById(R.id.rescue_cost);

        if (hired) {
            ViewUpdates.setText(label, getString(R.string.rescued));
            ViewUpdates.setText(costText, "");
            ViewUpdates.setBackgroundColor(button, getResources().getColor(R.color.surface_1));
            return;
        }

        boolean affordable = state.getMoney() >= cost;
        ViewUpdates.setText(label, getString(R.string.rescue));
        ViewUpdates.setText(costText, NumberFormatter.money(cost));
        ViewUpdates.setBackgroundColor(button,
                getResources().getColor(affordable ? R.color.accent : R.color.disabled));
    }
}
