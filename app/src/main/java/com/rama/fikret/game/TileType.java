package com.rama.fikret.game;

import com.rama.fikret.R;

/**
 * Registry of every tile "biome" a map can be built from.
 *
 * The numeric {@link #id} is the TENS digit of a raw map cell value (see
 * {@link MapCell}). Every tile type owns a 3x3 spritesheet ("atlas") where
 * each of the 9 sub-images corresponds to a numpad position:
 *
 * <pre>
 * 7 8 9
 * 4 5 6
 * 1 2 3
 * </pre>
 *
 * so a single cell can render as a corner, an edge, or the plain center
 * piece of that biome, depending on its position digit.
 *
 * To add a new biome (water, lava, ground, snow...) just add another enum
 * constant here with its own id and drawable - nothing else needs to change,
 * GameView reads this list to know what to load.
 */
public enum TileType {
    GRASS(1, R.drawable.gm_grass, 3, 3);

    public final int id;
    public final int atlasRes;
    public final int atlasColumns;
    public final int atlasRows;

    TileType(int id, int atlasRes, int atlasColumns, int atlasRows) {
        this.id = id;
        this.atlasRes = atlasRes;
        this.atlasColumns = atlasColumns;
        this.atlasRows = atlasRows;
    }

    /** Looks up a tile type by its tens-digit id, falling back to the first
     *  registered type (grass) for 0 or any unknown id, so old/short codes
     *  never crash the renderer. */
    public static TileType fromId(int id) {
        for (TileType t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        return values()[0];
    }
}
