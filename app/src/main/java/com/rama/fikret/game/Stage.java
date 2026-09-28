package com.rama.fikret.game;

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
