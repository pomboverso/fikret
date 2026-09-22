package com.rama.fikret.game;

/**
 * Everything one stage needs: its tile array (the same array-of-arrays
 * format from GameMap/MapCell) and where the goose starts on it.
 */
public class Stage {
    public final int[][] tiles;
    public final int spawnRow;
    public final int spawnCol;

    public Stage(int[][] tiles, int spawnRow, int spawnCol) {
        this.tiles = tiles;
        this.spawnRow = spawnRow;
        this.spawnCol = spawnCol;
    }
}
