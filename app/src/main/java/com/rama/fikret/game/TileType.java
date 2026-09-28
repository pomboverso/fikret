package com.rama.fikret.game;

import com.rama.fikret.R;

public enum TileType {
    NONE(0, 0, false),
    GRASS(1, R.drawable.grass, false),
    WATER(2, R.drawable.water, true),
    SAND(3, R.drawable.sand, false),
    SOIL(4, R.drawable.soil, false),
    DEEP_GRASS(5, R.drawable.deep_grass, false),
    DEEP_WATER(6, R.drawable.deep_water, true),
    SNOW(7, R.drawable.snow, false),
    ICE(8, R.drawable.ice, false),
    VOLCANIC_SOIL(9, R.drawable.volcanic_soil, false),
    LAVA(10, R.drawable.magma, true),
    ACID_SOIL(11, R.drawable.acid_soil, false),
    ACID_LAKE(12, R.drawable.acid_lake, true),
    BUBBLEGUM(13, R.drawable.bubblegum, false),
    BUBBLEGUM_LAKE(14, R.drawable.bubblegum_lake, true),
    SPACE_PURPLE_SOIL(15, R.drawable.space_soil, false),
    SPACE_LAKE(16, R.drawable.space_lake, true),
    NIGHTMARE_BLOOD_LAKE(17, R.drawable.blood_lake, true);

    public static final int ATLAS_COLUMNS = 3;
    public static final int ATLAS_ROWS = 3;

    public final int id;
    public final int atlasRes;
    public final boolean liquid;

    TileType(int id, int atlasRes, boolean liquid) {
        this.id = id;
        this.atlasRes = atlasRes;
        this.liquid = liquid;
    }

    public static TileType fromId(int id) {
        for (TileType t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        return NONE;
    }
}
