package com.rama.fikret.game;

/**
 * Everything one stage needs: its tile array (the same array-of-arrays
 * format from GameMap/MapCell), where the goose starts on it, and whether
 * it's blowing a blizzard (see Blizzard/GameView.render()) - the only
 * per-stage weather effect so far, so a plain boolean is enough; a second
 * one would probably want an enum instead.
 */
public class Stage {
    public final int[][] tiles;
    public final int spawnRow;
    public final int spawnCol;
    public final boolean hasBlizzard;

    public Stage(int[][] tiles, int spawnRow, int spawnCol) {
        this(tiles, spawnRow, spawnCol, false);
    }

    public Stage(int[][] tiles, int spawnRow, int spawnCol, boolean hasBlizzard) {
        this.tiles = tiles;
        this.spawnRow = spawnRow;
        this.spawnCol = spawnCol;
        this.hasBlizzard = hasBlizzard;
    }
}
