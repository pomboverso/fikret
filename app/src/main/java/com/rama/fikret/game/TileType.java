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
 * To add a new biome, add a constant here with its own id/drawable (and
 * `liquid` = true if it should be swum in) - nothing else needs to change,
 * GameView reads this list to know what to load.
 */
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

    /** Every real tile atlas is a 3x3 grid (see class doc). */
    public static final int ATLAS_COLUMNS = 3;
    public static final int ATLAS_ROWS = 3;

    public final int id;
    public final int atlasRes; // 0 for NONE - never actually loaded
    /** True for tiles a creature swims in rather than walks on: the goose
     *  (and the birds) switch to their swimming pose while standing on one.
     *  Any position of the atlas counts - even the shoreline edge/corner
     *  pieces are almost entirely liquid. See MapCell.isLiquid(). */
    public final boolean liquid;

    TileType(int id, int atlasRes, boolean liquid) {
        this.id = id;
        this.atlasRes = atlasRes;
        this.liquid = liquid;
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
