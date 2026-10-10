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
import com.rama.fikret.helpers.FarmArt;
import com.rama.fikret.helpers.ViewUpdates;
import com.rama.fikret.managers.FontManager;

import java.util.ArrayList;
import java.util.List;

/**
 * One row per manager still available in the current farm: picture, name + description, and the
 * rescue button. Whatever has been bought disappears from the list, like in the upgrades screen.
 *
 * Order: the ten garden managers, the accountants (bought with angels), the discount managers
 * (bought with money) and, last, the guide bird that opens the way to the next world. The
 * accountants and discount managers only exist in the farms that define them.
 */
public class ManagersActivity extends ListScreenActivity {
    private static final int KIND_MANAGER = 0;
    private static final int KIND_GUIDE = 1;
    private static final int KIND_ACCOUNTANT = 2;
    private static final int KIND_DISCOUNT = 3;

    /** Each entry is {kind, garden}; the garden is unused for the guide bird. */
    private final List<int[]> rows = new ArrayList<>();

    @Override
    protected String screenTitle() {
        return getString(R.string.menu_managers);
    }

    @Override
    protected String subtitle() {
        return Worlds.ALL[state.getCurrentWorld()].name;
    }

    private void rebuildRows() {
        rows.clear();
        for (int g = 0; g < Worlds.GARDENS_PER_WORLD; g++) {
            if (!state.hasManager(world, g)) {
                rows.add(new int[]{KIND_MANAGER, g});
            }
        }
        if (state.hasAccountantList(world)) {
            for (int g = 0; g < Worlds.GARDENS_PER_WORLD; g++) {
                if (!state.hasAccountant(world, g)) {
                    rows.add(new int[]{KIND_ACCOUNTANT, g});
                }
            }
        }
        if (state.hasDiscountList(world)) {
            for (int g = 0; g < Worlds.GARDENS_PER_WORLD; g++) {
                if (!state.hasDiscount(world, g)) {
                    rows.add(new int[]{KIND_DISCOUNT, g});
                }
            }
        }
        // The guide bird always closes the list.
        if (!state.hasTravelManager(world)) {
            rows.add(new int[]{KIND_GUIDE, 0});
        }
    }

    @Override
    protected BaseAdapter createAdapter() {
        rebuildRows();
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return rows.size();
            }

            @Override
            public Object getItem(int position) {
                return rows.get(position);
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
                    ((ImageView) row.findViewById(R.id.manager_picture))
                            .setImageDrawable(FarmArt.supervisor(ManagersActivity.this, world));
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
            if (position >= rows.size()) {
                return;
            }
            int kind = rows.get(position)[0];
            int g = rows.get(position)[1];
            boolean hired;
            switch (kind) {
                case KIND_GUIDE:
                    hired = state.hireTravelManager(world);
                    break;
                case KIND_ACCOUNTANT:
                    hired = state.hireAccountant(world, g);
                    break;
                case KIND_DISCOUNT:
                    hired = state.hireDiscountManager(world, g);
                    break;
                default:
                    hired = state.hireManager(world, g);
                    break;
            }
            if (hired) {
                // The bought one leaves the list.
                rebuildRows();
                adapter.notifyDataSetChanged();
            }
        });
    }

    /** Puts the lake coin in front of a cost label (or takes it away), only when that changes. */
    private void setCoin(TextView cost, boolean coin) {
        Object last = cost.getTag(R.id.last_coin);
        if (last instanceof Boolean && (Boolean) last == coin) {
            return;
        }
        cost.setTag(R.id.last_coin, coin);
        if (coin) {
            int size = Math.round(cost.getTextSize());
            cost.setCompoundDrawables(FarmArt.coin(this, Worlds.ANGEL_WORLD, size), null, null, null);
            cost.setCompoundDrawablePadding(size / 4);
        } else {
            cost.setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    protected void bindRow(View row, int position) {
        row.setTag(position);
        if (position >= rows.size()) {
            return;
        }
        int kind = rows.get(position)[0];
        int g = rows.get(position)[1];
        Worlds.WorldDef def = Worlds.ALL[world];

        String name;
        String description;
        String costLabel;
        boolean coin = false;   // lake points are shown with the lake coin in front
        boolean affordable;
        switch (kind) {
            case KIND_GUIDE: {
                double cost = state.travelManagerCost(world);
                name = "Guide Bird";
                description = "Leads you to the next world.";
                costLabel = NumberFormatter.money(world, cost);
                affordable = state.getMoney(world) >= cost;
                break;
            }
            case KIND_ACCOUNTANT: {
                double cost = state.accountantCost(world, g);
                String garden = def.gardens[g].name;
                name = garden + " accountant";
                description = "Makes " + garden + " cost 10% less.";
                costLabel = NumberFormatter.number(cost);
                coin = true;
                affordable = state.getAngels() >= cost;
                break;
            }
            case KIND_DISCOUNT: {
                double cost = state.discountCost(world, g);
                String garden = def.gardens[g].name;
                name = garden + " discount";
                description = "Makes " + garden + " cost 99.999% less.";
                costLabel = NumberFormatter.money(world, cost);
                affordable = state.getMoney(world) >= cost;
                break;
            }
            default: {
                double cost = state.managerCost(world, g);
                String garden = def.gardens[g].name;
                name = garden + " supervisor";
                description = "Harvests your " + garden + " automatically.";
                costLabel = NumberFormatter.money(world, cost);
                affordable = state.getMoney(world) >= cost;
                break;
            }
        }

        ViewUpdates.setText((TextView) row.findViewById(R.id.manager_name), FontManager.sanitizeForFont(name));
        ViewUpdates.setText((TextView) row.findViewById(R.id.manager_description), FontManager.sanitizeForFont(description));
        ViewUpdates.setText((TextView) row.findViewById(R.id.rescue_label), getString(R.string.rescue));
        TextView costView = (TextView) row.findViewById(R.id.rescue_cost);
        ViewUpdates.setText(costView, costLabel);
        setCoin(costView, coin);
        ViewUpdates.setBackgroundColor(row.findViewById(R.id.rescue_button),
                getResources().getColor(affordable ? R.color.accent : R.color.disabled));
    }
}
