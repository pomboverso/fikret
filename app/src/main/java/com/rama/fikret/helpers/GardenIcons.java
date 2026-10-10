package com.rama.fikret.helpers;

import com.rama.fikret.R;

public final class GardenIcons {
    private GardenIcons() {
    }

    private static final int[] LAKE = {
            R.drawable.lake_yuca, R.drawable.lake_mango, R.drawable.lake_cajuil,
            R.drawable.lake_guayaba, R.drawable.lake_carambola, R.drawable.lake_jobo,
            R.drawable.lake_papaya, R.drawable.lake_yautia, R.drawable.lake_mamey,
            R.drawable.lake_guanabana
    };

    public static int forGarden(int world, int g) {
        return LAKE[Math.max(0, Math.min(g, LAKE.length - 1))];
    }
}
