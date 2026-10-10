package com.rama.fikret.game;

import java.util.ArrayList;
import java.util.List;

public class Stage {
    public final int[][] tiles;
    public final int spawnRow;
    public final int spawnCol;
    public final boolean hasBlizzard;
    public final List<SonarReveal> sonarReveals = new ArrayList<>();

    public Stage(int[][] tiles, int spawnRow, int spawnCol) {
        this(tiles, spawnRow, spawnCol, false);
    }

    public Stage(int[][] tiles, int spawnRow, int spawnCol, boolean hasBlizzard) {
        this.tiles = tiles;
        this.spawnRow = spawnRow;
        this.spawnCol = spawnCol;
        this.hasBlizzard = hasBlizzard;
    }

    public Stage withSonarReveal(SonarReveal reveal) {
        sonarReveals.add(reveal);
        return this;
    }
}
