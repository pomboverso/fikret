package com.rama.fikret.game;

import com.rama.fikret.R;

/**
 * Registry of every tile "biome" a map can be built from - matches the
 * "Tiles" list 1:1 by id.
 *
 * Every real tile owns a 3x3 spritesheet ("atlas") where each of the 9
 * sub-images corresponds to a numpad position:
 *
 * <pre>
 * 7 8 9
 * 4 5 6
 * 1 2 3
 * </pre>
 *
 * so a single layer can render as a corner, an edge, or the plain center
 * piece of that biome, depending on its position digit (see MapCell).
 *
 * NONE (id 0) is not a real biome - it means "nothing drawn here", used
 * when a cell has no background layer (see MapCell.hasBackground()).
 *
 * To add a new biome, add a constant here with its own id/drawable -
 * nothing else needs to change, GameView reads this list to know what to
 * load.
 */
public enum TileType {
    NONE(0, 0),
    GRASS(1, R.drawable.grass),
    WATER(2, R.drawable.water),
    SAND(3, R.drawable.sand),
    SOIL(4, R.drawable.soil),
    DEEP_GRASS(5, R.drawable.deep_grass),
    DEEP_WATER(6, R.drawable.deep_water),
    SNOW(7, R.drawable.snow),
    ICE(8, R.drawable.ice),
    VOLCANIC_SOIL(9, R.drawable.volcanic_soil),
    LAVA(10, R.drawable.magma),
    ACID_SOIL(11, R.drawable.acid_soil),
    ACID_LAKE(12, R.drawable.acid_lake),
    BUBBLEGUM(13, R.drawable.bubblegum),
    BUBBLEGUM_LAKE(14, R.drawable.bubblegum_lake),
    SPACE_PURPLE_SOIL(15, R.drawable.space_soil),
    SPACE_LAKE(16, R.drawable.space_lake),
    NIGHTMARE_BLOOD_LAKE(17, R.drawable.blood_lake);

    /** Every real tile atlas is a 3x3 grid (see class doc). */
    public static final int ATLAS_COLUMNS = 3;
    public static final int ATLAS_ROWS = 3;

    public final int id;
    public final int atlasRes; // 0 for NONE - never actually loaded

    TileType(int id, int atlasRes) {
        this.id = id;
        this.atlasRes = atlasRes;
    }

    /** Looks up a tile type by its 2-digit id, falling back to NONE for 0
     *  or any unrecognized id, so malformed codes never crash the renderer. */
    public static TileType fromId(int id) {
        for (TileType t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        return NONE;
    }
}
