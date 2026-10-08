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
        Worlds.WorldDef def = Worlds.ALL[state.getCurrentWorld()];
        return def.name;
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
                }
                bind(row, position);
                return row;
            }
        };
    }

    private void bind(View row, final int position) {
        Worlds.WorldDef def = Worlds.ALL[world];
        boolean travel = position == Worlds.GARDENS_PER_WORLD;

        String name;
        String description;
        boolean hired;
        double cost;
        if (travel) {
            name = "Guide Bird";
            description = "Your Lead you the next world.";
            hired = state.hasTravelManager(world);
            cost = state.travelManagerCost(world);
        } else {
            String garden = def.gardens[position].name;
            name = garden + " supervisor";
            description = "Harvests your " + garden + " automatically.";
            hired = state.hasManager(world, position);
            cost = state.managerCost(world, position);
        }

        ((TextView) row.findViewById(R.id.manager_name)).setText(FontManager.sanitizeForFont(name));
        ((TextView) row.findViewById(R.id.manager_description)).setText(FontManager.sanitizeForFont(description));

        View button = row.findViewById(R.id.rescue_button);
        TextView label = row.findViewById(R.id.rescue_label);
        TextView costText = row.findViewById(R.id.rescue_cost);

        if (hired) {
            label.setText(R.string.rescued);
            costText.setText("");
            button.setBackgroundColor(getResources().getColor(R.color.surface_1));
            button.setOnClickListener(null);
            button.setClickable(false);
            return;
        }

        boolean affordable = state.getMoney() >= cost;
        label.setText(R.string.rescue);
        costText.setText(NumberFormatter.money(cost));
        button.setBackgroundColor(getResources().getColor(affordable ? R.color.accent : R.color.surface_0));
        button.setClickable(affordable);
        final boolean isTravel = travel;
        button.setOnClickListener(affordable ? v -> {
            if (isTravel) {
                state.hireTravelManager(world);
            } else {
                state.hireManager(world, position);
            }
            adapter.notifyDataSetChanged();
        } : null);
    }
}
