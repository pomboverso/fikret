package com.rama.fikret.helpers;

import com.rama.fikret.R;

/** The picture of each garden. Always a colourful fruit, never a lock. */
public final class GardenIcons {
    private GardenIcons() {
    }

    /** Same order as the lake gardens in Worlds: Yuca, Mango, Cajuil, Guayaba, ... Guanabana. */
    private static final int[] LAKE = {
            R.drawable.lake_yuca, R.drawable.lake_mango, R.drawable.lake_cajuil,
            R.drawable.lake_guayaba, R.drawable.lake_carambola, R.drawable.lake_jobo,
            R.drawable.lake_papaya, R.drawable.lake_yautia, R.drawable.lake_mamey,
            R.drawable.lake_guanabana
    };

    /**
     * Drawable of garden g in the given world. Farms without their own art yet reuse the lake
     * fruits as stand-ins; give them their own array here when their vectors exist.
     */
    public static int forGarden(int world, int g) {
        return LAKE[Math.max(0, Math.min(g, LAKE.length - 1))];
    }
}
