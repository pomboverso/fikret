package com.rama.fikret.game;

public final class TilePosition {
    private TilePosition() {
    }

    public static int col(int position) {
        return (position - 1) % 3;
    }

    public static int row(int position) {
        return 2 - ((position - 1) / 3);
    }
}
